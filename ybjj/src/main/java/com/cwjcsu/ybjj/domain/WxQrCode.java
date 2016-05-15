package com.cwjcsu.ybjj.domain;

import com.cwjcsu.common.util.Dict;
import com.cwjcsu.common.util.DictImpl;
import com.cwjcsu.common.util.JsonUtil;
import com.cwjcsu.common.util.StringUtil;
import com.cwjcsu.ybjj.domain.enums.QrStatus;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;

public class WxQrCode extends AbstractEntity {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column wx_qrcode.id
     *
     * @mbggenerated
     */
    private Integer id;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column wx_qrcode.uuid
     *
     * @mbggenerated
     */
    private String uuid;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column wx_qrcode.ticket
     *
     * @mbggenerated
     */
    private String ticket;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column wx_qrcode.target_id
     *
     * @mbggenerated
     */
    private Integer targetId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column wx_qrcode.open_id
     *
     * @mbggenerated
     */
    private String openId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column wx_qrcode.status
     *
     * @mbggenerated
     */
    private QrStatus status;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column wx_qrcode.remark
     *
     * @mbggenerated
     */
    private String remark;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column wx_qrcode.create_time
     *
     * @mbggenerated
     */
    private Date createTime;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column wx_qrcode.expire_seconds
     *
     * @mbggenerated
     */
    private Integer expireSeconds;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column wx_qrcode.attachement
     *
     * @mbggenerated
     */
    private String attachement;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column wx_qrcode.scene_key
     *
     * @mbggenerated
     */
    private String sceneKey;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column wx_qrcode.id
     *
     * @return the value of wx_qrcode.id
     *
     * @mbggenerated
     */
    public Integer getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column wx_qrcode.id
     *
     * @param id the value for wx_qrcode.id
     *
     * @mbggenerated
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column wx_qrcode.uuid
     *
     * @return the value of wx_qrcode.uuid
     *
     * @mbggenerated
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column wx_qrcode.uuid
     *
     * @param uuid the value for wx_qrcode.uuid
     *
     * @mbggenerated
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column wx_qrcode.ticket
     *
     * @return the value of wx_qrcode.ticket
     *
     * @mbggenerated
     */
    public String getTicket() {
        return ticket;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column wx_qrcode.ticket
     *
     * @param ticket the value for wx_qrcode.ticket
     *
     * @mbggenerated
     */
    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column wx_qrcode.target_id
     *
     * @return the value of wx_qrcode.target_id
     *
     * @mbggenerated
     */
    public Integer getTargetId() {
        return targetId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column wx_qrcode.target_id
     *
     * @param targetId the value for wx_qrcode.target_id
     *
     * @mbggenerated
     */
    public void setTargetId(Integer targetId) {
        this.targetId = targetId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column wx_qrcode.open_id
     *
     * @return the value of wx_qrcode.open_id
     *
     * @mbggenerated
     */
    public String getOpenId() {
        return openId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column wx_qrcode.open_id
     *
     * @param openId the value for wx_qrcode.open_id
     *
     * @mbggenerated
     */
    public void setOpenId(String openId) {
        this.openId = openId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column wx_qrcode.status
     *
     * @return the value of wx_qrcode.status
     *
     * @mbggenerated
     */
    public QrStatus getStatus() {
        return status;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column wx_qrcode.status
     *
     * @param status the value for wx_qrcode.status
     *
     * @mbggenerated
     */
    public void setStatus(QrStatus status) {
        this.status = status;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column wx_qrcode.remark
     *
     * @return the value of wx_qrcode.remark
     *
     * @mbggenerated
     */
    public String getRemark() {
        return remark;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column wx_qrcode.remark
     *
     * @param remark the value for wx_qrcode.remark
     *
     * @mbggenerated
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column wx_qrcode.create_time
     *
     * @return the value of wx_qrcode.create_time
     *
     * @mbggenerated
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column wx_qrcode.create_time
     *
     * @param createTime the value for wx_qrcode.create_time
     *
     * @mbggenerated
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column wx_qrcode.expire_seconds
     *
     * @return the value of wx_qrcode.expire_seconds
     *
     * @mbggenerated
     */
    public Integer getExpireSeconds() {
        return expireSeconds;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column wx_qrcode.expire_seconds
     *
     * @param expireSeconds the value for wx_qrcode.expire_seconds
     *
     * @mbggenerated
     */
    public void setExpireSeconds(Integer expireSeconds) {
        this.expireSeconds = expireSeconds;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column wx_qrcode.attachement
     *
     * @return the value of wx_qrcode.attachement
     *
     * @mbggenerated
     */
    public String getAttachement() {
        return attachement;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column wx_qrcode.attachement
     *
     * @param attachement the value for wx_qrcode.attachement
     *
     * @mbggenerated
     */
    public void setAttachement(String attachement) {
        this.attachement = attachement;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column wx_qrcode.scene_key
     *
     * @return the value of wx_qrcode.scene_key
     *
     * @mbggenerated
     */
    public String getSceneKey() {
        return sceneKey;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column wx_qrcode.scene_key
     *
     * @param sceneKey the value for wx_qrcode.scene_key
     *
     * @mbggenerated
     */
    public void setSceneKey(String sceneKey) {
        this.sceneKey = sceneKey;
    }

    //-------------------自定义方法

    public Dict getAttachementAsDict() {
        String attachment = getAttachement();
        return new DictImpl(JsonUtil.fromJsonAsMap(attachment));
    }

    public void setAttachementAsDict(Dict dict) {
        setAttachement(JsonUtil.jsonEncode(dict.getMap()));
    }

    public <T> T getAttachementAsType(Class<T> type) {
        String attachment = getAttachement();
        if (StringUtil.notEmpty(attachment))
            return JsonUtil.fromJson(attachment, type);
        return null;
    }

    public void setAttachementAsObject(Object t) {
        if (t != null) {
            setAttachement(JsonUtil.jsonEncode(t));
        } else {
            setAttachement(null);
        }
    }

    /**
     * 在seconds秒后是否过期
     *
     * @param seconds
     * @return
     */
    public boolean isExpiredAfterSeconds(int seconds) {
        return getValidSeconds() >= seconds;
    }

    /**
     * 二维码是否可用（未扫描或者已扫描但业务还没有做完）
     *
     * @return
     */
    public boolean isActive() {
        return !StringUtil.in(getStatus(), QrStatus.SUCCESS, QrStatus.REJECTED, QrStatus.EXPIRED,QrStatus.FAIL);
    }

    /**
     * 从现在时刻算起，获取二维码有效的时间（单位：秒）
     *
     * @return
     */
    public int getValidSeconds() {
        return (int) ((getCreateTime().getTime() + getExpireSeconds() * 1000 - System.currentTimeMillis()) / 1000);
    }

    public String getQrCodeUrl() {
        try {
            return "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=" + URLEncoder.encode(getTicket(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=" + getTicket();
        }
    }
}