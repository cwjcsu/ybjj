package com.cwjcsu.ybjj.service.impl;

import com.cwjcsu.ybjj.domain.User;
import com.cwjcsu.ybjj.domain.UserAccount;
import com.cwjcsu.ybjj.mapper.OpenAccountMapper;
import com.cwjcsu.ybjj.mapper.UserAccountMapper;
import com.cwjcsu.ybjj.mapper.UserMapper;
import com.cwjcsu.ybjj.service.UserService;
import com.google.common.base.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @author ye
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserMapper userMapper;
    @Autowired
    UserAccountMapper userAccountMapper;
    @Autowired
    OpenAccountMapper openAccountMapper;


    @Override
    public Optional<User> findByNameOrEmailOrPhone(String name) {
        User user = userMapper.selectByNameOrEmailOrPhone(name);
        return Optional.fromNullable(user);
    }

    @Override
    public Optional<User> getById(Integer userId) {
        User user = userMapper.selectByPrimaryKey(userId);
        return Optional.fromNullable(user);
    }

    @Override
    public User findUserByOpenID(String openID, int oauthType) {
        return userMapper.selectByOpenId(oauthType, openID);
    }

    @Override
    @Transactional
    public int createUser(User user, UserAccount userAccount) {
        user.setCreateTime(new Date());
        userMapper.insert(user);
        userAccount.setUserId(user.getId());
        userAccountMapper.insert(userAccount);
        return user.getId();
    }

    @Override
    public boolean isUserExisted(String username) {
        User user = userMapper.selectByNameOrEmailOrPhone(username);
        return user != null;
    }

    @Override
    public boolean isEmailExist(String email) {
        User user = userMapper.selectByEmail(email);
        return user != null;
    }

    @Override
    public boolean isPhoneExist(String phone) {
        User user = userMapper.selectByPhone(phone);
        return user != null;
    }

    @Override
    public UserAccount selectUserAccountByUserId(int userId) {
        return userAccountMapper.selectByPrimaryKey(userId);
    }

    @Override
    public void modifyUserAccount(UserAccount userAccount) {
        userAccountMapper.updateByPrimaryKey(userAccount);
    }

    @Override
    public void modify(User user) {
        userMapper.updateByPrimaryKey(user);
    }
}
