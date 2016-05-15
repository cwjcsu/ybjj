/**
 * 通用Logger, 使用如下用法
 * <pre><code>
 *     //import
 *     var Logger = require('./services/Logger.js');
 *
 *     //use
 *     Logger.info('blablabla');
 *     Logger.error('blablabla');
 *
 * </code></pre>
 */
var Logger = {};

var methods = ('assert,clear,count,debug,dir,dirxml,error,exception,group,' +
'groupCollapsed,groupEnd,info,log,markTimeline,profile,profiles,profileEnd,' +
'show,table,time,timeEnd,timeline,timelineEnd,timeStamp,trace,warn').split(',');

var createFunction = function(method){
    return function () {
        if(process.env.NODE_ENV !== 'production') {
            window.console[method].apply(window.console, arguments);
        }
    };
};

var method;
var noop = function() {};

while (typeof(method = methods.pop()) !== 'undefined') {
    if (typeof(window.console[method]) !== 'undefined') {
        Logger[method] = createFunction(method);
    } else {
        Logger[method] = noop;
    }
}

module.exports = Logger;