package com.cwjcsu.ybjj.mapper;

import com.cwjcsu.ybjj.domain.UserWebToken;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface UserWebTokenMapper extends BaseMapper<UserWebToken> {
    UserWebToken selectByTokenId(@Param("tokenId") String tokenId);

    List<UserWebToken> findByOpenAccountId(@Param("openAccountId") Integer openAccountId);

    int deleteExpiredWebToken();
}