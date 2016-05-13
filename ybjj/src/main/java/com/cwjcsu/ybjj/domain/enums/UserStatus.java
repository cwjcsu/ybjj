package com.cwjcsu.ybjj.domain.enums;

/**
 * 用户状态
 *
 * @author ye
 */
public enum UserStatus implements IdEnum {
    INACTIVE(0),           // 未激活
    ACTIVE(1),             // 激活
    BLOCK(2),              // 被禁用
    DELETED(3);             // 标记为删除

    private int id;

    UserStatus(int id) {
        this.id = id;
    }
    @Override
    public int getId() {
        return id;
    }
}
