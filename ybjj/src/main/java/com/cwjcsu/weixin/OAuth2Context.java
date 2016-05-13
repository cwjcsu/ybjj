/*
 --------------------------------------
  Skybility
 ---------------------------------------
  Copyright By Skybility ,All right Reserved
 * author                     date    comment
 * chenweijun@skybility.com  2015/8/17  Created
 */
package com.cwjcsu.weixin;

import com.cwjcsu.common.util.Dict;
import com.cwjcsu.common.util.DictImpl;
import com.cwjcsu.ybjj.domain.User;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.springframework.ui.ModelMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.Map;

/**
 * 微信OAauth2请求的上下文
 *
 * @author Atlas
 */
public class OAuth2Context {
    private HttpServletRequest request;
    private HttpServletResponse response;

    private String state;

    private Dict stateParameters = new DictImpl(Collections.emptyMap());

    private WxMpUser wxMpUser;

    private ModelMap modelMap;

    /**
     * 绑定的行云网账户
     */
    private User user;

    private StateInvoker stateInvoker;

    public HttpServletRequest getRequest() {
        return request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public void setResponse(HttpServletResponse response) {
        this.response = response;
    }

    public String getState() {
        return state;
    }

    public String getBaseState() {
        return stateInvoker.getBaseState();
    }

    public void setState(String state) {
        this.state = state;
    }

    public Dict getStateParameters() {
        return stateParameters;
    }

    public String getOpenId() {
        return wxMpUser.getOpenId();
    }

    public void setStateParameters(Map stateParameters) {
        this.stateParameters = new DictImpl(stateParameters);
    }


    public WxMpUser getWxMpUser() {
        return wxMpUser;
    }

    public void setWxMpUser(WxMpUser wxMpUser) {
        this.wxMpUser = wxMpUser;
    }

    public ModelMap getModelMap() {
        return modelMap;
    }

    public void setModelMap(ModelMap modelMap) {
        this.modelMap = modelMap;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setStateParameters(Dict stateParameters) {
        this.stateParameters = stateParameters;
    }

    public StateInvoker getStateInvoker() {
        return stateInvoker;
    }

    public void setStateInvoker(StateInvoker stateInvoker) {
        this.stateInvoker = stateInvoker;
    }
}
