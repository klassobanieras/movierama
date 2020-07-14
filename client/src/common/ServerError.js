import React, { Component } from 'react';
import './ServerError.css';
import { Link } from 'react-router-dom';
import { Button } from 'antd';
import { SERVER_ERROR_PAGE_MESSAGE, SERVER_ERROR_PAGE_CODE } from '../constants/Constants';

class ServerError extends Component {
    render() {
        return (
            <div className="server-error-page">
                <h1 className="server-error-title">
                    {SERVER_ERROR_PAGE_CODE} 
                </h1>
                <div className="server-error-desc">
                    {SERVER_ERROR_PAGE_MESSAGE}                
                </div>
                <Link to="/"><Button className="server-error-go-back-btn" type="primary" size="large">Go Back</Button></Link>
            </div>
        );
    }
}

export default ServerError;