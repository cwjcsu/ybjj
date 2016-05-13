package com.cwjcsu.ybjj.mapper;

import com.cwjcsu.ybjj.domain.LoginHistory;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface LoginHistoryMapper extends BaseMapper<LoginHistory> {
    List<LoginHistory> selectLatestByUserId(@Param("userId") Integer userId, @Param("limit") int limit);

    int deleteOldLoginHistory(int size);
}