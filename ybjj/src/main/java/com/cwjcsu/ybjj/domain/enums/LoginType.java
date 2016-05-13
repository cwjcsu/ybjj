package com.cwjcsu.ybjj.domain.enums;

/**
 * @author ye
 */
public enum LoginType implements IdEnum {
    PASSWORD(0);    // 用户名密码登录方式


    private int id;

    LoginType(int i) {
        id = i;
    }

    @Override
    public int getId() {
        return id;
    }
}
