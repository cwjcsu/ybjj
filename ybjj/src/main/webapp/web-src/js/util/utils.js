var AppConstant = require('../constants/AppConstant.js');
var utils = {
        noop: function () {

        },

        /**
         * 用浏览器打开url
         * @param url
         */
        browseUrl: function (url) {
            if (window.javaApp) {
                window.javaApp.browseUrl(url);
            } else {
                var win = window.open(url, '_blank');
                win.focus();
            }
        },
        /**
         * 从href中解析参数
         * @param href
         * @returns {{}}
         */
        getParametersFromHref: function (href) {
            var args = href.split("?");
            var ret = {};
            if (args[0] == href) { /*参数为空*/
                return ret;
                /*无需做任何处理*/
            }
            var str = args[1];
            var strs = str.split("#");
            str = strs[0];
            args = str.split("&");

            for (var i = 0; i < args.length; i++) {
                str = args[i];
                var arg = str.split("=");
                if (arg.length <= 1) {
                    continue;
                }
                ret[arg[0]] = arg[1];
            }
            return ret;
        },
        trim: function (str) {
            if (!str || typeof str != 'string') {
                return str;
            }
            return str.replace(/^\s*|\s*$/g, '');
        },

        windowSize: function () {
            return {
                width: window.innerWidth || document.documentElement.clientWidth || document.body.clientWidth,
                height: window.innerHeight || document.documentElement.clientHeight || document.body.clientHeight
            };
        },

        regs: {
            phone: /^(\(?([0\+]\d{2,5}-)?(0\d{2,3})\)?-?)?(\d{7,8}|\d{11})(-(\d{3,}))?$/,
            email: /^\w+((-\w+)|(\.\w+))*\@[A-Za-z0-9]+((\.|-)[A-Za-z0-9]+)*\.[A-Za-z0-9]+$/,
            ip: /^(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])$/,
            weixin: /^[a-zA-Z][a-zA-Z0-9-_]{5,19}/
        },

        getTextSize: function (fontName, fontSize) {
            var body = document.getElementsByTagName("body")[0];
            var dummy = document.createElement("span");
            var dummyText = document.createTextNode("EgM");
            dummy.appendChild(dummyText);
            dummy.setAttribute("style", 'font-family: ' + fontName + ';font-size:' + fontSize + 'px');
            body.appendChild(dummy);
            var result = {
                width: Math.ceil(dummy.offsetWidth / 3),
                height: dummy.offsetHeight
            };
            body.removeChild(dummy);

            dummy = document.createElement("canvas");
            if (dummy) {
                var ctx = dummy.getContext("2d");
                ctx.font = fontSize + 'px ' + fontName;
                result.width = Math.ceil(ctx.measureText('1234567890').width / 10);
            }

            //result = {
            //	width : 7,
            //	height : 16
            //};
            return result;
        },

        requestAnimFrame: (function () {
            return window.requestAnimationFrame ||
                window.webkitRequestAnimationFrame ||
                window.mozRequestAnimationFrame ||
                window.oRequestAnimationFrame ||
                window.msRequestAnimationFrame ||
                function (callback) {
                    window.setTimeout(callback, 1000 / 60);
                };
        })(),

        /**
         * 在num=1,len=4的情况下输出 0004, 在num=10000,len=4的情况下输出 9999
         * @param num 目标数字
         * @param len 输出长度
         */
        fixNumber: function (num, len) {
            num = Math.ceil(num);
            if (num < 0) num = 0;
            var numStr = num.toString();
            var n0 = len - numStr.length;
            if (n0 < 0) {
                return (Math.pow(10, len) - 1).toString();
            }
            while (n0-- > 0) {
                numStr = '0' + numStr;
            }
            return numStr;
        },

        /**
         * 简单判断触摸设备
         * @returns {boolean}
         */
        isTouchDevice: function () {
            if ('ontouchstart' in window) {
                return true;
            } else {
                return false;
            }
        },

        isInputFiled: function (dom) {
            var tagName = dom.tagName;
            return (tagName == 'INPUT' || tagName == 'TEXTAREA');
        },

        // 判断是否图片文件
        isImgFile: function (fileName) {
            var ext;
            ext = ['.gif', '.jpg', '.jpeg', '.png', '.bmp', '.bmml'];
            var s = fileName.toLowerCase();
            var r = false;
            for (var i = 0; i < ext.length; i++) {
                if (s.indexOf(ext[i]) > 0) {
                    r = true;
                    break;
                }
            }
            return r;
        },
        /**
         * 获取http路径，
         * @param prefix
         * @param path
         */
        getAvatarURL: function (prefix, path) {
            if (typeof(path) != 'string' || !path.trim()) {
                return '';
            }
            if (path.startWith('http://') || path.startWith('https://')) {
                return path;
            }
            if (prefix && prefix.endWith('/')) {
                prefix = prefix.substring(0, prefix.length - 1);
            }
            if (path.startWith('/')) {
                path = path.substring(1);
            }
            return prefix + '/' + path;
        },

        // 单位转换
        storageChange: function (size) {
            if (size <= '0') {
                return ' 0 KB';
            }
            var s;
            s = ['B', 'KB', 'MB', 'GB', 'TB', 'PB'];
            var e = Math.floor(Math.log(size) / Math.log(1024));
            return (size / Math.pow(1024, Math.floor(e))).toFixed(2) + " " + s[e];
        },

        //获取文件后缀
        getFileExt: function (fileName) {
            var file = fileName.replace(/.*(\/|\\)/, "");
            var fileArray = (/[.]/.exec(file)) ? /[^.]+$/.exec(file.toLowerCase()) : '';
            var fileExt = fileArray[0];
            var fileMap = {
                'bat': '批处理文件', 'bmp': '图片文件', 'doc': 'word文档', 'docx': 'word文档', 'exe': '可执行文件',
                'gif': '图片文件', 'img': '通用图片文件', 'jpg': '图片文件', 'pdf': 'PDF文档', 'png': '图片文件',
                'ppt': '幻灯片文档', 'pptx': '幻灯片文档',
                'txt': '文本文档', 'xls': 'EXCEL文档', 'xlsx': 'EXCEL文档', 'rar': '压缩文件', 'zip': '压缩文件',
                'avi': 'AVI视频文件',
                'css': 'CSS文件',
                'htm': 'HTM文件',
                'html': 'HTML文件',
                'js': 'JS文件',
                'mov': 'MOV视频文件',
                'mp4': 'MP4视频文件',
                'mpg': 'MPG视频文件'
            };
            // 其他未定义图片格式
            if (fileExt == 'bmml' || fileExt == 'jpeg') {
                fileExt = 'img';
            } else {
                if (fileMap[fileExt] !== undefined) {
                    return fileExt;
                } else {
                    fileExt = 'file';
                }
            }
            return fileExt;
        },
        setSelectionRange: function (input, selectionStart, selectionEnd) {
            if (input.setSelectionRange) {
                input.focus();
                input.setSelectionRange(selectionStart, selectionEnd);
            }
            else if (input.createTextRange) {
                var range = input.createTextRange();
                range.collapse(true);
                range.moveEnd('character', selectionEnd);
                range.moveStart('character', selectionStart);
                range.select();
            }
        },
        setCursorPosition: function (input, pos) {
            this.setSelectionRange(input, pos, pos);
        },
        redirectToLogin: function (params) {
            params = params || '';
            window.location.href = AppConstant.PORTAL_CONTEXT_PATH + AppConstant.LOGIN_PAGE + params;
        }
    }
    ;

