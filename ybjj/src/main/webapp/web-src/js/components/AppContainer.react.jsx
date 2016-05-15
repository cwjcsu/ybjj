var React = require('react');
var utils = require('../util/utils.js');
var classNames = require('classnames');
var RouterContainer = require('../services/RouterContainer.js');
var Logger = require('../services/Logger.js');
var AppContainer = React.createClass({
    getInitialState: function () {
        return {}
    },
    componentDidMount: function () {
        var that = this;
    },
    render: function () {
        return (
            <div>
                <h2>主页TODO</h2>
            </div>
        );
    }
});

module.exports = AppContainer;