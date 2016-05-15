import React from 'react'
import { render } from 'react-dom'
import { Router, Route, Link, browserHistory } from 'react-router'

var utils = require('../util/utils.js');
var classNames = require('classnames');

var AppContainer = require('./AppContainer.react.jsx');
var Cookies = require('../../js/util/Cookies.js');
var Main = require('./main/Main.react.jsx');


var App = React.createClass({
    componentDidMount: function () {

    },
    componentWillUnmount: function () {

    },
    render: function () {
        return (
            <AppContainer>
                {this.props.children}
            </AppContainer>
        );
    }
});

var routes = (
    <Router history={browserHistory}>
        <Route path="/" component={Main}/>
    </Router>
);

render(routes, document.body);
