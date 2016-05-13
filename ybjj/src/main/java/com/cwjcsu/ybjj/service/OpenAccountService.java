/*
 --------------------------------------
  Skybility
 ---------------------------------------
  Copyright By Skybility ,All right Reserved
 * author                     date    comment
 * chenweijun@skybility.com  2015/8/20  Created
 */
package com.cwjcsu.ybjj.service;


import com.cwjcsu.ybjj.domain.OpenAccount;
import com.cwjcsu.ybjj.domain.User;
import com.cwjcsu.ybjj.domain.enums.OauthType;
import me.chanjar.weixin.mp.bean.result.WxMpUser;

import java.util.List;

/**
 * @author Atlas
 */
public interface OpenAccountService {


    User createWeixinOpenAccount(WxMpUser wxUser);

    OpenAccount bindWeixinOpenAccount(User user, WxMpUser wxUser);


    List<OpenAccount> selectOpenAccountsByUserId(Integer userId);

    OpenAccount selectOpenAccountByUserId(int oauthType, Integer userId);

    OpenAccount selectOpenAccountByOpenId(String openId);

    void deleteOpenAccountByUserId(int oauthType, Integer userId);

    void deleteByOpenIdAndType(String openId, OauthType oauthType);

    void updateUserInfo(User user);

    OpenAccount selectOpenAccountById(Integer openAccountId);
}
