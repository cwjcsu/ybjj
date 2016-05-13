package com.cwjcsu.ybjj.service;

import com.cwjcsu.ybjj.domain.User;
import com.cwjcsu.ybjj.domain.UserAccount;
import com.google.common.base.Optional;

/**
 * @author ye
 */
public interface UserService {

    /**
     * 根据用户名查找用户
     */
    Optional<User> findByNameOrEmailOrPhone(String name);


//    UserVo getUserProfile(Integer userId);

    Optional<User> getById(Integer userId);

    /**
     * 根据openID和认证类型查找用户
     * @param openID
     * @param oauthType 第三方认证方式，0为QQ，1为微信，2为微博
     * @return
     */
    User findUserByOpenID(String openID, int oauthType);

    /**
     * 创建用户
     * @param user
     * @param userAccount
     * @return
     */
    int createUser(User user, UserAccount userAccount);

    /**
     * 判断用户是否已经存在，存在返回true
     * @param username
     * @return
     */
    boolean isUserExisted(String username);

    void modify(User user);

    boolean isEmailExist(String email);

    boolean isPhoneExist(String phone);

    /**
     * 根据用户ID查询用户账户信息
     * @param userId
     * @return
     */
    UserAccount selectUserAccountByUserId(int userId);

    void modifyUserAccount(UserAccount userAccount);
}
