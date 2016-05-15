var React = require('react');
var classNames = require('classnames');

var utils = require('../../../util/utils.js');
var QRCodeAction = require('../../../actions/QRCodeAction.js');
var WeixinConstant = require('../../../constants/WeixinConstant.js');


var QRCode = React.createClass({

    propTypes: {
        beforeQrCode: React.PropTypes.func,//获取二维码之前进行的操作，会把获取QrCode的函数注入，如果成功则继续，否则显示错误信息
        onSuccess: React.PropTypes.func,//业务成功
        onScanned: React.PropTypes.func,//传入this对象，如果设置，则扫描后不会出现coutdown浮窗
        onError: React.PropTypes.func,//业务失败
        userId: React.PropTypes.number,//用户ID，一般不需要设置该参数，例如登录和绑定的userId就是从后台session中取出的
        sceneKey: React.PropTypes.oneOf([WeixinConstant.sceneKey.SCENE_KEY_LOGIN]).isRequired,//场景key，该属性必须设置，而且要与后台定义的一致
        width: React.PropTypes.number,//默认为200（单位为px）
        height: React.PropTypes.number,//默认为200（单位为px）
        onHelp: React.PropTypes.func,//帮助链接点击函数
        regenerate: React.PropTypes.func//重新生成二维码的方法
    },

    getDefaultProps: function () {
        return {
            width: 200,
            height: 200,
            sceneKey: WeixinConstant.sceneKey.SCENE_KEY_LOGIN
        }
    },

    scanQRCodeTimer: null,

    countdownTimer: null,

    getInitialState: function () {
        return {
            //QRCode:'https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=gQFD8DoAAAAAAAAAASxodHRwOi8vd2VpeGluLnFxLmNvbS9xL3ZFeFpRVEhsMDE2cVlnd19RV0tZAAIE%2Fk%2FJVQMECAcAAA%3D%3D',
            QRCode: null,
            loaded: false,
            loadMsg: '',
            showCountdownDialog: false,//倒计时对话框
            showRejectDialog: false,//拒绝提示对话框
            showTimeoutDialog: false,//超时提示对话框
            showServerBusyDialog: false,//微信服务器繁忙提示对话框
            showShieldMessageDialog: false,//屏蔽公众号消息提示对话框
            leftTime: 300//单位为秒
        };
    },

    componentDidMount: function () {
        this.getQRCode();
    },

    componentWillUnmount: function () {
        this.clearAllTimer();
    },

    /**
     * 清除所有定时器
     */
    clearAllTimer: function () {
        if (this.scanQRCodeTimer) {
            clearInterval(this.scanQRCodeTimer);
            this.scanQRCodeTimer = null;
        }
        if (this.countdownTimer) {
            clearInterval(this.countdownTimer);
            this.countdownTimer = null;
        }
    },

    regenerate: function () {
        if (this.props.regenerate) {
            this.props.regenerate();
        } else {
            this.getQRCode();
        }
    },
    /**
     * 获取二维码
     */
    getQRCode: function () {
        var that = this;
        that.setState({
            showCountdownDialog: false,
            showRejectDialog: false,
            showTimeoutDialog: false,
            showServerBusyDialog: false,
            showShieldMessageDialog: false
        });
        if (this.props.beforeQrCode) {
            this.props.beforeQrCode(this._checkAndGetQrCode);
        } else {
            this._checkAndGetQrCode(true, null);
        }
    },

    _checkAndGetQrCode: function (ok, msg) {
        var that = this;
        var failHandler = function (message) {
            that.clearAllTimer();
            that.setState({
                loadMsg: '无法获取微信二维码，请稍后重试',
                loaded: false
            });
            var data = {
                success: false,
                msg: message
            };
            that.onCallback(that.props.onError, data);
        };
        if (ok) {
            //获取二维码
            QRCodeAction.getQRCode(that.props.sceneKey, that.props.userId || null).then(function (data) {
                that.setState({
                    QRCode: data.QRCode,
                    loaded: true,
                    loadMsg: '',
                });
                //获取二维码成功后轮询扫描状态
                that.pollScanQRCodeStatus();
            }).then(undefined, function (e) {
                failHandler(e.message);
            });
        } else {
            failHandler(msg);
        }
    },

    /**
     * 回调
     * @param callback
     * @param message
     */
    onCallback: function (callback, message) {
        if (callback) {
            callback(message);
        }
    },

    /**
     * 轮询二维码扫描状态
     */
    pollScanQRCodeStatus: function () {
        var that = this;
        //开始轮询扫描状态
        that.clearAllTimer();
        that.scanQRCodeTimer = setInterval(function () {
            //获取二维码
            QRCodeAction.pollScanQRCodeResult(that.props.sceneKey).then(function (data) {
                if (data.status == WeixinConstant.qrCodeStatus.SCANNED) { //已经扫描，等待确认
                    if (that.props.onScanned) {
                        that.props.onScanned(that);
                        return;
                    }
                    var leftTime = data.leftTime;//单位为秒
                    if (leftTime <= 0) {
                        that.clearAllTimer();
                        that.setState({
                            showCountdownDialog: false,
                            showRejectDialog: false,
                            showTimeoutDialog: true,
                            showServerBusyDialog: false,
                            showShieldMessageDialog: false
                        });

                    } else if (!that.state.showCountdownDialog) {
                        that.setState({
                            showCountdownDialog: true,
                            showRejectDialog: false,
                            showTimeoutDialog: false,
                            showServerBusyDialog: false,
                            showShieldMessageDialog: false,
                            leftTime: leftTime
                        }, function () {
                            clearInterval(that.countdownTimer);
                            that.countdownTimer = setInterval(function () {
                                leftTime = that.state.leftTime - 1;
                                that.setState({
                                    leftTime: leftTime
                                }, function () {
                                    if (leftTime == 0) {
                                        that.clearAllTimer();
                                        that.setState({
                                            showCountdownDialog: false,
                                            showRejectDialog: false,
                                            showTimeoutDialog: true,
                                            showServerBusyDialog: false,
                                            showShieldMessageDialog: false
                                        });
                                    }
                                });
                            }, 1000);//每一秒倒计时一次
                        });
                    }
                } else if (data.status == WeixinConstant.qrCodeStatus.SUCCESS) {//已同意操作
                    that.clearAllTimer();
                    that.onCallback(that.props.onSuccess, data);
                } else if (data.status == WeixinConstant.qrCodeStatus.REJECTED) {//已拒绝操作
                    that.clearAllTimer();
                    that.setState({
                        showCountdownDialog: false,
                        showRejectDialog: true,
                        showTimeoutDialog: false,
                        showServerBusyDialog: false,
                        showShieldMessageDialog: false
                    });
                } else if (data.status == WeixinConstant.qrCodeStatus.EXPIRED) {//超时
                    that.clearAllTimer();
                    that.setState({
                        showCountdownDialog: false,
                        showRejectDialog: false,
                        showTimeoutDialog: true,
                        showServerBusyDialog: false,
                        showShieldMessageDialog: false
                    });
                } else if (data.status == WeixinConstant.qrCodeStatus.FAIL) {//失败
                    that.clearAllTimer();
                    that.setState({
                        showCountdownDialog: false,
                        showRejectDialog: false,
                        showTimeoutDialog: false,
                        showServerBusyDialog: data.errcode == -1,
                        showShieldMessageDialog: data.errcode == 48002
                    });
                }
            }).then(undefined, function (e) {
                that.clearAllTimer();
                var data = {
                    success: false,
                    msg: e.msg
                };
                that.onCallback(that.props.onError, data);
            });
        }, 2000);
    },

    onHelp: function () {
        if (this.props.onHelp) {
            this.props.onHelp();
        }
    },
    render: function () {
        var countdownDialogClass = classNames('wechat-state-tips countdown-dialog', {show: this.state.showCountdownDialog});
        var rejectDialogClass = classNames('wechat-state-tips reject-dialog', {show: this.state.showRejectDialog});
        var timeoutDialogClass = classNames('wechat-state-tips timeout-dialog', {show: this.state.showTimeoutDialog});
        var serverBusyDialogClass = classNames('wechat-state-tips weixin-error-dialog', {show: this.state.showServerBusyDialog});
        var shieldMessageDialogClass = classNames('wechat-state-tips weixin-error-dialog', {show: this.state.showShieldMessageDialog});
        var tipTime = parseInt(this.state.leftTime / 60) + '分' + this.state.leftTime % 60 + '秒';
        var style = {
            width: this.props.width,
            height: this.props.height,
        };

        var sceneValue = "登录";
        if (this.props.sceneKey === WeixinConstant.sceneKey.SCENE_KEY_BIND) {
            sceneValue = "绑定";
        } else if (this.props.sceneKey === WeixinConstant.sceneKey.SCENE_KEY_LOGIN) {
            sceneValue = "登录";
        }

        var qrcodeImgCls = classNames('qrcode-img', {hide: !this.state.loaded});
        var qrcodeMsgCls = classNames('qrcode-msg', {error: !!this.state.loadMsg}, {hide: this.state.loaded});

        var bottomTitleCls = classNames('bottom-title', {hide: !!this.state.loadMsg});

        return (
            <div>
                <div className="cw-qrcode">
                    <div className='qrcode-img-c'>
                        <img className={qrcodeImgCls} src={this.state.QRCode} style={style} ></img>
                        <div className={qrcodeMsgCls}><span>{this.state.loadMsg}</span></div>
                    </div>
                    <div className={countdownDialogClass}>
                        <div className="wechatTips-icon"></div>
                        <div className="wechatTips-Wrap">
                            <div className="wechat-tips-head">成功扫描</div>
                            <div className="wechat-tips-body">
                                <div className="middle">
                                    <div className="inner">行云服务易代维公众号会给您的微信发送一条消息，请在<span
                                        className="tips-time">{tipTime}</span>内点击链接获取桌面。
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div className={rejectDialogClass}>
                        <div className="wechatTips-icon"></div>
                        <div className="wechatTips-Wrap">
                            <div className="wechat-tips-head">拒绝{sceneValue}</div>
                            <div className="wechat-tips-body">
                                <div className="middle">
                                    <div className="inner">
                                        您已拒绝使用微信身份{sceneValue}行云服务易代维，您可选择其它{sceneValue}方式，或<br/><span
                                        className="click-me" onClick={this.getQRCode}>点此</span>重新进行微信{sceneValue}。
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div className={timeoutDialogClass}>
                        <div className="wechatTips-icon"></div>
                        <div className="wechatTips-Wrap">
                            <div className="wechat-tips-head">二维码已过期</div>
                            <div className="wechat-tips-body">
                                <div className="middle">
                                    <div className="inner">二维码已过期，请<span className="click-me"
                                                                         onClick={this.regenerate}>点击此处</span>生成一个新的二维码。
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div className={serverBusyDialogClass}>
                        <div className="wechatTips-icon"></div>
                        <div className="wechatTips-Wrap">
                            <div className="wechat-tips-head">消息推送失败</div>
                            <div className="wechat-tips-body">
                                <div className="middle">
                                    <div className="inner">微信服务繁忙，请稍候再试</div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div className={shieldMessageDialogClass}>
                        <div className="wechatTips-icon"></div>
                        <div className="wechatTips-Wrap">
                            <div className="wechat-tips-head">消息推送失败</div>
                            <div className="wechat-tips-body">
                                <div className="middle">
                                    <div className="inner">您屏蔽了公众号的消息推送功能，请开启后重新扫描，<a
                                        href="javascript:void(0)" onClick={this.onHelp}
                                        target="_blank">查看如何开启</a>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div className={bottomTitleCls}>微信扫一扫登录</div>
            </div>
        );
    }
});

module.exports = QRCode;
