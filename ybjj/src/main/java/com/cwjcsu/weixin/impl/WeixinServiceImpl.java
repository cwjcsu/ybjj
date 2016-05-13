/*
 --------------------------------------
  Skybility
 ---------------------------------------
  Copyright By Skybility ,All right Reserved
 * author                     date    comment
 * chenweijun@skybility.com  2015/8/10  Created
 */
package com.cwjcsu.weixin.impl;


import com.cwjcsu.common.service.PropertyService;
import com.cwjcsu.common.util.DictImpl;
import com.cwjcsu.common.util.StringUtil;
import com.cwjcsu.weixin.WeixinService;
import com.cwjcsu.weixin.WxRequestContext;
import com.cwjcsu.ybjj.domain.OpenAccount;
import com.cwjcsu.ybjj.domain.WxQrCode;
import com.cwjcsu.ybjj.domain.enums.QrStatus;
import com.cwjcsu.ybjj.mapper.WxQrCodeMapper;
import com.cwjcsu.ybjj.service.OpenAccountService;
import com.cwjcsu.ybjj.service.UserSessionService;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.*;
import me.chanjar.weixin.mp.bean.*;
import me.chanjar.weixin.mp.bean.custombuilder.NewsBuilder;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * @author atlas
 */
@Service
public class WeixinServiceImpl implements WeixinService {

    private Logger log = LoggerFactory.getLogger(WeixinService.class);

    public static final String KEY_WEIXIN_LOGIN = "weixin.logininfo";

    private WxMpConfigStorage wxMpConfigStorage;
    private WxMpService wxMpService;
    private WxMpMessageRouter wxMpMessageRouter;

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private OpenAccountService openAccountService;
    @Autowired
    private UserSessionService userSessionService;

    @Autowired
    private WxQrCodeMapper wxQrCodeMapper;

    private Map<String, List<WxQrCodeScanListener>> listeners = new HashMap<String, List<WxQrCodeScanListener>>();

    private boolean weixinEnabled = false;


    private void doInitWxMpService() {
        if (Boolean.TRUE.equals(propertyService.getAsBoolean("weixin.enabled"))) {
            weixinEnabled = true;
        } else {
            weixinEnabled = false;
        }
        if (!weixinEnabled) {
            log.error("微信功能没有启用，终止初始化WxMpController");
            return;
        }
        WxMpInMemoryConfigStorage config = new WxMpInMemoryConfigStorage();
        config.setAppId(propertyService.getAsString("weixin.appId")); // 设置微信公众号的appid
        config.setSecret(propertyService.getAsString("weixin.secret")); // 设置微信公众号的app corpSecret
        config.setToken(propertyService.getAsString("weixin.token")); // 设置微信公众号的token
        config.setAesKey(propertyService.getAsString("weixin.aesKey")); // 设置微信公众号的EncodingAESKey
        config.setOauth2redirectUri(propertyService.getAsString("weixin.redirectUri"));
        wxMpConfigStorage = config;
        wxMpService = new WxMpServiceImpl();
        wxMpService.setWxMpConfigStorage(wxMpConfigStorage);
        wxMpMessageRouter = new WxMpMessageRouter(wxMpService);
        addRouterRules(wxMpMessageRouter);
    }

    @PostConstruct
    private void initWxMpService() {
        if (wxMpService == null) {
            synchronized (this) {
                if (wxMpService == null) {
                    doInitWxMpService();
                }
            }
        }
    }

    private void checkWeixinEnabled() {
        if (!isWeixinEnabled()) {
            throw new IllegalStateException("微信功能没有启用");
        }
    }

    private void addRouterRules(WxMpMessageRouter router) {
        router.rule().async(true).event("unsubscribe").handler(getUnSubscribeHandler()).end();

        router.rule()
                .async(true)
                .matcher(new WxMpMessageMatcher() {//所有扫描事件：包括关注扫描和带参数扫描
                    @Override
                    public boolean match(WxMpXmlMessage message) {
                        return "event".equals(message.getMsgType()) && message.getTicket() != null;
                    }
                })
                .handler(getQrCodeHandler())
                .end();

        //"人工服务"菜单转多客服处理
        router.rule()
                .async(false)
                .matcher(new WxMpMessageMatcher() {//所有消息,以及点击"客服"菜单
                    @Override
                    public boolean match(WxMpXmlMessage message) {
                        return (!"event".equals(message.getMsgType()))||//所有消息
                                ("event".equals(message.getMsgType()) && "CLICK".equals(message.getEvent())
                                        && "MANUAL_SERVICE".equals(message.getEventKey()));//"客服"菜单
                    }
                })
                .handler(getCustomerServiceHandler()).end();
    }

