var EventEmitter = require('events').EventEmitter;
var AppDispatcher = require('../dispatchers/AppDispatcher');

var extend = require('extend');

var BaseStore = extend({}, EventEmitter.prototype, {
    _dispatchToken: null,
    subscribe: function (actionSubscribe) {
        this._dispatchToken = AppDispatcher.register(actionSubscribe());
    },
    getDispatchToken: function () {
        return this._dispatchToken;
    },
    emitChange: function () {
        this.emit('CHANGE');
    },
    addChangeListener: function (cb) {
        this.on('CHANGE', cb);
    },
    removeChangeListener: function (cb) {
        this.removeListener('CHANGE', cb);
    }
});

module.exports = BaseStore;