/**
 * 一些对象的扩展
 */
// _indexOf忽略大小写
String.prototype._indexOf = function () {
    if (typeof(arguments[arguments.length - 1]) != 'boolean')
        return this.indexOf.apply(this, arguments);
    else {
        var bi = arguments[arguments.length - 1];
        var thisObj = this;
        var idx = 0;
        if (typeof(arguments[arguments.length - 2]) == 'number') {
            idx = arguments[arguments.length - 2];

            thisObj = this.substr(idx);
        }

        var re = new RegExp(arguments[0], bi ? 'i' : '');
        var r = thisObj.match(re);
        return r === null ? -1 : r.index + idx;
    }
};

//计算字符串长度
String.prototype.strLen = function (chs) {
    if (chs) {
        var len = 0;
        for (var i = 0; i < this.length; i++) {
            if (this.charCodeAt(i) > 255 || this.charCodeAt(i) < 0) len += 2; else len++;
        }
        return len;
    } else {
        // 中文也只算一个字符
        return this.length;
    }
};
//将字符串拆成字符，并存到数组中
String.prototype.strToChars = function () {
    var chars = [];
    for (var i = 0; i < this.length; i++) {
        chars[i] = [this.substr(i, 1), this.isCHS(i)];
    }
    String.prototype.charsArray = chars;
    return chars;
};
//判断某个字符是否是汉字
String.prototype.isCHS = function (i) {
    if (this.charCodeAt(i) > 255 || this.charCodeAt(i) < 0)
        return true;
    else
        return false;
};
//截取字符串（从start字节到end字节）
String.prototype.subCHString = function (start, end) {
    var len = 0;
    var str = "";
    this.strToChars();
    for (var i = 0; i < this.length; i++) {
        if (this.charsArray[i][1])
            len += 2;
        else
            len++;
        if (end < len)
            return str;
        else if (start < len)
            str += this.charsArray[i][0];
    }
    return str;
};
//截取字符串（从start字节截取length个字节）
String.prototype.subCHStr = function (start, length) {
    return this.subCHString(start, start + length);
};
// 判断字符是否为一个字母(a-z|A-Z)
String.prototype.isLetter = function () {
    return this.length === 1 && this.match(/[a-z]/i);
};

String.prototype.startWith = function (str) {
    return this.indexOf(str) === 0;
};
String.prototype.endWith = function (str) {
    var t = this;
    return t.substring(t.length - str.length, t.length) == str;
};

/*
 * 时间格式化
 */
Date.prototype.format = function (format) {
    var o = {
        "M+": this.getMonth() + 1, //month
        "d+": this.getDate(),    //day
        "h+": this.getHours(),   //hour
        "m+": this.getMinutes(), //minute
        "s+": this.getSeconds(), //second
        "q+": Math.floor((this.getMonth() + 3) / 3),  //quarter
        "S": this.getMilliseconds() //millisecond
    };
    if (/(y+)/.test(format)) {
        format = format.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    }
    for (var k in o) {
        if (new RegExp("(" + k + ")").test(format)) {
            format = format.replace(RegExp.$1, RegExp.$1.length == 1 ? o[k] : ("00" + o[k]).substr(("" + o[k]).length));
        }
    }
    return format;
};

// 是否为同一天
Date.prototype.isSameDateAs = function (pDate) {
    return (
        this.getFullYear() === pDate.getFullYear() &&
        this.getMonth() === pDate.getMonth() &&
        this.getDate() === pDate.getDate()
    );
};

/**
 * 是否为相邻的时间
 * @param targetTime Date对象
 * @param step 秒
 */
Date.prototype.isCloselyTime = function (targetTime, step) {
    return Math.abs(targetTime.getTime() - this.getTime()) <= step * 1000;
};


module.exports = utils;