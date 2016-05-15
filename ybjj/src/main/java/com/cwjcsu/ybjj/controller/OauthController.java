package com.cwjcsu.ybjj.controller;


import com.cwjcsu.common.service.PropertyService;
import com.cwjcsu.common.util.*;
import com.cwjcsu.weixin.OAuth2Context;
import com.cwjcsu.weixin.StateMapping;
import com.cwjcsu.weixin.WeixinService;
import com.cwjcsu.weixin.WxRequestContext;
import com.cwjcsu.ybjj.constant.SessionConstant;
import com.cwjcsu.ybjj.domain.OpenAccount;
import com.cwjcsu.ybjj.domain.User;
import com.cwjcsu.ybjj.domain.UserSession;
import com.cwjcsu.ybjj.domain.WxQrCode;
import com.cwjcsu.ybjj.domain.enums.OauthType;
import com.cwjcsu.ybjj.domain.enums.QrStatus;
import com.cwjcsu.ybjj.domain.enums.SceneKeyType;
import com.cwjcsu.ybjj.domain.vo.WxLoginVo;
import com.cwjcsu.ybjj.exception.WeixinException;
import com.cwjcsu.ybjj.service.AuthService;
import com.cwjcsu.ybjj.service.OpenAccountService;
import com.cwjcsu.ybjj.service.UserService;
import com.cwjcsu.ybjj.service.UserSessionService;
import com.google.common.base.Optional;
import me.chanjar.weixin.common.bean.result.WxError;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.apache.commons.lang.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.cwjcsu.common.util._.$;
import static org.apache.commons.collections.MapUtils.putAll;

/**
 * 第三方合作账户登录
 * Created by lidonghao on 15/8/5.
 */

@RestController
@RequestMapping("/oauth")
public class OauthController {
    private static Logger logger = LoggerFactory.getLogger(OauthController.class);

    public static final String SESSION_KEY_WX_LOGIN = "wx.qrcode.login";

    public static final String OAUTH_STATE_LOGIN = "oauth_state_login";
    public static final String OAUTH_STATE_BIND = "oauth_state_bind";

    public static final String OP_TYPE_LOGIN = "login";
    public static final String OP_TYPE_BIND = "bind";
    public static final String SECURE_HTTP = "secureHttp";
    public static final String NORMAL_HTTP = "normalHttp";

    public static final String SESSION_KEY_BIND_INFO = "key_bind_info";

    public static final int OAUTH_CALLBACK_SUCCESS = 0;//回调成功
    public static final int OAUTH_CALLBACK_REPEATED_BIND = 1;//重复绑定
    public static final int OAUTH_CALLBACK_ERROR = 2;//回调失败

    public static final String DEFAULT_SEPARATOR = "::";

    @Autowired
    UserService userService;
    @Autowired
    AuthService authService;
    @Autowired
    UserSessionService userSessionService;


    @Autowired
    private WeixinService weixinService;

    @Autowired
    private OpenAccountService openAccountService;

    @Autowired
    private PropertyService propertyService;

    @Value("${weixin.template.accountChange}")
    private String weixinAccountChangeTemplate;

    @PostConstruct
    private void init() {
        WeixinService.WxQrCodeScanListener listener = new WeixinService.WxQrCodeScanListener() {
            @Override
            public boolean onQrCodeScaned(WxQrCode qrCode, WxRequestContext context) throws Exception {
                return onOAuthWxQrCodeScaned(qrCode, context);
            }
        };
        weixinService.addWxQrCodeScanListener(SceneKeyType.SCENE_KEY_LOGIN.getSceneKey(), listener);

        weixinService.addWxQrCodeScanListener(SceneKeyType.SCENE_KEY_BIND.getSceneKey(), listener);
    }

