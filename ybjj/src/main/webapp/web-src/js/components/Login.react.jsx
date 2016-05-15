require("../../less/Login.less");
var React = require('react');
var ReactDOM = require('react-dom');
var classNames = require('classnames');

var React = require('react');
var {Input,Button} = require('react-bootstrap');
var AuthAction = require('../actions/AuthAction.js');
var AuthStore = require('../stores/AuthStore.js');
var Logger = require('../services/Logger.js');
var QRCode = require('./common/widgets/QRCode.react.jsx');
var WeixinConstant = require('../constants/WeixinConstant.js');


var Login = React.createClass({
    checkStatusJob: null,
    getInitialState: function () {
        return {}
    },
    componentDidMount: function () {
        var that = this;
    },
    componentWillUnmount: function () {
    },

    onScanQRCodeSuccess: function (data) {
        AuthAction.dispatcherJwt(data.jwt);
        Logger.log("scanSuccess  ", data);
        window.location.href = "/index.html";
    },
    onScanQRCodeError(data){
        Logger.log(data.msg)
    },
    render: function () {
        return (
            <div className="login-panel-wrapper">
                <h2 className="login-title">夜半歌声</h2>
                <div className="login-panel">
                    <QRCode
                        sceneKey={WeixinConstant.sceneKey.SCENE_KEY_LOGIN} width={225}
                        height={225} onSuccess={this.onScanQRCodeSuccess} onError={this.onScanQRCodeError}/>
                </div>
            </div>
        );
    }
});

ReactDOM.render(<Login/>, document.getElementById('domBody'));