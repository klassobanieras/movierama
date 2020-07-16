import React, {Component} from 'react';
import {
    Link,
    withRouter
} from 'react-router-dom';
import './AppHeader.css';
import {Layout, Menu, Dropdown, Button, Tooltip} from 'antd';
import {PlusCircleFilled, HomeFilled, UserOutlined, DownOutlined, AlertOutlined} from '@ant-design/icons';

const Header = Layout.Header;

class AppHeader extends Component {
    constructor(props) {
        super(props);
        this.handleMenuClick = this.handleMenuClick.bind(this);
    }

    handleMenuClick({key}) {
        if (key === "logout") {
            this.props.onLogout();
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

                <Menu.Item key="/profile" className="profile-menu">
                    <ProfileDropdownMenu
                        currentUser={this.props.currentUser}
                        handleMenuClick={this.handleMenuClick}/>
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
                <Menu theme="dark" mode="horizontal" defaultSelectedKeys={[this.props.location.pathname]}>
                    {menuItems}
                </Menu>
            </Header>

        );
    }
}

function ProfileDropdownMenu(props) {
    const dropdownMenu = (
        <Menu onClick={props.handleMenuClick} className="profile-dropdown-menu">
            <Menu.Item key="user-info" className="dropdown-item" disabled>
                <div className="username-info">
                    @{props.currentUser}
                </div>
            </Menu.Item>
            <Menu.Divider/>
            <Menu.Item key="profile" className="dropdown-item">
                <Link to={`/users/${props.currentUser}`}>Profile</Link>
            </Menu.Item>
            <Menu.Item key="logout" className="dropdown-item">
                Logout
            </Menu.Item>
        </Menu>
    );

    return (
        <Dropdown
            overlay={dropdownMenu}
            trigger={['click']}
            getPopupContainer={() => document.getElementsByClassName('profile-menu')[0]}>
            <a className="ant-dropdown-link">
                <UserOutlined className="nav-icon" style={{marginRight: 0}}/> <DownOutlined/>
            </a>
        </Dropdown>
    );
}


export default withRouter(AppHeader);