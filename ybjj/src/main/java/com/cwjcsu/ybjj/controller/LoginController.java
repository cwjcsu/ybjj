package com.cwjcsu.ybjj.controller;

import com.cwjcsu.common.service.PropertyService;
import com.cwjcsu.common.util.HttpUtil;
import com.cwjcsu.ybjj.constant.ErrorCode;
import com.cwjcsu.ybjj.constant.SessionConstant;
import com.cwjcsu.ybjj.domain.OpenAccount;
import com.cwjcsu.ybjj.domain.User;
import com.cwjcsu.ybjj.domain.UserSession;
import com.cwjcsu.ybjj.domain.UserWebToken;
import com.cwjcsu.ybjj.service.*;
import com.google.common.base.Optional;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author atlas
 */
@RestController
@RequestMapping("/")
public class LoginController {

    private static Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    UserService userService;
    @Autowired
    AuthService authService;
    @Autowired
    UserSessionService userSessionService;
    @Autowired
    PropertyService propertyService;
    @Autowired
    LoginService loginService;
    @Autowired
    RestTemplate restTemplate;

    @Autowired
    private OpenAccountService openAccountService;

    @Value("${usersession.timeout.minute}")
    int TIMEOUT_MINUTE;

    /**
     * 登录预处理
     */
    @RequestMapping(value = "/preLogin")
    public Map<String, Object> preLogin(@RequestParam String account, HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, Object> result = new HashMap<String, Object>();

        Optional<User> userOpt = userService.findByNameOrEmailOrPhone(account);
        if (!userOpt.isPresent()) {
            result.put("needCaptcha", false);
        } else {//需要查最近FAIL_NEED_CHECKCODE_COUNT次的登录记录
            User user = userOpt.get();
            result.put("needCaptcha", loginService.isUserNeedCaptcha(user.getId()));
        }

        result.put("success", true);
        return result;
    }

    public Map<String, Object> loginForUser(User user, boolean rememberMe5Day, HttpServletRequest request) {
        UserSession session = userSessionService.createPortalUserSession(user.getId(), null, rememberMe5Day);

        request.getSession(true).setAttribute(SessionConstant.KEY_USER_SESSION_ID, session.getId());
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("success", true);
        map.put("jwt", authService.getJWTForTokenId(session.getTokenId()));

        return map;
    }

    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    Map<String, Object> logout(@RequestParam String jwt,
                               HttpServletRequest request) {
        String address = HttpUtil.getRemoteIP(request);
        String tokenId = null;
        try {
            tokenId = authService.getJWTBody(jwt).getId();
        } catch (Exception e) {
            logger.trace("the jwt is invalid", e);
        }
        if (tokenId != null) {
            UserSession session = userSessionService.findByTokenId(tokenId);
            if (session != null) {

                userSessionService.deleteTokenAndSession(session);
            }
        }

        //使http会话失效
        if (request.getSession() != null) {
            request.getSession().invalidate();
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("success", true);
        return map;
    }

    @RequestMapping(value = "/checkJwt", method = RequestMethod.POST)
    Map<String, Object> checkJwt(@RequestParam String jwt,
                                 HttpServletRequest request) {
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            String tokenId = null;
            try {
                tokenId = authService.getJWTBody(jwt).getId();
            } catch (Exception e) {
                logger.trace("the jwt is invalid", e);
            }

            if (tokenId == null) {
                map.put("success", false);
                map.put("msg", "the jwt is invalid");
                map.put("code", ErrorCode.JWT_INVALID);
                return map;
            }
            synchronized (userSessionService) {
                UserWebToken userWebToken = userSessionService.findWebTokenByTokenId(tokenId);
                if (userWebToken == null) {
                    map.put("success", false);
                    map.put("msg", "尚未登录");
                    map.put("code", ErrorCode.SESSION_NOT_FOUND);
                    return map;
                } else {
                    Integer openAccountId = userWebToken.getOpenAccountId();
                    if (openAccountId != null) {
                        OpenAccount openAccount = openAccountService.selectOpenAccountById(openAccountId);
                        if (openAccount == null) {
                            map.put("success", false);
                            map.put("code", ErrorCode.JWT_INVALID);
                            map.put("msg", "the jwt is invalid");
                            return map;
                        }
                        OpenAccount currentOpenAccount = openAccountService.selectOpenAccountByUserId(openAccount.getOauthType().getId(), userWebToken.getUserId());
                        if (currentOpenAccount == null || !openAccount.getOpenId().equals(currentOpenAccount.getOpenId())) {
                            map.put("success", false);
                            map.put("code", ErrorCode.JWT_INVALID);
                            map.put("msg", "the jwt is invalid");
                            return map;
                        }
                    }
                    if (userWebToken.getExpireTime().after(new Date())) {
                        UserSession session = userSessionService.findByTokenId(tokenId);
                        if (session == null) {
                            session = new UserSession();
                            session.setUserId(userWebToken.getUserId());
                            session.setTokenId(userWebToken.getTokenId());
                            session.setExpireTime(LocalDateTime.now().plusMinutes(TIMEOUT_MINUTE).toDate());
                            userSessionService.createUserSession(session);
                        } else {
                            userSessionService.increaseUserSessionExpiredDate(session.getId());
                        }
                        request.getSession(true).setAttribute(SessionConstant.KEY_USER_SESSION_ID, session.getId());
                        map.put("success", true);
                        return map;
                    } else {
                        map.put("success", false);
                        map.put("msg", "请重新登录");
                        map.put("code", ErrorCode.SESSION_EXPIRE);
                        return map;
                    }
                }
            }
        } catch (Exception e) {
            map.put("success", false);
            map.put("msg", e.getMessage());
        }
        return map;
    }

    @RequestMapping(value = "/heartbeat", method = RequestMethod.POST)
    Map<String, Object> heartbeat(HttpServletRequest request) {
        Map<String, Object> map = new HashMap<String, Object>();
        HttpSession httpSession = request.getSession();
        if (httpSession != null && httpSession.getAttribute(SessionConstant.KEY_USER_SESSION_ID) != null) {
            Integer userSessionId = (Integer) httpSession.getAttribute(SessionConstant.KEY_USER_SESSION_ID);
            userSessionService.increaseUserSessionExpiredDate(userSessionId);
        }
        map.put("success", true);
        return map;
    }

    @RequestMapping(value = "/https2http_proxy", method = {RequestMethod.POST, RequestMethod.GET})
    public ResponseEntity<?> https(@RequestParam String url) {
        HttpEntity<String> requestEntity = new HttpEntity<String>("");

        ResponseEntity<byte[]> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, byte[].class);
        return ResponseEntity.ok()
                .contentLength(responseEntity.getHeaders().getContentLength())
                .contentType(responseEntity.getHeaders().getContentType())
                .body(new InputStreamResource(new ByteArrayInputStream(responseEntity.getBody())));
    }
}
