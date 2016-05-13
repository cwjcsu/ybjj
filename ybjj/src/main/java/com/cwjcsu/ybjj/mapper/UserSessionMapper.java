package com.cwjcsu.ybjj.mapper;

import com.cwjcsu.ybjj.domain.UserSession;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public interface UserSessionMapper extends BaseMapper<UserSession> {
    List<UserSession> selectByFields(@Param("map") Map<String, Object> map);

    List<UserSession> selectExpiredSession();

    List<UserSession> selectNotExpiredByUserId(Integer userId);
}