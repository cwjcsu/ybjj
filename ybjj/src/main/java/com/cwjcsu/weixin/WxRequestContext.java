/*$$
 --------------------------------------
  Skybility
 ---------------------------------------
  Copyright By Skybility ,All right Reserved
 * author                          date   comment
 * chenweijun@skybility.com       2015/8/11  Created
 */
package com.cwjcsu.weixin;

import com.cwjcsu.common.util.Dict;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.WxMpXmlMessage;

import java.util.Map;

/**
 * @author atlas
 */
public class WxRequestContext {
    private WxMpXmlMessage wxMessage;
    private Dict parameters;
    private WxMpService wxMpService;
    private WxSessionManager sessionManager;

    public WxMpXmlMessage getWxMessage() {
        return wxMessage;
    }

    public void setWxMessage(WxMpXmlMessage wxMessage) {
        this.wxMessage = wxMessage;
    }

    public Dict getDict() {
        return parameters;
    }

    public Map<String, Object> getParameters() {
        Map<String, Object> map = (Map<String, Object>) parameters.getMap();
        return map;
    }

    public String getOpenId() {
        return wxMessage.getFromUserName();
    }

    public void setParameters(Dict parameters) {
        this.parameters = parameters;
    }

    public WxMpService getWxMpService() {
        return wxMpService;
    }

    public void setWxMpService(WxMpService wxMpService) {
        this.wxMpService = wxMpService;
    }

    public WxSessionManager getSessionManager() {
        return sessionManager;
    }

    public void setSessionManager(WxSessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }
}