    /**
     * 获取微信二维码
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "/getWxQrCode", method = {RequestMethod.GET, RequestMethod.POST})
    Map<String, Object> getWxQrCode(@RequestParam(value = "userId", required = false) Integer userId, @RequestParam String sceneKey,
                                    HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            if (!isSceneKeyLegal(sceneKey)) {
                logger.error("sceneKey[{}]未在SceneKeyType类中定义", sceneKey);
                throw new Exception("sceneKey参数错误");
            }
            WxLoginVo vo = new WxLoginVo();
            if (SceneKeyType.SCENE_KEY_BIND.getSceneKey().equals(sceneKey)) {
                UserSession userSession = userSessionService.getUserSessionByRequest(request);
                vo.setId(userSession.getUserId());
            } else if (userId != null) {
                vo.setId(userId);
            }
            String uuid = (String) request.getSession().getAttribute(sceneKey);
            WxQrCode code = null;
            if (uuid == null) {
                //登录二维码5分钟过期
                code = weixinService.createWxQrCode(sceneKey, JsonUtil.GSON.toJson(vo), 5 * 60);
                request.getSession().setAttribute(sceneKey, code.getUuid());
            } else {
                //保证有2秒可用
                code = weixinService.checkAndGetWxQrCode(uuid, 2, true);
                if (code == null || !code.isActive()) {
                    code = weixinService.createWxQrCode(sceneKey, JsonUtil.GSON.toJson(vo), 5 * 60);
                    request.getSession().setAttribute(sceneKey, code.getUuid());
                }
            }
            String picUrl = "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=" + URLEncoder.encode(code.getTicket(), "UTF-8");
            result.put("success", true);
            result.put("QRCode", picUrl);
        } catch (Exception e) {
            result.put("success", false);
            result.put("msg", e.getMessage());
            logger.error("获取微信二维码片失败:" + e.getMessage(), e);
        }
        return result;
    }

    /**
     * 查询二维码扫描结果
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "/checkScanWxResult", method = {RequestMethod.GET, RequestMethod.POST})
    Map<String, Object> checkScanWxResult(@RequestParam String sceneKey, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            if (!isSceneKeyLegal(sceneKey)) {
                logger.error("sceneKey[{}]未在SceneKeyType类中定义", sceneKey);
                throw new Exception("sceneKey参数错误");
            }
            int status = QrStatus.WAIT_SCAN.getId();
            String uuid = (String) request.getSession().getAttribute(sceneKey);
            WxQrCode code = null;
            if (uuid != null) {
                code = weixinService.findWxQrCodeByUUID(uuid);
            }
            if (code != null) {
                if (QrStatus.SUCCESS.equals(code.getStatus())) {//扫描二维码成功后，处理各种不同的业务
                    result.putAll(this.doBusinessAfterScanningSuccess(code, request));
                } else if (QrStatus.FAIL.equals(code.getStatus())) {
                    WxError error = WxError.fromJson(code.getRemark());
                    if (error != null) {
                        result.put("errcode", error.getErrorCode());
                        result.put("errmsg", error.getErrorMsg());
                    } else {
                        result.put("errcode", -1);
                        result.put("errmsg", "Unknown Error");
                    }
                } else if (!QrStatus.EXPIRED.equals(code.getStatus()) && code.getValidSeconds() <= 2) {
                    code.setStatus(QrStatus.EXPIRED);
                    weixinService.updateWxQrCode(code);
                }
                result.put("leftTime", code.getValidSeconds());
                status = code.getStatus().getId();
            }
            result.put("status", status);
            result.put("success", true);
        } catch (Exception e) {
            result.put("success", false);
            result.put("msg", e.getMessage());
            logger.error("查询微信扫描结果失败:" + e.getMessage(), e);
        }
        return result;
    }


    public static class BindInfo {
        private WxMpUser wxUser;
        private String openId;
        private OauthType oauthType;

        private Integer userId;

        public Integer getUserId() {
            return userId;
        }

        public void setUserId(Integer userId) {
            this.userId = userId;
        }

        public WxMpUser getWxUser() {
            return wxUser;
        }

        public void setWxUser(WxMpUser wxUser) {
            this.wxUser = wxUser;
        }

        public String getOpenId() {
            return openId;
        }

        public void setOpenId(String openId) {
            this.openId = openId;
        }

        public OauthType getOauthType() {
            return oauthType;
        }

        public void setOauthType(OauthType oauthType) {
            this.oauthType = oauthType;
        }
    }


    /**
     * 扫描绑定二维码后，发送一条消息给用户选择：同意或拒绝。
     *
     * @param qrCode
     * @param ctx
     * @return
     * @throws WxErrorException
     */
    private boolean onOAuthWxQrCodeScaned(WxQrCode qrCode, WxRequestContext ctx) throws WxErrorException {
        String openID = ctx.getOpenId();
        if (QrStatus.SCANNED.equals(qrCode.getStatus())) {
            weixinService.sendTextMessage(openID, "已经扫描过二维码");
            return false;
        }
        if (qrCode.getOpenId() != null && !qrCode.getOpenId().equals(openID)) {//二维码已经扫过而且扫描的用户不一样
            weixinService.sendTextMessage(openID, "扫描二维码非法");
            return false;
        }
        User user = userService.findUserByOpenID(openID, OauthType.WEIXIN.getId());

        WxLoginVo vo = qrCode.getAttachementAsType(WxLoginVo.class);
        if (vo == null) {
            vo = new WxLoginVo();
        }
        WxMpUser wxUser = vo.getWxUser();
        if (wxUser == null) {//从微信服务器获取微信用户信息，保存到WxQrCode的业务数据区域
            wxUser = ctx.getWxMpService().userInfo(openID, "zh_CN");
            vo.setWxUser(wxUser);
            qrCode.setAttachementAsObject(vo);
        }

        qrCode.setTargetId(vo.getId());
        qrCode.setOpenId(openID);
        qrCode.setStatus(QrStatus.SCANNED);

        // 询问是否重新绑定
        if (user != null && SceneKeyType.SCENE_KEY_BIND.getSceneKey().equals(qrCode.getSceneKey())) {
            String msg = alreadyBind(wxUser.getNickname(), user.getNickname(), qrCode);
            weixinService.sendTextMessage(openID, msg);
            weixinService.updateWxQrCode(qrCode);
            return false;
        }

        String linkAccept = weixinService.getOAuth2AuthorizationUrl(WeixinService.OAuth2Scope.snsapi_userinfo, $("{}_{}_{}", qrCode.getSceneKey(), "accept", qrCode.getUuid()));
        String linkRefuse = weixinService.getOAuth2AuthorizationUrl(WeixinService.OAuth2Scope.snsapi_userinfo, $("{}_{}_{}", qrCode.getSceneKey(), "refuse", qrCode.getUuid()));

        String msg;
        if (SceneKeyType.SCENE_KEY_LOGIN.getSceneKey().equals(qrCode.getSceneKey())) {
            msg = getLoginConfirmMsg(wxUser.getNickname(), linkAccept, linkRefuse);
        } else {
            msg = getBindConfirmMsg(wxUser.getNickname(), qrCode, linkAccept, linkRefuse);
        }
        try {
            weixinService.sendTextMessage(openID, msg);
        } catch (WxErrorException e) {
            WxError err = e.getError();
            qrCode.setStatus(QrStatus.FAIL);
            qrCode.setRemark(err.getJson());
            logger.error("error handling QrCode {} : ", qrCode.getTicket(), e.toString());
        }
        weixinService.updateWxQrCode(qrCode);
        return false;
    }


