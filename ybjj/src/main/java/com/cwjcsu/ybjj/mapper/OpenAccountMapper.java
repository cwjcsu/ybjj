package com.cwjcsu.ybjj.mapper;

import com.cwjcsu.ybjj.domain.OpenAccount;
import com.cwjcsu.ybjj.domain.enums.OauthType;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface OpenAccountMapper extends BaseMapper<OpenAccount> {
    OpenAccount selectByOpenId(@Param("oauthType") int type, @Param("openId") String openId);

    List<OpenAccount> selectOpenAccountsByUserId(@Param("userId") int userId);

    OpenAccount selectByUserId(@Param("oauthType") int oauthType, @Param("userId") Integer userId);

    void deleteByUserId(@Param("oauthType") int oauthType, @Param("userId") Integer userId);

    void deleteByOpenIdAndType(@Param("openId") String openId, @Param("oauthType") OauthType oauthType);

    OpenAccount selectOpenAccountByOpenId(String openId);
}