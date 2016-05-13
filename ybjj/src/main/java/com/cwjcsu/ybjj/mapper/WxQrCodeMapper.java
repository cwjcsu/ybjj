package com.cwjcsu.ybjj.mapper;

import com.cwjcsu.ybjj.domain.WxQrCode;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

@Component
public interface WxQrCodeMapper extends BaseMapper<WxQrCode> {

    WxQrCode findByTicket(@Param("ticket") String ticket);

    int deleteByTicket(@Param("ticket") String ticket);

    WxQrCode findByUUID(@Param("uuid") String uuid);

    int deleteByUUID(@Param("uuid") String uuid);

    int deleteByTargetIdAndSceneKey(@Param("targetId") int targetId, @Param("sceneKey") String sceneKey);

    WxQrCode selectLatestByTargetIdAndSceneKey(@Param("targetId") int targetId, @Param("sceneKey") String sceneKey);
}