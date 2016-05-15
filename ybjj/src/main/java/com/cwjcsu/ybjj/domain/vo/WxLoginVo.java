/*
 --------------------------------------
  Skybility
 ---------------------------------------
  Copyright By Skybility ,All right Reserved
 * author                     date    comment
 * chenweijun@skybility.com  2015/8/28  Created
 */
package com.cwjcsu.ybjj.domain.vo;

import me.chanjar.weixin.mp.bean.result.WxMpUser;

/**
 * @author Atlas
 */
public class WxLoginVo {
    //扫描的微信用户
    private WxMpUser wxUser;

    //关联或者创建的行云网账户id:User.id
    private Integer id;
    //行云网用户昵称
    private String nickname;

    public WxMpUser getWxUser() {
        return wxUser;
    }

    public void setWxUser(WxMpUser wxUser) {
        this.wxUser = wxUser;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
