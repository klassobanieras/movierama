import React from 'react';
import {Spin} from 'antd';

export default function LoadIndicator(props) {
    return (
        <Spin tip="Loading...">
        </Spin>
    );
}