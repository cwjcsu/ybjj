package com.cwjcsu.ybjj.domain;

import com.cwjcsu.ybjj.domain.enums.Gender;
import com.cwjcsu.ybjj.domain.enums.UserStatus;
import com.cwjcsu.ybjj.domain.enums.UserType;
import java.util.Date;

public class User extends AbstractEntity {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column users.id
     *
     * @mbggenerated
     */
    private Integer id;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column users.name
     *
     * @mbggenerated
     */
    private String name;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column users.nickname
     *
     * @mbggenerated
     */
    private String nickname;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column users.create_time
     *
     * @mbggenerated
     */
    private Date createTime;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column users.avatar
     *
     * @mbggenerated
     */
    private String avatar;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column users.gender
     *
     * @mbggenerated
     */
    private Gender gender;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column users.user_status
     *
     * @mbggenerated
     */
    private UserStatus userStatus;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column users.online
     *
     * @mbggenerated
     */
    private Boolean online;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column users.user_type
     *
     * @mbggenerated
     */
    private UserType userType;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column users.signature
     *
     * @mbggenerated
     */
    private String signature;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column users.company
     *
     * @mbggenerated
     */
    private String company;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column users.phone
     *
     * @mbggenerated
     */
    private String phone;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column users.email
     *
     * @mbggenerated
     */
    private String email;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column users.location
     *
     * @mbggenerated
     */
    private String location;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column users.id
     *
     * @return the value of users.id
     *
     * @mbggenerated
     */
    public Integer getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column users.id
     *
     * @param id the value for users.id
     *
     * @mbggenerated
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column users.name
     *
     * @return the value of users.name
     *
     * @mbggenerated
     */
    public String getName() {
        return name;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column users.name
     *
     * @param name the value for users.name
     *
     * @mbggenerated
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column users.nickname
     *
     * @return the value of users.nickname
     *
     * @mbggenerated
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column users.nickname
     *
     * @param nickname the value for users.nickname
     *
     * @mbggenerated
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column users.create_time
     *
     * @return the value of users.create_time
     *
     * @mbggenerated
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column users.create_time
     *
     * @param createTime the value for users.create_time
     *
     * @mbggenerated
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column users.avatar
     *
     * @return the value of users.avatar
     *
     * @mbggenerated
     */
    public String getAvatar() {
        return avatar;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column users.avatar
     *
     * @param avatar the value for users.avatar
     *
     * @mbggenerated
     */
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column users.gender
     *
     * @return the value of users.gender
     *
     * @mbggenerated
     */
    public Gender getGender() {
        return gender;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column users.gender
     *
     * @param gender the value for users.gender
     *
     * @mbggenerated
     */
    public void setGender(Gender gender) {
        this.gender = gender;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column users.user_status
     *
     * @return the value of users.user_status
     *
     * @mbggenerated
     */
    public UserStatus getUserStatus() {
        return userStatus;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column users.user_status
     *
     * @param userStatus the value for users.user_status
     *
     * @mbggenerated
     */
    public void setUserStatus(UserStatus userStatus) {
        this.userStatus = userStatus;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column users.online
     *
     * @return the value of users.online
     *
     * @mbggenerated
     */
    public Boolean getOnline() {
        return online;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column users.online
     *
     * @param online the value for users.online
     *
     * @mbggenerated
     */
    public void setOnline(Boolean online) {
        this.online = online;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column users.user_type
     *
     * @return the value of users.user_type
     *
     * @mbggenerated
     */
    public UserType getUserType() {
        return userType;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column users.user_type
     *
     * @param userType the value for users.user_type
     *
     * @mbggenerated
     */
    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column users.signature
     *
     * @return the value of users.signature
     *
     * @mbggenerated
     */
    public String getSignature() {
        return signature;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column users.signature
     *
     * @param signature the value for users.signature
     *
     * @mbggenerated
     */
    public void setSignature(String signature) {
        this.signature = signature;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column users.company
     *
     * @return the value of users.company
     *
     * @mbggenerated
     */
    public String getCompany() {
        return company;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column users.company
     *
     * @param company the value for users.company
     *
     * @mbggenerated
     */
    public void setCompany(String company) {
        this.company = company;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column users.phone
     *
     * @return the value of users.phone
     *
     * @mbggenerated
     */
    public String getPhone() {
        return phone;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column users.phone
     *
     * @param phone the value for users.phone
     *
     * @mbggenerated
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column users.email
     *
     * @return the value of users.email
     *
     * @mbggenerated
     */
    public String getEmail() {
        return email;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column users.email
     *
     * @param email the value for users.email
     *
     * @mbggenerated
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column users.location
     *
     * @return the value of users.location
     *
     * @mbggenerated
     */
    public String getLocation() {
        return location;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column users.location
     *
     * @param location the value for users.location
     *
     * @mbggenerated
     */
    public void setLocation(String location) {
        this.location = location;
    }
}