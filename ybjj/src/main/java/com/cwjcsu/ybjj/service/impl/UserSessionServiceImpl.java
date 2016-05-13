package com.cwjcsu.ybjj.service.impl;


import com.cwjcsu.ybjj.constant.SessionConstant;
import com.cwjcsu.ybjj.domain.User;
import com.cwjcsu.ybjj.domain.UserSession;
import com.cwjcsu.ybjj.domain.UserWebToken;
import com.cwjcsu.ybjj.mapper.UserMapper;
import com.cwjcsu.ybjj.mapper.UserSessionMapper;
import com.cwjcsu.ybjj.mapper.UserWebTokenMapper;
import com.cwjcsu.ybjj.service.UserSessionService;
import com.cwjcsu.ybjj.util.DateUtil;
import com.google.common.base.Optional;
import com.google.common.collect.Iterables;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * @author ye
 */
@Service
public class UserSessionServiceImpl implements UserSessionService {

    @Autowired
    UserSessionMapper userSessionMapper;

    @Autowired
    UserWebTokenMapper userWebTokenMapper;

    @Autowired
    UserMapper userMapper;

    @Value("${usersession.timeout.minute}")
    int TIMEOUT_MINUTE;

    @Override
    public UserSession findByTokenId(String tokenId) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("token_id", tokenId);
        List<UserSession> sessions = userSessionMapper.selectByFields(map);
        return Iterables.getOnlyElement(sessions, null);
    }

    @Override
    @Transactional
    public void createUserSession(UserSession userSession) {
        List<UserSession> userSessions = userSessionMapper.selectNotExpiredByUserId(userSession.getUserId());
        if (userSessions.size() == 0) {
            // 生成第一条用户的http session时上线
            onlineStatusChange(userSession.getUserId(), true);
        }
        userSessionMapper.insert(userSession);
    }

    @Override
    @Transactional
    public void deleteUserSession(UserSession userSession) {
        UserWebToken userWebToken = userWebTokenMapper.selectByTokenId(userSession.getTokenId());
        if (userWebToken != null && Boolean.TRUE.equals(userWebToken.getDeleteOnSessionDestroy())) {
            userWebTokenMapper.deleteByPrimaryKey(userWebToken.getId());
        }
        userSessionMapper.deleteByPrimaryKey(userSession.getId());
        List<UserSession> userSessions = userSessionMapper.selectNotExpiredByUserId(userSession.getUserId());
        if (userSessions.size() == 0) {
            // 删除最后一条用户的http session时下线
            onlineStatusChange(userSession.getUserId(), false);
        }
    }

    @Override
    @Transactional
    public void deleteTokenAndSession(UserSession userSession) {
        UserWebToken userWebToken = userWebTokenMapper.selectByTokenId(userSession.getTokenId());
        if (userWebToken != null) {
            userWebTokenMapper.deleteByPrimaryKey(userWebToken.getId());
        }
        userSessionMapper.deleteByPrimaryKey(userSession.getId());
        List<UserSession> userSessions = userSessionMapper.selectNotExpiredByUserId(userSession.getUserId());
        if (userSessions.size() == 0) {
            // 删除最后一条用户的http session时下线
            onlineStatusChange(userSession.getUserId(), false);
        }
    }
    @Override
    @Transactional
    public synchronized int cleanExpiredSession() {
        List<UserSession> expireSessions = userSessionMapper.selectExpiredSession();
        int count = expireSessions.size();
        for (UserSession session : expireSessions) {
            deleteUserSession(session);
        }
        return count;
    }


    @Override
    public synchronized int cleanExpiredWebToken() {
        return userWebTokenMapper.deleteExpiredWebToken();
    }

    @Override
    public Optional<UserSession> findByUserSessionId(Integer userSessionId) {
        return Optional.fromNullable(userSessionMapper.selectByPrimaryKey(userSessionId));
    }

    @Override
    @Transactional
    public UserSession createPortalUserSession(Integer userId, Integer openAccountId, boolean rememberMe5Day) {
        UserWebToken userWebToken = new UserWebToken();
        userWebToken.setTokenId(UUID.randomUUID().toString());
        userWebToken.setUserId(userId);
        userWebToken.setOpenAccountId(openAccountId);
        if(rememberMe5Day) {
            userWebToken.setExpireTime(LocalDateTime.now().plusDays(5).toDate());
            userWebToken.setDeleteOnSessionDestroy(false);
        } else {
            userWebToken.setExpireTime(DateUtil.MAX_DATE);
            userWebToken.setDeleteOnSessionDestroy(true);
        }
        userWebTokenMapper.insert(userWebToken);

        UserSession session = new UserSession();
        session.setUserId(userId);
        session.setTokenId(userWebToken.getTokenId());
        session.setExpireTime(LocalDateTime.now().plusMinutes(TIMEOUT_MINUTE).toDate());
        createUserSession(session);
        return session;
    }

    @Override
    @Transactional
    public UserSession createWxlUserSession(Integer userId, Integer openAccountId) {
        UserWebToken userWebToken = new UserWebToken();
        userWebToken.setTokenId(UUID.randomUUID().toString());
        userWebToken.setUserId(userId);
        userWebToken.setOpenAccountId(openAccountId);
        //微信创建的token不过期
        userWebToken.setExpireTime(DateUtil.MAX_DATE);
        userWebToken.setDeleteOnSessionDestroy(false);
        userWebTokenMapper.insert(userWebToken);
        UserSession session = new UserSession();
        session.setUserId(userId);
        session.setTokenId(userWebToken.getTokenId());
        session.setExpireTime(LocalDateTime.now().plusMinutes(TIMEOUT_MINUTE).toDate());
        createUserSession(session);
        return session;
    }

    @Override
    @Transactional
    public void deleteUserSessionByOpenAccountId(Integer openAccountId) {
        List<UserWebToken> webTokens = userWebTokenMapper.findByOpenAccountId(openAccountId);
        for (UserWebToken webToken : webTokens) {
            UserSession userSession = findByTokenId(webToken.getTokenId());
            if(userSession!=null) {
                deleteTokenAndSession(userSession);
            }
        }
    }

    @Override
    public void increaseUserSessionExpiredDate(Integer userSessionId) {
        UserSession session = userSessionMapper.selectByPrimaryKey(userSessionId);
        if(session != null){
            session.setExpireTime(LocalDateTime.now().plusMinutes(TIMEOUT_MINUTE).toDate());
            userSessionMapper.updateByPrimaryKey(session);
        }
        if (session != null && session.getExpireTime().after(new Date())) {
            // 有心跳说明在线
            onlineStatusChange(session.getUserId(), true);
        }
    }

    @Override
    public UserWebToken findWebTokenByTokenId(String tokenId) {
        return userWebTokenMapper.selectByTokenId(tokenId);
    }


    private void onlineStatusChange(Integer userId, boolean online) {
        User user = userMapper.selectByPrimaryKey(userId);
        if (!user.getOnline().equals(online)) { // 状态改变才修改
            userMapper.updateOnlineStautsById(userId, online);
        }
    }

    /**
     * 如果已经登录，则获取用户的会话信息
     *
     * @param request
     * @return
     */
    @Override
    public UserSession getUserSessionByRequest(HttpServletRequest request) {
        HttpSession session = request.getSession();
        if (session != null) {
            Integer sid = (Integer) request.getSession().getAttribute(SessionConstant.KEY_USER_SESSION_ID);
            if (sid != null) {
                Optional<UserSession> sessionOptional = findByUserSessionId(sid);
                if(sessionOptional.isPresent()) {
                    return sessionOptional.get();
                }
            }
        }
        return null;
    }
}
