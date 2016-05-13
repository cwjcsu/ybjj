/*
 --------------------------------------
  Skybility
 ---------------------------------------
  Copyright By Skybility ,All right Reserved
 * author                     date    comment
 * chenweijun@skybility.com  2015/8/28  Created
 */
package com.cwjcsu.ybjj.domain.enums;


import com.cwjcsu.common.util.StringUtil;

/**
 * 临时二维码的状态
 *
 * @author Atlas
 */
public enum QrStatus implements IdEnum {
    /**
     * 等待扫描
     */
    WAIT_SCAN(0),
    /**
     * 已经扫描
     */
    SCANNED(1),
    /**
     * 业务成功
     */
    SUCCESS(2),
    /**
     * 业务失败
     */
    REJECTED(3),
    /**
     * 二维码过期
     */
    EXPIRED(4),

    /**
     * 失败
     */
    FAIL(5);


    private int id;

    private QrStatus(int id) {

        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }

    public boolean isValid() {
        return !StringUtil.in(this, EXPIRED, FAIL, REJECTED);
    }
}
