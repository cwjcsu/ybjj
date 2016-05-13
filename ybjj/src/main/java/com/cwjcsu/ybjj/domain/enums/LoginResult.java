package com.cwjcsu.ybjj.domain.enums;

/**
 * @author ye
 */
public enum LoginResult implements IdEnum {
    SUCCESS(0),      // 登录成功
    PASSWD_ERROR(1), // 密码错误
    OTHER_ERROR(9);  // 其它错误

    private int id;

    LoginResult(int i) {
        id = i;
    }

    @Override
    public int getId() {
        return id;
    }
}
