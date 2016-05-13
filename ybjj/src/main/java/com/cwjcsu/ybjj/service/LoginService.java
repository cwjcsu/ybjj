package com.cwjcsu.ybjj.service;

/**
 * @author ye
 */
public interface LoginService {
    boolean isUserNeedCaptcha(Integer userId);

    void successLogin(Integer userId, String userAgent, String clientIp);

    void passwdErrorLogin(Integer userId, String userAgent, String clientIp);
}
