/*
 --------------------------------------
  Skybility
 ---------------------------------------
  Copyright By Skybility ,All right Reserved
 * author                     date    comment
 * chenweijun@skybility.com  2015/8/20  Created
 */
package com.cwjcsu.ybjj.service.impl;

import com.cwjcsu.common.service.PropertyService;
import com.cwjcsu.common.util.RandomUtil;
import com.cwjcsu.common.util.StringUtil;
import com.cwjcsu.common.util._;
import com.cwjcsu.weixin.WeixinService;
import com.cwjcsu.ybjj.domain.OpenAccount;
import com.cwjcsu.ybjj.domain.User;
import com.cwjcsu.ybjj.domain.UserAccount;
import com.cwjcsu.ybjj.domain.enums.Gender;
import com.cwjcsu.ybjj.domain.enums.OauthType;
import com.cwjcsu.ybjj.domain.enums.UserStatus;
import com.cwjcsu.ybjj.domain.enums.UserType;
import com.cwjcsu.ybjj.mapper.OpenAccountMapper;
import com.cwjcsu.ybjj.mapper.UserMapper;
import com.cwjcsu.ybjj.service.OpenAccountService;
import com.cwjcsu.ybjj.service.UserService;
import com.cwjcsu.ybjj.service.UserSessionService;
import com.google.common.base.Optional;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.apache.commons.lang.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.collections.MapUtils.putAll;

/**
 * @author Atlas
 */
@Service
public class OpenAccountServiceImpl implements OpenAccountService {

    final static private Logger logger = LoggerFactory.getLogger(OpenAccountServiceImpl.class);

    @Autowired
    private OpenAccountMapper openAccountMapper;
    @Autowired
    private UserMapper userMapper;

    @Autowired
    WeixinService weixinService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserSessionService userSessionService;

    @Autowired
    private PropertyService propertyService;

    @Value("${weixin.template.accountChange}")
    private String weixinAccountChangeTemplate;

    @Override
    @Transactional
    public User createWeixinOpenAccount(WxMpUser wxUser) {
        OpenAccount account = createOpenAccountBy(wxUser);
        User user = createUserBy(account);
        UserAccount userAccount = new UserAccount();
        userAccount.setPassword("");
        userAccount.setPasswordSalt("");
        userService.createUser(user, userAccount);
        account.setUserId(user.getId());
        openAccountMapper.insert(account);
        return user;
    }


    @Override
    @Transactional
    public OpenAccount bindWeixinOpenAccount(User user, WxMpUser wxUser) {
        OpenAccount account = openAccountMapper.selectByOpenId(OauthType.WEIXIN.getId(), wxUser.getOpenId());
        if (account == null) {
            account = createOpenAccountBy(wxUser);
            account.setUserId(user.getId());
            openAccountMapper.insert(account);
        } else {
            account.setUserId(user.getId());
            openAccountMapper.updateByPrimaryKey(account);
        }
        return account;
    }

    @Override
    public List<OpenAccount> selectOpenAccountsByUserId(Integer userId) {
        return openAccountMapper.selectOpenAccountsByUserId(userId);
    }

    @Override
    public OpenAccount selectOpenAccountByUserId(int oauthType,Integer userId) {
        return openAccountMapper.selectByUserId(oauthType, userId);
    }

    @Override
    public OpenAccount selectOpenAccountByOpenId(String openId) {
        return openAccountMapper.selectOpenAccountByOpenId(openId);
    }

    @Override
    public void deleteOpenAccountByUserId(int oauthType,Integer userId) {
        OpenAccount openAccount = openAccountMapper.selectByUserId(oauthType, userId);
        Optional<User> userOptional = userService.getById(userId);
        cleanOnUnbindOpenAccount(userOptional.get(),openAccount);
        openAccountMapper.deleteByUserId(oauthType, userId);
        afterUnBind(userOptional.get(),openAccount);
    }

    @Override
    public void deleteByOpenIdAndType(String openId, OauthType oauthType) {
        OpenAccount openAccount = openAccountMapper.selectByOpenId(oauthType.getId(),openId);
        User user = userService.findUserByOpenID(openId, oauthType.getId());
        cleanOnUnbindOpenAccount(user,openAccount);
        openAccountMapper.deleteByOpenIdAndType(openId, oauthType);
        afterUnBind(user,openAccount);

    }

    @Override
    public void updateUserInfo(User user) {
        userMapper.updateByPrimaryKey(user);
    }

    @Override
    public OpenAccount selectOpenAccountById(Integer openAccountId) {
        return openAccountMapper.selectByPrimaryKey(openAccountId);
    }

    //解绑时做清理动作
    private void cleanOnUnbindOpenAccount(User user, OpenAccount openAccount) {
        if(openAccount!=null && user!=null && OauthType.WEIXIN.equals(openAccount.getOauthType())) {
            userSessionService.deleteUserSessionByOpenAccountId(openAccount.getId());
        }
    }

    //发送解绑成功模板消息
    private void afterUnBind(User user, OpenAccount openAccount) {
        if(openAccount!=null && user!=null && OauthType.WEIXIN.equals(openAccount.getOauthType())) {
            Map<String,String> msgParam = putAll(new HashMap<String,String>(), new String[][] { {"first", "微信解绑成功"},
                    {"account", user.getNickname()},
                    {"time", FastDateFormat.getInstance("yyyy年MM月dd日hh:mm:ss").format(new Date())},
                    {"type", "微信解绑"},
                    {"remark", _.$("此微信账号已经与{}账户“{}”成功解绑", propertyService.getAsString("portal.name"), user.getNickname())}
            });
            try {
                weixinService.sendTemplateMessage(openAccount.getOpenId(),weixinAccountChangeTemplate,null,msgParam);
            } catch (WxErrorException e) {
                logger.error("sendTemplateMessage error",e);
            }
        }
    }

    public User createUserBy(OpenAccount openAccount) {
        User user = new User();
        String randomName = RandomUtil.getRandomName();
        //这里要判断名称是否重复
        while (userService.isUserExisted(randomName)) {
            randomName = RandomUtil.getRandomName();
        }
        user.setName(randomName);
        user.setCreateTime(new Date());
        user.setAvatar(openAccount.getAvatar());
        user.setNickname(openAccount.getNickname());
        user.setOnline(true);
        user.setGender(openAccount.getGender());
        user.setUserType(UserType.NORMAL);
        user.setUserStatus(UserStatus.ACTIVE);
        user.setLocation(openAccount.getLocation());
        return user;
    }

    public OpenAccount createOpenAccountBy(WxMpUser wxUser) {
        OpenAccount user = new OpenAccount();
        user.setOauthType(OauthType.WEIXIN);
        user.setOpenId(wxUser.getOpenId());
        user.setNickname(wxUser.getNickname());
        user.setAvatar(wxUser.getHeadImgUrl());
        user.setCity(wxUser.getCity());
        if (StringUtil.in(wxUser.getSex(), "男", "1")) {
            user.setGender(Gender.MALE);
        } else if (StringUtil.in(wxUser.getSex(), "女", "2")) {
            user.setGender(Gender.FEMALE);
        } else {
            user.setGender(Gender.UNSPECIFIC);
        }

        user.setProvince(wxUser.getProvince());
        user.setLocation((wxUser.getProvince() != null ? wxUser.getProvince() : "") + (wxUser.getCity() != null ? wxUser.getCity() : ""));
        user.setCreateTime(new Date());
        return user;
    }
}
