package com.cwjcsu.ybjj.service;

import com.cwjcsu.ybjj.domain.UserSession;
import com.cwjcsu.ybjj.domain.UserWebToken;
import com.google.common.base.Optional;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

/**
 * @author ye
 */
public interface UserSessionService {

    /**
     * 根据tokenId查找其session
     */
    UserSession findByTokenId(String tokenId);

    /**
     * 创建一个userSession
     */
    void createUserSession(UserSession userSession);

    /**
     * 删除指定的userSession
     */
    void deleteUserSession(UserSession userSession);


    @Transactional
    void deleteTokenAndSession(UserSession userSession);

    int cleanExpiredSession();

    Optional<UserSession> findByUserSessionId(Integer userSessionId);

    UserSession createPortalUserSession(Integer userId, Integer openAccountId, boolean rememberMe5Day);

    UserSession createWxlUserSession(Integer userId, Integer openAccountId);

    @Transactional
    void deleteUserSessionByOpenAccountId(Integer openAccountId);

    void increaseUserSessionExpiredDate(Integer userSessionId);

    UserWebToken findWebTokenByTokenId(String tokenId);

    int cleanExpiredWebToken();

    /**
     * 如果已经登录，则获取用户的会话信息
     *
     * @param request
     * @return
     */
    UserSession getUserSessionByRequest(HttpServletRequest request);
}
