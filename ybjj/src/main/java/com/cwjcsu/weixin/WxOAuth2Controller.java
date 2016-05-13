/*
 --------------------------------------
  Skybility
 ---------------------------------------
  Copyright By Skybility ,All right Reserved
 * author                     date    comment
 * chenweijun@skybility.com  2015/8/17  Created
 */
package com.cwjcsu.weixin;


import com.cwjcsu.common.service.PropertyService;
import com.cwjcsu.common.service.ServiceSupport;
import com.cwjcsu.common.util.HttpUtil;
import com.cwjcsu.common.util.SpringContextUtil;
import com.cwjcsu.ybjj.constant.SessionConstant;
import com.cwjcsu.ybjj.domain.OpenAccount;
import com.cwjcsu.ybjj.domain.User;
import com.cwjcsu.ybjj.domain.UserSession;
import com.cwjcsu.ybjj.domain.enums.OauthType;
import com.cwjcsu.ybjj.mapper.UserMapper;
import com.cwjcsu.ybjj.service.AuthService;
import com.cwjcsu.ybjj.service.OpenAccountService;
import com.cwjcsu.ybjj.service.UserService;
import com.cwjcsu.ybjj.service.UserSessionService;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author Atlas
 */
@Controller
public class WxOAuth2Controller extends ServiceSupport {

    public static Logger log = LoggerFactory.getLogger(WxOAuth2Controller.class);

    public static final String SESSION_KEY_WX_USER = "wxUser";
    //public static final String SESSION_KEY_USER = "user";

    public static final String SESSION_KEY_OPENID = "openid";

    @Autowired
    private PropertyService propertyService;

    //微信绑定行云服务账号的view
    private String notBindView = "m/not_bind";

    private String errorView = "m/error";

    private String helpView = "m/help";

    @Autowired
    private WeixinService weixinService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private UserSessionService userSessionService;

    @Autowired
    private OpenAccountService openAccountService;

    @Autowired
    private AuthService authService;

    /**
     * 固定state对应的处理器
     */
    private Map<String, StateInvoker> stateInvokers = new HashMap<String, StateInvoker>();

    /**
     * 包含模版的state处理器
     */
    private List<StateInvoker> templateInvokers = new ArrayList<StateInvoker>();

    @Override
    protected void doStart() throws Exception {
        if (!isWeixinEnabled()) {
            return;
        }
        Map<String, Object> beans = SpringContextUtil.getApplicationContext()
                .getBeansOfType(Object.class);
        for (Map.Entry<String, Object> entry : beans.entrySet()) {
            Object controller = entry.getValue();
            if (this == controller) {
                continue;
            }
            for (Method m : controller.getClass().getMethods()) {
                if (m.isAnnotationPresent(StateMapping.class)) {
                    StateMapping stateMapping = m.getAnnotation(StateMapping.class);
                    for (String state : stateMapping.state()) {
//                        /*if (!Const.actions.containsKey(state)) {
//                            throw new Exception("Action not found:" + state);
//                        }*/
//                        ActionInvoker mOld = actionInvokers.get(state);
//                        if (mOld != null) {
//                            throw new Exception(_.$("action {} mapped to duplicated method {} and {}", state, mOld.getMethod(), m));
//                        }
//                        if (!String.class.equals(m.getReturnType())) {
//                            throw new Exception("ActionMapping method must return an String type");
//                        }
//                        if (m.getParameterTypes().length != 1 || m.getParameterTypes()[0] != OAuth2Context.class) {
//                            throw new Exception("ActionMapping method must have exactly 1 parameter of type " + OAuth2Context.class);
//                        }
                        StateInvoker invoker = new StateInvoker(controller, m, state, stateMapping);
                        if (invoker.isTemplate()) {
                            templateInvokers.add(invoker);
                        } else {
                            stateInvokers.put(state, invoker);
                        }
                    }
                }
            }
        }

        Collections.sort(templateInvokers, new Comparator<StateInvoker>() {
            @Override
            public int compare(StateInvoker o1, StateInvoker o2) {
                return o1.getState().compareTo(o2.getState());
            }
        });
    }

    @Override
    protected void doStop() throws Exception {

    }

