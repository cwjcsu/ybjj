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

    render: function () {
        return (
            <div className="login-panel-wrapper">
                <div className="login-panel">
                    <QRCode
                        sceneKey={WeixinConstant.sceneKey.SCENE_KEY_LOGIN} width={225}
                        height={225}/>
                </div>
            </div>
        );
    }
});

ReactDOM.render(<Login/>, document.body);