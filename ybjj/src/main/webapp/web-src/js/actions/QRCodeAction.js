var AjaxAPI = require('../services/AjaxAPI.js');

var QRCodeAction = {
    /**
     * 获取登录二维码
     * @param sceneKey 场景Key，不能为空
     * @param userId 用户ID，可以为空
     * @returns {*}
     */
    getQRCode:function(sceneKey,userId){
        return AjaxAPI.request('/api/getWxQrCode',{
            data: {
                sceneKey : sceneKey,
                userId : userId
            },
            dataType: 'json'
        });
    },

    /**
     * 轮询扫描二维码的结果
     * @param sceneKey 场景Key，不能为空
     */
    pollScanQRCodeResult:function(sceneKey){
        return AjaxAPI.request('/api/checkScanWxResult',{
            data: {
                sceneKey : sceneKey
            },
            dataType: 'json'
        });
    }

};

module.exports = QRCodeAction;