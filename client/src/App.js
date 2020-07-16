import React, {Component} from 'react';
import './App.css';
import {
    Route,
    withRouter,
    Switch
} from 'react-router-dom';

import {AuthService, getCurrentUser} from './utils/AuthService';
import {ACCESS_TOKEN, APPLICATION_NAME, SUCCESSFULL_LOGIN, SUCCESSFULL_LOGOUT} from './constants/Constants';

import MovieList from './movie/MovieList'
import NewMovie from './movie/NewMovie'
import {LoginForm} from './user/login/Login';
import {SignUp} from './user/signup/SignUp';
import Profile from './user/profile/Profile';
import AppHeader from './common/AppHeader';
import NotFound from './common/NotFound';
import LoadIndicator from './common/LoadIndicator';
import PrivateRoute from './common/PrivateRoute';

import {Layout, notification} from 'antd';
import {SignUpConfirm} from "./user/signup/SignUpConfirm";
import Amplify from '@aws-amplify/core'
import awsConfig from "./config/aws-config";

const {Content} = Layout;

Amplify.configure(awsConfig);

Amplify.Logger.LOG_LEVEL = 'INFO';

class App extends Component {
    constructor(props) {
        super(props);
        this.state = {
            currentUser: null,
            isAuthenticated: false,
            isLoading: false
        }
        this.handleLogout = this.handleLogout.bind(this);
        this.loadCurrentUser = this.loadCurrentUser.bind(this);
        this.handleLogin = this.handleLogin.bind(this);

        notification.config({
            placement: 'topRight',
            top: 70,
            duration: 3,
        });
    }

    loadCurrentUser() {
        this.setState({
            isLoading: true
        });
        AuthService.getCurrentUser()
            .then(response => {
                this.setState({
                    currentUser: response.attributes.email,
                    isAuthenticated: true,
                    isLoading: false
                });
            }).catch(error => {
            this.setState({
                isLoading: false
            });
        });
    }

    componentDidMount() {
        this.loadCurrentUser();
    }

    handleLogout(redirectTo = "/", notificationType = "success", description = SUCCESSFULL_LOGOUT) {
        localStorage.removeItem(ACCESS_TOKEN);

        this.setState({
            currentUser: null,
            isAuthenticated: false
        });

        AuthService.signOut();
        this.props.history.push(redirectTo);

        notification[notificationType]({
            message: APPLICATION_NAME,
            description: description,
        });
    }

    handleLogin() {
        notification.success({
            message: APPLICATION_NAME,
            description: SUCCESSFULL_LOGIN,
        });
        this.loadCurrentUser();
        this.props.history.push("/");
    }

    render() {
        if (this.state.isLoading) {
            return <LoadIndicator/>
        }

        return (
            <Layout className="app-container">
                <AppHeader isAuthenticated={this.state.isAuthenticated}
                           currentUser={this.state.currentUser}
                           onLogout={this.handleLogout}/>

                <Content className="app-content">
                    <div className="container">
                        <Switch>
                            <Route exact path="/"
                                   render={(props) => <MovieList isAuthenticated={this.state.isAuthenticated}
                                                                 currentUser={this.state.currentUser} handleLogout={this.handleLogout}
                                                                 isProfileMovieList={false} {...props}/>}>
                            </Route>
                            <Route path="/login" component={LoginForm}></Route>
                            <Route path="/signup" component={SignUp}></Route>
                            <Route path="/signup-confirm" component={SignUpConfirm}></Route>
                            <Route path="/users/:username"
                                   render={(props) => <Profile isAuthenticated={this.state.isAuthenticated}
                                                               currentUser={this.state.currentUser} {...props}  />}>
                            </Route>
                            <PrivateRoute authenticated={this.state.isAuthenticated} path="/movies/new" component={NewMovie}
                                          handleLogout={this.handleLogout} currentUser={this.state.currentUser}></PrivateRoute>
                            <Route component={NotFound}></Route>
                        </Switch>
                    </div>
                </Content>
            </Layout>
        );
    }
}

export default withRouter(App);