    private String getBindConfirmMsg(String wxNickname, WxQrCode qrCode, String linkAccept, String linkRefuse) {
        Dict dict = qrCode.getAttachementAsDict();
        Integer id = dict.getAsInteger("id");
        Optional<User> userOpt = userService.getById(id);
        StringBuilder sb = new StringBuilder(propertyService.getAsString("portal.name") + "账户");
        if (userOpt.isPresent()) {
            sb.append("“").append(userOpt.get().getNickname()).append("”");
        }
        String msg = $("您正在将此微信账户与{}绑定，是否同意？\n{}      {}", sb.toString(), WeixinUtil.wrapWithHyperlink("同意", linkAccept), WeixinUtil.wrapWithHyperlink("拒绝", linkRefuse));
        return msg;
    }

    private String getLoginConfirmMsg(String wxNickname, String linkAccept, String linkRefuse) {
        String msg = $("您正以微信身份登录{}，请问是否同意？\n{}      {}", propertyService.getAsString("portal.name"), WeixinUtil.wrapWithHyperlink("同意", linkAccept), WeixinUtil.wrapWithHyperlink("拒绝", linkRefuse));
        return msg;
    }


    /**
     * 微信绑定或绑定确认消息，用户点击微信里面的“同意”或者“拒绝”连接之后的处理方法
     * <p/>
     * wxbind_accept_{uuid} or wxbind_refuse_{uuid}
     *
     * @param context
     * @return
     */
    @StateMapping(state = {WeixinUtil.SCENE_KEY_BIND + "_{accept}_{uuid}", WeixinUtil.SCENE_KEY_LOGIN + "_{accept}_{uuid}"}, requireBind = false)
    public ModelAndView wxOauthQrCodeConfirm(OAuth2Context context) {
        ModelAndView v = null;
        String uuid = context.getStateParameters().getAsString("uuid");
        String openID = context.getOpenId();
        try {
            String accept = context.getStateParameters().getAsString("accept");
            if (StringUtil.isEmpty(uuid) || !StringUtil.in(accept, "accept", "refuse")) {
                throw new WeixinException("非法链接");
            }
            WxQrCode qrCode = weixinService.findWxQrCodeByUUID(uuid);
            if (qrCode == null) {
                throw new WeixinException("非法链接");
            }
            if (!qrCode.isActive()) {
                throw new WeixinException("链接已失效");
            }
            String state = context.getBaseState();
            String action = "登录";
            if (SceneKeyType.SCENE_KEY_BIND.getSceneKey().equals(state)) {
                action = "绑定";
            }
            String info = $("{}失败", action);
            String title = $("拒绝{}", action);
            QrStatus status = QrStatus.REJECTED;
            String pageUrl = "m/error";
            if ("accept".equals(accept)) {
                title = $("成功{}", action);
                status = QrStatus.SUCCESS;
                WxLoginVo vo = qrCode.getAttachementAsType(WxLoginVo.class);
                if (SceneKeyType.SCENE_KEY_LOGIN.getSceneKey().equals(state)) {
                    WxMpUser wxUser = vo.getWxUser();
                    if (wxUser == null) {
                        throw new WeixinException("获取用户信息失败");
                    }
                    User user = userService.findUserByOpenID(wxUser.getOpenId(), OauthType.WEIXIN.getId());
                    if (user == null) {
                        user = openAccountService.createWeixinOpenAccount(wxUser);
                    }
                    vo.setNickname(user.getNickname());
                    qrCode.setAttachementAsObject(vo);
                    qrCode.setTargetId(user.getId());
                    info = $("您已成功登录{}", propertyService.getAsString("portal.name"));
                } else {
                    qrCode.setTargetId(vo.getId());
                    info = $("您已成功绑定{}账户", propertyService.getAsString("portal.name"));
                }

                pageUrl = "m/wxlogin_confirm";
            }
            qrCode.setStatus(status);
            weixinService.updateWxQrCode(qrCode);
            v = new ModelAndView(pageUrl);
            v.addObject("message", info);
            v.addObject("title", title);
            return v;
        } catch (Exception e) {
            if (!(e instanceof WeixinException)) {
                logger.error("error handle wxBindConfirm", e);
            }
            v = new ModelAndView("m/error");
            v.addObject("message", e.getMessage());
            v.addObject("title", "错误");
            return v;
        }
    }

