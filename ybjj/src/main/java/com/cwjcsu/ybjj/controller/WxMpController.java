/*
 --------------------------------------
  Skybility
 ---------------------------------------
  Copyright By Skybility ,All right Reserved
 * author                     date    comment
 * chenweijun@skybility.com  2015/8/10  Created
 */
package com.cwjcsu.ybjj.controller;

import com.cwjcsu.weixin.WeixinService;
import me.chanjar.weixin.common.util.StringUtils;
import me.chanjar.weixin.mp.bean.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.WxMpXmlOutMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 微信消息接口
 * @author atlas
 */
@RestController
public class WxMpController {
    private Logger log = LoggerFactory.getLogger(WxMpController.class);

    @Autowired
    private WeixinService weixinService;

    @RequestMapping(value = "/weixin", method = {RequestMethod.GET, RequestMethod.POST})
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=utf-8");
        if (!weixinService.isWeixinEnabled()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND,"Weixin not enabled");
            return;
        }
        response.setStatus(HttpServletResponse.SC_OK);
        String signature = request.getParameter("signature");
        String nonce = request.getParameter("nonce");
        String timestamp = request.getParameter("timestamp");

        if (!weixinService.getWxMpService().checkSignature(timestamp, nonce, signature)) {
            // 消息签名不正确，说明不是公众平台发过来的消息
            response.getWriter().println("非法请求");
            return;
        }
        String echostr = request.getParameter("echostr");
        if (StringUtils.isNotBlank(echostr)) {
            // 说明是一个仅仅用来验证的请求，回显echostr
            response.getWriter().println(echostr);
            return;
        }
        String encryptType = StringUtils.isBlank(request.getParameter("encrypt_type")) ?
                "raw" :
                request.getParameter("encrypt_type");
        if ("raw".equals(encryptType)) {
            // 明文传输的消息
            WxMpXmlMessage inMessage = WxMpXmlMessage.fromXml(request.getInputStream());
            WxMpXmlOutMessage outMessage = weixinService.getWxMpMessageRouter().route(inMessage);
            if (outMessage != null) {
                response.getWriter().write(outMessage.toXml());
            } else {
                response.getWriter().write("");
            }
            return;
        }
        if ("aes".equals(encryptType)) {
            // 是aes加密的消息
            String msgSignature = request.getParameter("msg_signature");
            WxMpXmlMessage inMessage = WxMpXmlMessage.fromEncryptedXml(request.getInputStream(), weixinService.getWxMpConfigStorage(), timestamp, nonce, msgSignature);
            WxMpXmlOutMessage outMessage = weixinService.getWxMpMessageRouter().route(inMessage);
            if (outMessage != null) {
                response.getWriter().write(outMessage.toEncryptedXml(weixinService.getWxMpConfigStorage()));
            } else {
                response.getWriter().write("");
            }
            return;
        }
        response.getWriter().println("不可识别的加密类型");
        return;
    }

}
