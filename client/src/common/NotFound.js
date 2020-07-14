import React, { Component } from 'react';
import './NotFound.css';
import { Link } from 'react-router-dom';
import { Button } from 'antd';
import { NOT_FOUND_PAGE_MESSAGE, NOT_FOUND_PAGE_CODE } from '../constants/Constants';

class NotFound extends Component {
    render() {
        return (
            <div className="page-not-found">
                <h1 className="title">
                    {NOT_FOUND_PAGE_CODE}
                </h1>
                <div className="desc">
                    {NOT_FOUND_PAGE_MESSAGE}
                </div>
                <Link to="/"><Button className="go-back-btn" type="primary" size="large">Go Back</Button></Link>
            </div>
        );
    }
}

export default NotFound;