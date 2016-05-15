import React from 'react'
import { render } from 'react-dom'
import { Router, Route, Link, browserHistory } from 'react-router'

var utils = require('../util/utils.js');
var classNames = require('classnames');

var AppContainer = require('./AppContainer.react.jsx');
var Cookies = require('../../js/util/Cookies.js');
var Main = require('./main/Main.react.jsx');
var AuthStore = require('../stores/AuthStore.js');
var AuthAction = require('../actions/AuthAction.js');

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

if (AuthStore.isLogged()) {
    AuthAction.checkJwt(AuthStore.getJwt()).then(function () {
        render(routes, document.getElementById('domBody'));
    }, function (e) {
        console.log(e);
    }).then(undefined, function (e) {
        console.log(e);
    });
} else {
    utils.redirectToLogin();
}
