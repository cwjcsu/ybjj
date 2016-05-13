package com.cwjcsu.ybjj.domain.enums;

/**
 * 性别
 *
 * @author ye
 */
public enum Gender implements IdEnum{
    UNSPECIFIC(0),
    MALE(1),
    FEMALE(2);

    private int id;

    Gender(int id) {
        this.id = id;
    }


    @Override
    public int getId() {
        return id;
    }
}
