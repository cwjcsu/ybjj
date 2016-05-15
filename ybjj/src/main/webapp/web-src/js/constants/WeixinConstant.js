/**
 * 微信相关常量
 */


//微信场景key，与后台的SceneKeyType枚举定义的一致
var sceneKey = {
    SCENE_KEY_LOGIN: 'wxlogin',//登录
};


//微信二维码状态，与后台的QrStatus枚举定义的一致
var qrCodeStatus = {
    WAIT_SCAN : 0, //等待扫描
    SCANNED : 1, //已经扫描
    SUCCESS : 2, //业务成功
    REJECTED : 3,//用户拒绝
    EXPIRED : 4, //二维码过期
    FAIL : 5 //业务失败
};

var WeixinConstant = {
    sceneKey : sceneKey,
    qrCodeStatus : qrCodeStatus
};


module.exports = WeixinConstant;