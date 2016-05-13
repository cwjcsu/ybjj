/*
 --------------------------------------
  Skybility
 ---------------------------------------
  Copyright By Skybility ,All right Reserved
 * author                     date    comment
 * chenweijun@skybility.com  2015/8/10  Created
 */
package com.cwjcsu.weixin;

import com.cwjcsu.ybjj.domain.WxQrCode;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpConfigStorage;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.WxMpCustomMessage;
import me.chanjar.weixin.mp.bean.result.WxMpUser;

import java.util.List;
import java.util.Map;

/**
 * @author atlas
 */
public interface WeixinService {

    /**
     * 检查二维码是否在seconds之内过期，如果没有过期则返回数据库记录，如果过期则返回null。如果是永久二维码，则会返回WxQrCode对象
     *
     * @param uuid
     * @param seconds
     * @param delete  =true时如果过期则删除数据库记录
     * @return
     */
    WxQrCode checkAndGetWxQrCode(String uuid, int seconds, boolean delete);

    interface Releaser {
        void release();
    }

    interface WxQrCodeScanListener {
        /**
         * 当一个二维码被扫描的时候会进行的通知
         *
         * @param qrCode
         * @return true 继续由后面的listener处理，false时不处理
         */
        boolean onQrCodeScaned(WxQrCode qrCode, WxRequestContext context) throws Exception;
    }

    /**
     * 添加一个scan监听器
     *
     * @param secenKey
     * @param scanListener
     */
    void addWxQrCodeScanListener(String secenKey, WxQrCodeScanListener scanListener);

    /**
     * @param secenKey      二维码关联的场景key
     * @param attachement   二维码关联的业务数据
     * @param expireSeconds 二维码的超时，如果为null则创建永久二维码，否则创建临时二维码
     * @return
     */
    WxQrCode createWxQrCode(String secenKey, Object attachement, Integer expireSeconds) throws WxErrorException;

    /**
     * @param userId        二维码所属用户id
     * @param openId        二维码所属微信用户
     * @param secenKey      二维码关联的场景key
     * @param attachement   二维码关联的业务数据
     * @param expireSeconds 二维码的超时，如果为null则创建永久二维码，否则创建临时二维码
     * @return
     * @throws WxErrorException
     */
    WxQrCode createWxQrCode(Integer userId, String openId, String secenKey, Object attachement, Integer expireSeconds) throws WxErrorException;

    void updateWxQrCode(WxQrCode wxQrCode);

    WxQrCode findWxQrCodeByUUID(String uuid);

    WxMpMessageRouter getWxMpMessageRouter();

    WxMpConfigStorage getWxMpConfigStorage();

    void sendTemplateMessage(String openId, String templateId, String url, String topColor, Map<String, String> values,
                             Map<String, String> colors) throws WxErrorException;

    void sendTemplateMessage(String openId, String templateId, String url, Map<String, String> values) throws WxErrorException;

    WxMpService getWxMpService();

    boolean isWeixinEnabled();

    WxMpUser getWxMpUserByCode(String code) throws WxErrorException;

    WxMpUser getWxMpUserByOpenId(String openId) throws WxErrorException;


    public enum OAuth2Scope {
        snsapi_base,
        snsapi_userinfo
    }

    String getOAuth2AuthorizationUrl(OAuth2Scope scope, String state);

    void sendTextMessage(String openId, String text) throws WxErrorException;

    /**
     * 发送图文消息，其中WxMpCustomMessage.WxArticle可以这样创建：
     * <code>
     * WxMpCustomMessage.WxArticle article1 = new WxMpCustomMessage.WxArticle();
     * article1.setUrl("URL");
     * article1.setPicUrl("PIC_URL");
     * article1.setDescription("Is Really A Happy Day");
     * article1.setTitle("Happy Day");
     * </code>
     *
     * @param openId
     * @param articles
     */
    public void sendNewsMessage(String openId, List<WxMpCustomMessage.WxArticle> articles) throws WxErrorException;


    /**
     * 获取微信相关的资源文件的url，在webapp/wx/static/ 目录下
     *
     * @param resourceName
     * @return
     */
    public String getResourceUrl(String resourceName);

}
