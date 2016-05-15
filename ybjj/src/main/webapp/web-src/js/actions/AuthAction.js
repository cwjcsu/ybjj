var $ = require('jquery');
var when = require('when');
var Logger = require('../services/Logger.js');
var AuthConstant = require('../constants/AuthConstant');
var AppDispatcher = require('../dispatchers/AppDispatcher');
var AjaxAPI = require('../services/AjaxAPI.js');
var AppConstant = require('../constants/AppConstant');

var AuthAction = {

    cloud_login : function(account,password, rememberMe5Day, verifyCode){
        return AjaxAPI.request(AppConstant.PORTAL_CONTEXT_PATH + '/api/login', {
            data: {
                account: account,
                password:password,
                rememberMe5Day: rememberMe5Day,
                captcha : verifyCode
            },
            dataType: 'json',
            method: 'post'
        }).then(function(data){
            var jwt = data.jwt;
            AppDispatcher.dispatch({
                actionType: AuthConstant.AUTH_LOGIN,
                jwt: jwt
            });
        });
    },

    needVerifyCode : function (account) {
        return AjaxAPI.request(AppConstant.PORTAL_CONTEXT_PATH + '/api/preLogin', {
            data: {
                account: account
            },
            dataType: 'json',
            method: 'post'
        });
    },

    logout : function(jwt){
        return AjaxAPI.request(AppConstant.PORTAL_CONTEXT_PATH + '/api/logout', {
            data: {
                jwt: jwt
            },
            dataType: 'json',
            method: 'post'
        }).then(function(){
            AppDispatcher.dispatch({
                actionType: AuthConstant.AUTH_LOGOUT
            });
        });
    },

    forceLogout: function(){
        AppDispatcher.dispatch({
            actionType: AuthConstant.AUTH_LOGOUT
        });
    },

    checkJwt: function(jwt){
        return AjaxAPI.request(AppConstant.PORTAL_CONTEXT_PATH + '/api/checkJwt', {
            data: {
                jwt: jwt,
                t : new Date().getTime()
            },
            dataType: 'json',
            method: 'post',
            cache: false
        });
    },

    // 直接调用checkJwt, 还会触发重定向等逻辑判断 这里只需要判断jwt是否ok
    checkJwtIsOk: function(jwt){
        var promise = when.promise(function (resolve, reject) {
            $.ajax(AppConstant.PORTAL_CONTEXT_PATH + '/api/checkJwt', {
                data: {
                    jwt: jwt,
                    t : new Date().getTime()
                },
                dataType: 'json',
                method: 'post',
                cache: false
            }).done(function (data) {
                if (data.success) {
                    resolve(data);
                } else {
                    var e = new Error(data.msg);
                    for(var i in data) {
                        e[i] = data[i];
                    }
                    reject(e);
                }
            }).fail(function (jqXHR, textStatus, error) {
                Logger.error('jqXHR: %s, textStatus: %s, error: %s', jqXHR, textStatus, error);
                reject(error);
            });
        });
        return promise;
    },
    heartbeat: function(){
        return AjaxAPI.request(AppConstant.PORTAL_CONTEXT_PATH + '/api/heartbeat', {
            data: {
                t : new Date().getTime()
            },
            dataType: 'json',
            method: 'post',
            cache: false
        });
    },

    /**
     * 分发jwt
     * @param jwt
     */
    dispatcherJwt:function(jwt){
        AppDispatcher.dispatch({
            actionType: AuthConstant.AUTH_LOGIN,
            jwt: jwt
        });
    }

};

module.exports = AuthAction;