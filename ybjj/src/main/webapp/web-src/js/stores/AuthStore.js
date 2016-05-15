var extend = require('extend');
var store = require('store');
var jwt_decode = require('jwt-decode');

var BaseStore = require('./BaseStore');
var AuthConstant = require('../constants/AuthConstant');
var Cookies = require('../util/Cookies.js');

var AuthStore = extend({}, BaseStore, {

    jwt : null,

    user : null,

    getJwt : function(){
        if(!this.jwt){
            var jwt = Cookies.get('jwt');
            if(jwt && jwt !== ''){
                store.set('jwt', jwt);
                Cookies.expire('jwt');//用完后立即把cookie清空
            }
            if (store.get('jwt')) {
                this.jwt = store.get('jwt');
                try {
                    this.user = jwt_decode(this.jwt);
                } catch (e){
                    store.remove('jwt');
                    this.jwt = null;
                    this.user = null;
                }
            }
        }
        return this.jwt;
    },

    isLogged : function() {
        if(this.getJwt()){
            return true;
        } else {
            return false;
        }
    },

    getUser : function(){
        if(this.isLogged()){
            return this.user;
        } else {
            return null;
        }
    }

});

var onAuthStateChange = function(action) {
    switch(action.actionType) {
        case AuthConstant.AUTH_LOGIN:
            store.set('jwt', action.jwt);
            this.jwt = action.jwt;
            try {
                this.user = jwt_decode(this.jwt);
            } catch (e){
                store.remove('jwt');
                this.jwt = null;
                this.user = null;
            }
            this.emitChange();
            break;
        case AuthConstant.AUTH_LOGOUT:
            store.remove('jwt');
            this.jwt = null;
            this.user = null;
            this.emitChange();
            break;
        default:
            break;
    }
};

AuthStore.subscribe(function(){
    return onAuthStateChange.bind(AuthStore);
});

module.exports = AuthStore;