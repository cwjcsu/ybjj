/**
 * 以Promise/A+规范的方式提供发送ajax请求的API, 使用如下用法
 * <pre><code>
 *     //import
 *     var AjaxAPI = require('./services/AjaxAPI.js');
 *
 *     //use
 *     AjaxAPI.request('/path', {
 *          data: {
 *              username: username
 *          },
 *          dataType: 'json',
 *          method: 'post'
 *     }).then(function(data){}).then(undefined, function(error){});
 * </code></pre>
 */
var $ = require('jquery');
var when = require('when');
var Logger = require('./Logger.js');
var utils = require('../util/utils.js');
var AuthConstant = require('../constants/AuthConstant.js');
var AppDispatcher = require('../dispatchers/AppDispatcher.js');

var AjaxAPI = {
    request: function (url, settings = {}) {
        settings.method = settings.method || 'post';
        var promise = when.promise(function (resolve, reject) {
            $.ajax(url, settings).done(function (data) {
                if (data.success) {
                    resolve(data);
                } else {
                    //如发现jwt不合法或jwt对应的会话已失效，则跳至登录页面
                    if(data.code === 1 || data.code === 2 || data.code === 3 ){
                        AppDispatcher.dispatch({
                            actionType: AuthConstant.AUTH_LOGOUT
                        });
                        utils.redirectToLogin();
                    } else {
                        var e = new Error(data.msg);
                        for(var i in data) {
                            e[i] = data[i];
                        }
                        reject(e);
                    }
                }
            }).fail(function (jqXHR, textStatus, error) {
                Logger.error('jqXHR: %s, textStatus: %s, error: %s', jqXHR, textStatus, error);
                reject(error);
            });
        });
        return promise;
    }
};

module.exports = AjaxAPI;
