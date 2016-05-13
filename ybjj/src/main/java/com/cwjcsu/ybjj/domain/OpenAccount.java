package com.cwjcsu.ybjj.domain;

import com.cwjcsu.ybjj.domain.enums.Gender;
import com.cwjcsu.ybjj.domain.enums.OauthType;
import java.util.Date;

public class OpenAccount extends AbstractEntity {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column open_account.id
     *
     * @mbggenerated
     */
    private Integer id;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column open_account.user_id
     *
     * @mbggenerated
     */
    private Integer userId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column open_account.oauth_type
     *
     * @mbggenerated
     */
    private OauthType oauthType;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column open_account.open_id
     *
     * @mbggenerated
     */
    private String openId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column open_account.nickname
     *
     * @mbggenerated
     */
    private String nickname;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column open_account.gender
     *
     * @mbggenerated
     */
    private Gender gender;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column open_account.avatar
     *
     * @mbggenerated
     */
    private String avatar;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column open_account.create_time
     *
     * @mbggenerated
     */
    private Date createTime;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column open_account.city
     *
     * @mbggenerated
     */
    private String city;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column open_account.province
     *
     * @mbggenerated
     */
    private String province;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column open_account.location
     *
     * @mbggenerated
     */
    private String location;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column open_account.home
     *
     * @mbggenerated
     */
    private String home;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column open_account.id
     *
     * @return the value of open_account.id
     *
     * @mbggenerated
     */
    public Integer getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column open_account.id
     *
     * @param id the value for open_account.id
     *
     * @mbggenerated
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column open_account.user_id
     *
     * @return the value of open_account.user_id
     *
     * @mbggenerated
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column open_account.user_id
     *
     * @param userId the value for open_account.user_id
     *
     * @mbggenerated
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column open_account.oauth_type
     *
     * @return the value of open_account.oauth_type
     *
     * @mbggenerated
     */
    public OauthType getOauthType() {
        return oauthType;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column open_account.oauth_type
     *
     * @param oauthType the value for open_account.oauth_type
     *
     * @mbggenerated
     */
    public void setOauthType(OauthType oauthType) {
        this.oauthType = oauthType;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column open_account.open_id
     *
     * @return the value of open_account.open_id
     *
     * @mbggenerated
     */
    public String getOpenId() {
        return openId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column open_account.open_id
     *
     * @param openId the value for open_account.open_id
     *
     * @mbggenerated
     */
    public void setOpenId(String openId) {
        this.openId = openId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column open_account.nickname
     *
     * @return the value of open_account.nickname
     *
     * @mbggenerated
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column open_account.nickname
     *
     * @param nickname the value for open_account.nickname
     *
     * @mbggenerated
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column open_account.gender
     *
     * @return the value of open_account.gender
     *
     * @mbggenerated
     */
    public Gender getGender() {
        return gender;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column open_account.gender
     *
     * @param gender the value for open_account.gender
     *
     * @mbggenerated
     */
    public void setGender(Gender gender) {
        this.gender = gender;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column open_account.avatar
     *
     * @return the value of open_account.avatar
     *
     * @mbggenerated
     */
    public String getAvatar() {
        return avatar;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column open_account.avatar
     *
     * @param avatar the value for open_account.avatar
     *
     * @mbggenerated
     */
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column open_account.create_time
     *
     * @return the value of open_account.create_time
     *
     * @mbggenerated
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column open_account.create_time
     *
     * @param createTime the value for open_account.create_time
     *
     * @mbggenerated
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column open_account.city
     *
     * @return the value of open_account.city
     *
     * @mbggenerated
     */
    public String getCity() {
        return city;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column open_account.city
     *
     * @param city the value for open_account.city
     *
     * @mbggenerated
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column open_account.province
     *
     * @return the value of open_account.province
     *
     * @mbggenerated
     */
    public String getProvince() {
        return province;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column open_account.province
     *
     * @param province the value for open_account.province
     *
     * @mbggenerated
     */
    public void setProvince(String province) {
        this.province = province;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column open_account.location
     *
     * @return the value of open_account.location
     *
     * @mbggenerated
     */
    public String getLocation() {
        return location;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column open_account.location
     *
     * @param location the value for open_account.location
     *
     * @mbggenerated
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column open_account.home
     *
     * @return the value of open_account.home
     *
     * @mbggenerated
     */
    public String getHome() {
        return home;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column open_account.home
     *
     * @param home the value for open_account.home
     *
     * @mbggenerated
     */
    public void setHome(String home) {
        this.home = home;
    }
}