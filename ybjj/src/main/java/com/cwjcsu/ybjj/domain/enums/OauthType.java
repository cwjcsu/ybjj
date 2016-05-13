package com.cwjcsu.ybjj.domain.enums;

/**
 * Created by lidonghao on 15/8/6.
 */
public enum OauthType implements IdEnum {
    QQ(0, "QQ"),
    WEIXIN(1, "微信"),
    WEIBO(2, "微博"),
    ALIYUN(3,"阿里云");

    private int id;
    private String name;

    OauthType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public int getId() {
        return id;
    }

    public String getName(){
        return name;
    }
}
