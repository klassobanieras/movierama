import React, { Component } from 'react';
import { login } from '../../utils/APIClient';
import './login.css';
import { Link } from 'react-router-dom';
import { ACCESS_TOKEN, LOGIN_AUTHORIZATION_ERROR_MESSAGE, LOGIN_GENERAL_ERROR, APPLICATION_NAME } from '../../constants/Constants';

import { Form, Input, Button, notification } from 'antd';
import {LockOutlined, UserOutlined} from '@ant-design/icons';
const FormItem = Form.Item;

class Login extends Component {
    render() {
        return (
            <div className="login-container">
                <h1 className="page-title">Login</h1>
                <div className="login-content">
                    <Form onLogin={this.props.onLogin} />
                </div>
            </div>
        );
    }
}

class LoginForm extends Component {
    constructor(props) {
        super(props);
        this.handleSubmit = this.handleSubmit.bind(this);
    }

    handleSubmit(event) {
        event.preventDefault();   
        this.props.form.validateFields((err, values) => {
            if (!err) {
                const loginRequest = Object.assign({}, values);
                login(loginRequest)
                .then(response => {
                    localStorage.setItem(ACCESS_TOKEN, response.accessToken);
                    this.props.onLogin();
                }).catch(error => {
                    if(error.status === 401) {
                        notification.error({
                            message: APPLICATION_NAME,
                            description: LOGIN_AUTHORIZATION_ERROR_MESSAGE
                        });                    
                    } else {
                        notification.error({
                            message: APPLICATION_NAME,
                            description: error.message || LOGIN_GENERAL_ERROR
                        });                                            
                    }
                });
            }
        });
    }

    render() {
        const { getFieldDecorator } = this.props.form;
        return (
            <Form onSubmit={this.handleSubmit} className="login-form">
                <FormItem>
                    {getFieldDecorator('usernameOrEmail', {
                        rules: [{ required: true, message: 'Please input your email or username!' }],
                    })(
                    <Input 
                        prefix={<UserOutlined/>}
                        size="large"
                        name="usernameOrEmail" 
                        placeholder="Email or username" />    
                    )}
                </FormItem>
                <FormItem>
                {getFieldDecorator('password', {
                    rules: [{ required: true, message: 'Please input your Password!' }],
                })(
                    <Input 
                        prefix={<LockOutlined/>}
                        size="large"
                        name="password" 
                        type="password" 
                        placeholder="Password"  />                        
                )}
                </FormItem>
                <FormItem>
                    <Button type="primary" htmlType="submit" size="large" className="login-form-button">Login</Button>
                    Or <Link to="/signup">Register now!</Link>
                </FormItem>
            </Form>
        );
    }
}

export default Login;