package com.cwjcsu.ybjj.mapper;

import com.cwjcsu.ybjj.domain.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

@Component
public interface UserMapper extends BaseMapper<User> {
    User selectByNameOrEmailOrPhone(String name);

    User selectByEmail(@Param("email") String email);

    User selectByPhone(@Param("phone") String phone);

    User selectByOpenId(@Param("oauthType") int oauthType, @Param("openId") String openId);

    int updateOnlineStautsById(@Param("userId") Integer userId, @Param("online") boolean online);

}