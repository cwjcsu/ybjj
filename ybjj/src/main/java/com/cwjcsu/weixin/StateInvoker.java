/*
 --------------------------------------
  Skybility
 ---------------------------------------
  Copyright By Skybility ,All right Reserved
 * author                     date    comment
 * chenweijun@skybility.com  2015/8/17  Created
 */
package com.cwjcsu.weixin;

import com.cwjcsu.common.util.Parts;
import com.cwjcsu.common.util.WeixinUtil;
import org.springframework.web.servlet.ModelAndView;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author Atlas
 */
public class StateInvoker {
    private Object controller;
    private Method method;
    private StateMapping mapping;
    private String state;
    private Parts parts;

    public StateInvoker(Object controller, Method method, String state, StateMapping mapping) {
        this.controller = controller;
        this.method = method;
        this.mapping = mapping;
        this.state = state;
        Parts parts = WeixinUtil.paraseTemplate(state);
        if (parts.isTemplate()) {
            this.parts = parts;
        }
    }

    public boolean isTemplate() {
        return parts != null && parts.isTemplate();
    }

    public String getBaseState() {
        if (isTemplate()) {
            return parts.getParts().get(0).getPart().replaceAll("_*$", "");
        }
        return getState();
    }

    /**
     * 如果当前的Invoker匹配了state参数，则以Map的方式返回匹配后的键值对，如果没有匹配到则返回null
     *
     * @param state
     * @return
     */
    public Map<String, String> match(String state) {
        if (isTemplate()) {
            return WeixinUtil.matchTemplate(state, parts);
        }
        return null;
    }

    public Object getController() {
        return controller;
    }

    public Method getMethod() {
        return method;
    }

    public ModelAndView invoke(OAuth2Context context) throws InvocationTargetException, IllegalAccessException {
        return (ModelAndView) method.invoke(controller, context);
    }

    public boolean requireBind() {
        return mapping.requireBind();
    }

//    public boolean requireLogin() {
//        return mapping.requireLogin();
//    }

    public String getState() {
        return state;
    }

    public StateMapping getMapping() {
        return mapping;
    }

    @Override
    public String toString() {
        return "StateInvoker{" +
                "method=" + method +
                '}';
    }
}