    /**
     * 判断sceneKey是否合法，已在SceneKeyType中定义的为合法sceneKey
     *
     * @param sceneKey
     * @return
     */
    private boolean isSceneKeyLegal(String sceneKey) {
        if (StringUtil.isEmpty(sceneKey)) {
            return false;
        }
        for (SceneKeyType sceneKeyType : SceneKeyType.values()) {
            if (sceneKeyType.getSceneKey().equals(sceneKey)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 扫描二维码成功后，处理各种不同的业务
     *
     * @param code
     * @param request
     * @return result
     * @throws Exception
     */
    private Map<String, Object> doBusinessAfterScanningSuccess(WxQrCode code, HttpServletRequest request) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        WxLoginVo vo = code.getAttachementAsType(WxLoginVo.class);
        WxMpUser wxUser = vo.getWxUser();
        User user = null;
        if (code.getTargetId() != null) {
            user = userService.getById(code.getTargetId()).get();
        }
        if (code.getSceneKey().equals(SceneKeyType.SCENE_KEY_LOGIN.getSceneKey())) {//登录
            String msg = getLoginGreeting(wxUser.getNickname(), user != null ? user.getNickname() : "");
            weixinService.sendTextMessage(wxUser.getOpenId(), msg);
            //创建UserSession
            UserSession session = userSessionService.createPortalUserSession(code.getTargetId(), null, false);
            request.getSession(true).setAttribute(SessionConstant.KEY_USER_SESSION_ID, session.getId());
            String jwt = authService.getJWTForTokenId(session.getTokenId());

            result.put("jwt", jwt);
        } else if (code.getSceneKey().equals(SceneKeyType.SCENE_KEY_BIND.getSceneKey())) {//绑定
            BindInfo bi = new BindInfo();
            bi.setWxUser(wxUser);
            bi.setOauthType(OauthType.WEIXIN);
            bi.setOpenId(wxUser.getOpenId());
            request.getSession().setAttribute(SESSION_KEY_BIND_INFO, bi);

            //绑定新的账户
            RequestResult r = this.bindOauthAccount(request);
            if (!r.isSuccess()) {
                throw new Exception(r.getMsg());
            }

            //确认绑定后，返回用户资料
            result.put("avatar", wxUser.getHeadImgUrl());
            result.put("nickname", wxUser.getNickname());
            result.put("location", (wxUser.getProvince() != null ? wxUser.getProvince() : "") + (wxUser.getCity() != null ? wxUser.getCity() : ""));
        } else {
            //其它与扫描二维码有关的业务
        }

        return result;
    }

    private String alreadyBind(String wxNickname, String portalNickname, WxQrCode qrCode) {
        String wxName = "微信账户“" + wxNickname + "”";
        Dict dict = qrCode.getAttachementAsDict();
        Integer id = dict.getAsInteger("id");
        Optional<User> userOpt = userService.getById(id);
        StringBuilder sb = new StringBuilder(propertyService.getAsString("portal.name") + "账户");
        if (userOpt.isPresent()) {
            sb.append("“").append(userOpt.get().getNickname()).append("”");
        }
        String portalName = propertyService.getAsString("portal.name");

        String linkContinue = weixinService.getOAuth2AuthorizationUrl(WeixinService.OAuth2Scope.snsapi_userinfo, $("{}_{}_{}", qrCode.getSceneKey() + "_c", "continue", qrCode.getUuid()));
        String linkCancel = weixinService.getOAuth2AuthorizationUrl(WeixinService.OAuth2Scope.snsapi_userinfo, $("{}_{}_{}", qrCode.getSceneKey() + "_c", "cancel", qrCode.getUuid()));

        String msg = $("您正在尝试将此微信账户与{}绑定，此微信账户曾经登录过{}并拥有业务数据，" +
                        "如果您继续绑定，其已有的业务数据将被清除，请确认是否继续。\n{}    {}", sb.toString(), portalName,
                WeixinUtil.wrapWithHyperlink("继续绑定", linkContinue), WeixinUtil.wrapWithHyperlink("取消", linkCancel));
        return msg;
    }

    /**
     * 微信重新绑定 点击 继续 和取消 后的处理
     */
    @StateMapping(state = {WeixinUtil.SCENE_KEY_BIND + "_c" + "_{action}_{uuid}"}, requireBind = false)
    public ModelAndView wxReBind(OAuth2Context context) {
        ModelAndView v = null;
        String uuid = context.getStateParameters().getAsString("uuid");
        String openID = context.getOpenId();
        try {
            String action = context.getStateParameters().getAsString("action");
            if (StringUtil.isEmpty(uuid) || !StringUtil.in(action, "continue", "cancel")) {
                throw new WeixinException("非法链接");
            }
            WxQrCode qrCode = weixinService.findWxQrCodeByUUID(uuid);
            if (qrCode == null) {
                throw new WeixinException("非法链接");
            }
            if (!qrCode.isActive()) {
                throw new WeixinException("链接已失效");
            }

            String info = "";
            String title = "绑定微信";
            String pageUrl = "m/wxlogin_confirm";

            User oldUser = userService.findUserByOpenID(openID, OauthType.WEIXIN.getId());
            if ("continue".equals(action)) {
                openAccountService.deleteOpenAccountByUserId(OauthType.WEIXIN.getId(), oldUser.getId());

                qrCode.setStatus(QrStatus.SUCCESS);
                title = "绑定成功";
                info = $("您已成功绑定{}账户", propertyService.getAsString("portal.name"));
            } else if ("cancel".equals(action)) {
                title = "取消绑定";
                info = "绑定失败";
                pageUrl = "m/error";

                qrCode.setStatus(QrStatus.REJECTED);
            }
            v = new ModelAndView(pageUrl);
            v.addObject("message", info);
            v.addObject("title", title);

            weixinService.updateWxQrCode(qrCode);
            return v;
        } catch (Exception e) {
            if (!(e instanceof WeixinException)) {
                logger.error("error handle wxBindConfirm", e);
            }
            v = new ModelAndView("m/error");
            v.addObject("message", e.getMessage());
            v.addObject("title", "错误");
            return v;
        }
    }

    private String getBindGreeting(String wxNickname, String portalNickname) {
        return $("您已经将微信账户“{}”和{}账户“{}”成功绑定。", wxNickname, propertyService.getAsString("portal.name"), portalNickname);
    }

    private String getLoginGreeting(String wxNickname, String portalNickname) {
        return $("您已经成功登录{}。", propertyService.getAsString("portal.name"));
    }

    @RequestMapping(value = "/unBindAccount", method = RequestMethod.POST)
    public RequestResult unBindAccount(@RequestParam("oauthType") Integer oauthType, HttpServletRequest request) {
        RequestResult r = new RequestResult();
        try {
            UserSession userSession = userSessionService.getUserSessionByRequest(request);
            if (userSession == null) {
                return r.fail("用户会话已过期");
            }
            User user = userService.getById(userSession.getUserId()).get();
            openAccountService.deleteOpenAccountByUserId(oauthType, user.getId());
            r.success("解除绑定成功");
        } catch (Exception e) {
            logger.error("解除绑定失败：{}", e.getMessage(), e);
            r.fail(e.getMessage());
        }

        return r;
    }

    private RequestResult bindOauthAccount(HttpServletRequest request) {
        RequestResult r = new RequestResult();
        try {
            UserSession userSession = userSessionService.getUserSessionByRequest(request);
            if (userSession == null) {
                return r.fail("用户会话已过期");
            }
            BindInfo bi = (BindInfo) request.getSession().getAttribute(SESSION_KEY_BIND_INFO);
            User user = userService.getById(userSession.getUserId()).get();

            if (user != null && bi.getOauthType() != null) {
                OpenAccount openAccount = openAccountService.selectOpenAccountByUserId(bi.getOauthType().getId(), user.getId());
                if (openAccount != null) {
                    openAccountService.deleteOpenAccountByUserId(bi.getOauthType().getId(), user.getId());
                }
                //绑定账户
                if (OauthType.WEIXIN.equals(bi.getOauthType())) {
                    openAccountService.bindWeixinOpenAccount(user, bi.getWxUser());
                    WxMpUser wxUser = bi.getWxUser();
                    String remark = $("此微信账号已经成功绑定了{}账户“{}”", propertyService.getAsString("portal.name"), user.getNickname());
                    Map<String, String> msgParam = putAll(new HashMap<String, String>(), new String[][]{{"first", "微信绑定成功"},
                            {"account", user.getNickname()},
                            {"time", FastDateFormat.getInstance("yyyy年MM月dd日hh:mm:ss").format(new Date())},
                            {"type", "微信绑定"},
                            {"remark", remark}
                    });
                    try {
                        weixinService.sendTemplateMessage(wxUser.getOpenId(), weixinAccountChangeTemplate, null, msgParam);
                    } catch (Exception e) {
                        logger.error("sendTemplateMessage error", e);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("绑定账户失败:{}", e.getMessage(), e);
            return r.fail(e.getMessage());
        }

        return r.success("绑定账户成功");
    }


}
