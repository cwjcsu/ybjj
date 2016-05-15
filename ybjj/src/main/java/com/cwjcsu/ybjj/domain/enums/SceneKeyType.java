package com.cwjcsu.ybjj.domain.enums;

/**
 * 微信二维码场景类型，如登录、绑定等等
 * <p/>
 */
public enum SceneKeyType implements IdEnum {
    SCENE_KEY_LOGIN("wxlogin"),//登录
    SCENE_KEY_BIND("wxbind");//绑定;

    private String sceneKey;

    private SceneKeyType(String sceneKey) {
        this.sceneKey = sceneKey;
    }

    public String getSceneKey() {
        return sceneKey;
    }

    @Override
    public int getId() {
        return 0;
    }
}
