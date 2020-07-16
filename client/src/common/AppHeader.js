import React, {Component} from 'react';
import {
    Link,
    withRouter
} from 'react-router-dom';
import './AppHeader.css';
import {Layout, Menu, Dropdown, Button, Tooltip} from 'antd';
import {PlusCircleFilled, HomeFilled, UserOutlined, DownOutlined, AlertOutlined} from '@ant-design/icons';
import {AuthService} from "../utils/AuthService";

const Header = Layout.Header;

class AppHeader extends Component {
    constructor(props) {
        super(props);
        this.handleMenuClick = this.handleMenuClick.bind(this);
    }

    handleMenuClick({key}) {
        if (key === "logout") {
            AuthService.signOut();
        }
    }

    render() {
        let menuItems;
        if (this.props.currentUser) {
            menuItems = [

                <Menu.Item key="/movies/new">
                    <Link to="/movies/new">
                        <PlusCircleFilled className="nav-icon"/>
                    </Link>
                </Menu.Item>,

                <Menu.Item key="/">
                    <Link to="/">
                        <HomeFilled className="nav-icon"/>
                    </Link>
                </Menu.Item>,
                <Menu.Item key="profile">
                    <Link to={`/users/${this.props.currentUser}`}>Profile</Link>
                </Menu.Item>,
                <Menu.Item key="logout">
                    Logout
                </Menu.Item>
            ];
        } else {
            menuItems = [
                <Menu.Item key="/login">
                    <Link to="/login">Login</Link>
                </Menu.Item>,
                <Menu.Item key="/signup">
                    <Link to="/signup">Signup</Link>
                </Menu.Item>
            ];
        }

        return (
            <Header className="header">
                <Button
                        icon={<AlertOutlined style={{color: 'hotpink'}} spin/>} size={"large"} href={"/"} style={{float: 'left', marginTop: '10px'}}/>
                <Menu theme="dark" mode="horizontal" defaultSelectedKeys={[this.props.location.pathname]} onClick={this.handleMenuClick}>
                    {menuItems}
                </Menu>
            </Header>

        );
    }
}


export default withRouter(AppHeader);