    private WxMpMessageHandler getCustomerServiceHandler() {
        return new WxMpMessageHandler() {
            @Override
            public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage, Map<String, Object> context, WxMpService wxMpService,
                                            WxSessionManager sessionManager) throws WxErrorException {
                try {
                    sendTextMessage(wxMessage.getFromUserName(), "您好，正在为您接入客服，请稍等...");
                } catch (WxErrorException e) {
                    log.error("发送客服消息失败(openid={}):{}", wxMessage.getFromUserName(), e.toString());
                }
                return WxMpXmlOutMessage.TRANSFER_CUSTOMER_SERVICE().fromUser(wxMessage.getToUserName())
                        .toUser(wxMessage.getFromUserName()).build();
            }
        };
    }

    public WxMpMessageHandler getUnSubscribeHandler() {
        return new WxMpMessageHandler() {
            @Override
            public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage, Map<String, Object> context, WxMpService wxMpService,
                                            WxSessionManager sessionManager) throws WxErrorException {
                String openId = wxMessage.getFromUserName();
                OpenAccount openAccount = openAccountService.selectOpenAccountByOpenId(openId);
                if(openAccount!=null) {
                    userSessionService.deleteUserSessionByOpenAccountId(openAccount.getId());
                }
                return null;
            }
        };
    }

    @Override
    public void addWxQrCodeScanListener(String secenKey, WxQrCodeScanListener scanListener) {
        List<WxQrCodeScanListener> list = listeners.get(secenKey);
        if (list == null) {
            synchronized (listeners) {
                list = listeners.get(secenKey);
                if (list == null) {
                    list = new ArrayList<WxQrCodeScanListener>();
                    listeners.put(secenKey, list);
                }
            }
        }
        if (!list.contains(scanListener)) {
            list.add(scanListener);
        }
    }

    @Override
    public WxQrCode createWxQrCode(String secenKey, Object attachement, Integer expireSeconds) throws WxErrorException {
        return createWxQrCode(null, null, secenKey, attachement, expireSeconds);
    }

    @Override
    public WxQrCode createWxQrCode(Integer userId, String openId, String secenKey, Object attachement, Integer expireSeconds) throws WxErrorException {
        checkWeixinEnabled();
        WxQrCode wxQrCode = new WxQrCode();
        if (expireSeconds != null) {
            if (expireSeconds < 60 || expireSeconds > 604800) {
                throw new IllegalArgumentException("expireSeconds must be between [60,604800]");
            }
        }
        if (StringUtil.isEmpty(secenKey)) {
            throw new IllegalArgumentException("secenKey must not be null");
        }
        wxQrCode.setStatus(QrStatus.WAIT_SCAN);
        wxQrCode.setUuid(StringUtil.generateUUID());
        if (attachement instanceof String) {
            wxQrCode.setAttachement((String) attachement);
        } else if (attachement != null) {
            wxQrCode.setAttachementAsObject(attachement);
        }
        wxQrCode.setCreateTime(new Date(System.currentTimeMillis() + 2000));
        wxQrCode.setSceneKey(secenKey);
        WxMpQrCodeTicket ticket = null;
        if (expireSeconds != null) {
            ticket = wxMpService.qrCodeCreateTmpTicket(1, expireSeconds);
        } else {
            ticket = wxMpService.qrCodeCreateLastTicket(secenKey);
        }
        wxQrCode.setTicket(ticket.getTicket());
        wxQrCode.setExpireSeconds(ticket.getExpire_seconds());
        if (userId != null) {
            wxQrCode.setTargetId(userId);
        }
        if (openId != null) {
            wxQrCode.setOpenId(openId);
        }
        wxQrCodeMapper.insert(wxQrCode);
        return wxQrCode;
    }

    @Override
    public void updateWxQrCode(WxQrCode wxQrCode) {
        wxQrCodeMapper.updateByPrimaryKey(wxQrCode);
    }

    @Override
    public WxQrCode findWxQrCodeByUUID(String uuid) {
        return wxQrCodeMapper.findByUUID(uuid);
    }

    @Override
    public WxQrCode checkAndGetWxQrCode(String uuid, int seconds, boolean delete) {
        checkWeixinEnabled();
        WxQrCode wxQrCode = wxQrCodeMapper.findByUUID(uuid);
        boolean expired = false;
        if (wxQrCode.getExpireSeconds() != null && wxQrCode.getExpireSeconds() > 0) {
            if (wxQrCode.getCreateTime().getTime() + wxQrCode.getExpireSeconds() * 1000 - seconds * 1000 <= System.currentTimeMillis()) {
                expired = true;
            }
        }
        if (expired && delete) {
            wxQrCodeMapper.deleteByUUID(uuid);
            return null;
        }
        return wxQrCode;
    }

    @Override
    public WxMpUser getWxMpUserByCode(String code) throws WxErrorException {
        checkWeixinEnabled();
        WxMpOAuth2AccessToken wxMpOAuth2AccessToken = wxMpService.oauth2getAccessToken(code);
        try {
            return wxMpService.oauth2getUserInfo(wxMpOAuth2AccessToken, null);
        } catch (WxErrorException e) {
            log.error("oauth2getUserInfo error :" + e);
            return getWxMpUserByOpenId(wxMpOAuth2AccessToken.getOpenId());
        }
    }

    @Override
    public WxMpUser getWxMpUserByOpenId(String openId) throws WxErrorException {
        return getWxMpService().userInfo(openId, "zh-cn");
    }

    @Override
    public String getOAuth2AuthorizationUrl(OAuth2Scope scope, String state) {
        return wxMpService.oauth2buildAuthorizationUrl(scope.name(), state);
    }

    @Override
    public void sendTextMessage(String openId, String text) throws WxErrorException {
        //长度有2048字符限制（in bytes）
        WxMpCustomMessage wcm = WxMpCustomMessage.TEXT()
                .content(text)
                .toUser(openId)
                .build();
        getWxMpService().customMessageSend(wcm);
    }

    @Override
    public void sendNewsMessage(String openId, List<WxMpCustomMessage.WxArticle> articles) throws WxErrorException {
        NewsBuilder newsBuilder = WxMpCustomMessage.NEWS();
        for (WxMpCustomMessage.WxArticle article : articles) {
            newsBuilder.addArticle(article);
        }
        WxMpCustomMessage wcm = newsBuilder.toUser(openId).build();
        getWxMpService().customMessageSend(wcm);
    }

    @Override
    public void sendTemplateMessage(String openId, String templateId, String url, String topColor, Map<String, String> values,
                                    Map<String, String> colors) throws WxErrorException {

        WxMpTemplateMessage wtm = new WxMpTemplateMessage();
        wtm.setTemplateId(templateId);
        if (!StringUtil.isEmpty(topColor)) {
            wtm.setTopColor(topColor);
        }
        wtm.setToUser(openId);
        wtm.setUrl(url);
        List<WxMpTemplateData> datas = new ArrayList<WxMpTemplateData>();
        Iterator<Map.Entry<String, String>> entryIterator = values.entrySet().iterator();
        while (entryIterator.hasNext()) {
            Map.Entry<String, String> entry = entryIterator.next();
            if (colors != null && colors.containsKey(entry.getKey())) {
                datas.add(new WxMpTemplateData(entry.getKey(), entry.getValue(), colors.get(entry.getKey())));
            } else {
                datas.add(new WxMpTemplateData(entry.getKey(), entry.getValue()));
            }

        }
        wtm.setDatas(datas);
        getWxMpService().templateSend(wtm);
    }

    @Override
    public void sendTemplateMessage(String openId, String templateId, String url, Map<String, String> values) throws WxErrorException {
        sendTemplateMessage(openId, templateId, url, null, values, null);
    }

    private void onQrCodeScaned(WxMpXmlMessage wxMessage, Map<String, Object> context, WxMpService wxMpService, WxSessionManager sessionManager) {
        String ticket = wxMessage.getTicket();
        WxQrCode wxQrCode = wxQrCodeMapper.findByTicket(ticket);
        if (wxQrCode == null || wxQrCode.getStatus() == null || !wxQrCode.getStatus().isValid()) {
            try {
                log.warn("扫描失效的二维码" + (wxQrCode != null ? ("qrcode [id=" + wxQrCode.getId() + ",status " + wxQrCode.getStatus().name() + "]") : "null"));
                sendTextMessage(wxMessage.getFromUserName(), "您扫描的二维码已失效");
            } catch (WxErrorException e) {
                log.error("发送客服消息失败(openid={}):{}", wxMessage.getFromUserName(), e.toString());
            }
            return;
        }

        List<WxQrCodeScanListener> list = listeners.get(wxQrCode.getSceneKey());
        if (list == null || list.size() == 0) {
            log.warn("no WxQrCodeScanListener for scene key {}", wxQrCode.getSceneKey());
            return;
        }
        try {
            WxRequestContext ctx = new WxRequestContext();
            ctx.setParameters(new DictImpl(context));
            ctx.setSessionManager(sessionManager);
            ctx.setWxMessage(wxMessage);
            ctx.setWxMpService(wxMpService);
            for (WxQrCodeScanListener listener : list) {
                if (!listener.onQrCodeScaned(wxQrCode, ctx)) {
                    break;
                }
            }
        } catch (Exception e) {
            log.warn("error handling QrCode {}", ticket, e);
        }
    }

    public WxMpMessageHandler getQrCodeHandler() {
        return new WxMpMessageHandler() {
            @Override
            public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage, Map<String, Object> context, WxMpService wxMpService, WxSessionManager sessionManager) throws WxErrorException {
                onQrCodeScaned(wxMessage, context, wxMpService, sessionManager);
                return null;
            }
        };
    }

    @Override
    public WxMpService getWxMpService() {
        checkWeixinEnabled();
        return wxMpService;
    }

    public WxMpMessageRouter getWxMpMessageRouter() {
        checkWeixinEnabled();
        return wxMpMessageRouter;
    }

    @Override
    public boolean isWeixinEnabled() {
        return weixinEnabled;
    }

    @Override
    public WxMpConfigStorage getWxMpConfigStorage() {
        checkWeixinEnabled();
        return wxMpConfigStorage;
    }

    public String getResourceUrl(String resourceName) {
        return StringUtil.joinPath(propertyService.getAsString("weixin.resource.url"), resourceName);
    }

}
