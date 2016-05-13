package com.cwjcsu.ybjj.service.impl;

import com.cwjcsu.ybjj.domain.LoginHistory;
import com.cwjcsu.ybjj.domain.enums.LoginResult;
import com.cwjcsu.ybjj.domain.enums.LoginType;
import com.cwjcsu.ybjj.mapper.LoginHistoryMapper;
import com.cwjcsu.ybjj.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author ye
 */
@Service
public class LoginServiceImpl implements LoginService {
    @Autowired
    LoginHistoryMapper loginHistoryMapper;

    /**
     * 登录失败多少次就需要验证码
     */
    public static int FAIL_NEED_CHECKCODE_COUNT = 3;

    @Override
    public boolean isUserNeedCaptcha(Integer userId) {
        List<LoginHistory> histories = loginHistoryMapper.selectLatestByUserId(userId, FAIL_NEED_CHECKCODE_COUNT);

        for(LoginHistory lh : histories) {
            if (lh.getResult() == LoginResult.SUCCESS) {
                // 三次只要有一次成功就不需要验证码
                return false;
            }
        }
        // 如果登录记录少于3条，返回false 不需要验证码
        // 否则走到这里说明这三条记录都不是成功的， 返回 true 需要验证码
        return histories.size() >= FAIL_NEED_CHECKCODE_COUNT;
    }

    @Override
    public void successLogin(Integer userId, String userAgent, String clientIp) {
        LoginHistory lh = new LoginHistory();
        lh.setUserId(userId);
        lh.setLoginTime(new Date());
        lh.setLoginType(LoginType.PASSWORD);
        lh.setUserAgent(userAgent);
        lh.setClientIp(clientIp);
        lh.setResult(LoginResult.SUCCESS);

        loginHistoryMapper.insert(lh);
    }

    @Override
    public void passwdErrorLogin(Integer userId, String userAgent, String clientIp) {
        LoginHistory lh = new LoginHistory();
        lh.setUserId(userId);
        lh.setLoginTime(new Date());
        lh.setLoginType(LoginType.PASSWORD);
        lh.setUserAgent(userAgent);
        lh.setClientIp(clientIp);
        lh.setResult(LoginResult.PASSWD_ERROR);

        loginHistoryMapper.insert(lh);
    }
}
