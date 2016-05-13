package com.cwjcsu.ybjj.domain.enums;

/**
 * 用户类型
 *
 * @author ye
 */
public enum UserType implements IdEnum {
    NORMAL(0);

    private int id;

    UserType(int id) {
        this.id = id;
    }
    @Override
    public int getId() {
        return id;
    }
}