    private boolean isWeixinEnabled() {
//        return Boolean.TRUE.equals(propertyService.getAsBoolean("weixin.enabled"));
        return true;
    }

    @RequestMapping(value = "/wxoauth2", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView doOAuth2(
            @RequestParam(value = "code", required = false) String code,
            @RequestParam(value = "state", required = false) String state,
            ModelMap model,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        long t1 = System.currentTimeMillis();
        if (!isWeixinEnabled()) {
            model.addAttribute("message", "微信相关功能未启用");
            ModelAndView v = new ModelAndView(errorView);
            v.addAllObjects(model);
            return v;
        }
        if (StringUtils.isEmpty(code)) {
            // 用户点击拒绝，导入到一个页面
            return new ModelAndView(helpView);
        }
        WxMpUser wxMpUser = null;
        try {
            log.info("OAuth2 request state={},code={},remoteAddr={}", new Object[]{state, code, HttpUtil.getRemoteIP(request)});
            wxMpUser = weixinService.getWxMpUserByCode(code);
            log.info("query weixin user cost: {}ms", new Object[]{System.currentTimeMillis()-t1});
        } catch (Exception e) {
            log.error("OAuth2 获取用户失败 state={},code={}", new Object[]{state, code, e});
            model.addAttribute("message", "获取微信授权信息失败，请重新点击菜单或链接");
            ModelAndView v = new ModelAndView(errorView);
            v.addAllObjects(model);
            return v;
        }

        OAuth2Context context = new OAuth2Context();
        context.setModelMap(model);
        context.setWxMpUser(wxMpUser);
        context.setRequest(request);
        context.setResponse(response);
        context.setState(state);
        ModelAndView v = dispatch(context);
        log.info("OAuth2 cost: {}", new Object[]{System.currentTimeMillis()-t1});
        return v;
    }

    public ModelAndView dispatch(OAuth2Context context) {
        try {
            context.getRequest().setAttribute("state", context.getState());
            StateInvoker invoker = stateInvokers.get(context.getState());
            if (invoker == null) {
                for (StateInvoker ai : templateInvokers) {
                    Map<String, String> kv = ai.match(context.getState());
                    if (kv != null) {// found match
                        context.setStateParameters(kv);
                        invoker = ai;
                        break;
                    }
                }
            }
            //将openId放入session中以便后面页面用到
            context.getRequest().getSession(true).setAttribute(SESSION_KEY_OPENID, context.getOpenId());
            //user是绑定的行云网用户
            User user = context.getUser();
            if (user == null) {
                user = userService.findUserByOpenID(context.getOpenId(), OauthType.WEIXIN.getId());
                context.setUser(user);
            }
            if (invoker == null) {
                ModelAndView v = new ModelAndView("m/not_implemented");
                v.addAllObjects(context.getModelMap());
                return v;
            }
            context.setStateInvoker(invoker);
            if (invoker.requireBind()) {//需要绑定行云网
                if (user == null) {
                    ModelAndView v = new ModelAndView();
                    WxMpUser wxMpUser = context.getWxMpUser();
                    if(wxMpUser.getNickname()==null) {
                        v.setViewName("redirect:"+weixinService.getOAuth2AuthorizationUrl(WeixinService.OAuth2Scope.snsapi_userinfo,context.getState()));
                        return v;
                    }
                    else {
                        context.getRequest().getSession(true).setAttribute(SESSION_KEY_WX_USER, wxMpUser);
                        v.addObject("state",context.getState());
                        v.setViewName("redirect:/wx/login.html");
                    }
                    return v;
                }
                else {
                    //用户已绑定,创建会话
                    createWxSession(context);
                }
            }
            return afterModelAndView(invoker.invoke(context));
        } catch (Exception e) {
            try {
                log.error("weixinOauth dispatch 出错", e);
                context.getModelMap().addAttribute("message", e.getMessage());
            } catch (Exception e1) {
            }
            ModelAndView v = new ModelAndView(errorView);
            v.addAllObjects(context.getModelMap());
            return v;
        }
    }

    private ModelAndView afterModelAndView(ModelAndView model) {
        if (model == null) {
            return null;
        }
        model.addObject("rootPath", propertyService.getAsString("portal.rootPath"));
        return model;
    }


    //后台生成jwt Cookie
    private void createWxSession(OAuth2Context context) {
        User user = context.getUser();//(此处先确保用户已绑定微信)

        //是否已经登录过,如果登录过判断用户是否当前绑定的用户
        UserSession userSession = userSessionService.getUserSessionByRequest(context.getRequest());
        if (userSession != null && !user.getId().equals(userSession.getUserId())) {//不是当前绑定的用户,清理会话
            userSessionService.deleteTokenAndSession(userSession);
            userSession = null;
        }

        if (userSession == null) {
            Integer openAccountId = null;
            OpenAccount openAccount = openAccountService.selectOpenAccountByUserId(OauthType.WEIXIN.getId(), user.getId());
            if(openAccount != null){
                openAccountId = openAccount.getId();
            }
            userSession = userSessionService.createWxlUserSession(user.getId(), openAccountId);
        }
        context.getRequest().getSession(true).setAttribute(SessionConstant.KEY_USER_SESSION_ID, userSession.getId());
        String jwt = authService.getJWTForTokenId(userSession.getTokenId());
        HttpServletResponse response = context.getResponse();
        Cookie cookie = new Cookie("jwt", jwt);
        cookie.setPath("/");
        response.addCookie(cookie);
    }


    // 用来测试的页面
    @RequestMapping(value = "/wxmenu", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView wxLinkTest(
            @RequestParam(value = "openId", required = false) String openId,
            @RequestParam(value = "nickname", required = false) String nickName,
            @RequestParam("state") String state,
            @RequestParam(value = "account") String account,
            ModelMap model,
            HttpServletRequest request,
            HttpServletResponse response) {
        if (!Boolean.TRUE.equals(propertyService.getAsBoolean("weixin.debug"))) {
            try {
                response.sendError(404, "Not Found");
            } catch (IOException e) {
                log.error("", e);
            }
            return null;
        }
        OAuth2Context context = new OAuth2Context();
        User user = userMapper.selectByNameOrEmailOrPhone(account);
        context.setRequest(request);
        context.setResponse(response);
        WxMpUser u = new WxMpUser();
        u.setNickname(nickName);
        u.setOpenId(openId);
        context.setWxMpUser(u);
        context.setUser(user);
        context.setModelMap(model);
        context.setState(state);
        for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
            String[] values = entry.getValue();
            model.addAttribute(entry.getKey(), values != null ? values[0] : "");
        }
        return dispatch(context);
    }

    /**
     * 用来测试页面，页面所需要的参数通过http-get查询字符串给定。
     * 比如下面的页面打开view：m/wxlogin_confirm.html，会携带message参数
     * http://localhost:8080/cloudPortal/api/wxlink?view=wxlogin_confirm&message=%E6%82%A8%E5%B7%B2%E7%BB%8F%E7%94%A8%E8%A1%8C%E4%BA%91%E8%B4%A6%E6%88%B7%E7%99%BB%E5%BD%95
     *
     * @param openId
     * @param nickName
     * @param state
     * @param view
     * @param account
     * @param model
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/wxlink", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView wxlink(
            @RequestParam(value = "openId", required = false) String openId,
            @RequestParam(value = "nickname", required = false) String nickName,
            @RequestParam(value = "state", required = false) String state, @RequestParam("view") String view,
            @RequestParam(value = "account", required = false) String account,
            ModelMap model,
            HttpServletRequest request,
            HttpServletResponse response) {
        if (!Boolean.TRUE.equals(propertyService.getAsBoolean("portal.debug"))) {
            try {
                response.sendError(404, "Debug Disabled");
            } catch (IOException e) {
                log.error("", e);
            }
            return null;
        }
        OAuth2Context context = new OAuth2Context();
        User user = userMapper.selectByNameOrEmailOrPhone(account);
        context.setRequest(request);
        context.setResponse(response);
        WxMpUser u = new WxMpUser();
        u.setNickname(nickName);
        u.setOpenId(openId);
        context.setWxMpUser(u);
        context.setUser(user);
        context.setModelMap(model);
        context.setState(state);
        for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
            String[] values = entry.getValue();
            model.addAttribute(entry.getKey(), values != null ? values[0] : "");
        }
        ModelAndView v = new ModelAndView("m/" + view);
        v.addAllObjects(model);
        return afterModelAndView(v);
    }


}
