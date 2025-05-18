import React from 'react';
import { Navbar, Nav } from 'react-bootstrap'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import {faBars, faHome, faUser} from '@fortawesome/free-solid-svg-icons'
import {useNavigate, Link} from "react-router-dom";
import Utils from "../utils/Utils";
import BackendService from "../services/BackendService";
import {connect} from "react-redux";
import {userActions} from "../utils/Rdx";

class NavigationBarClass extends React.Component {

    constructor(props) {
        super(props);
        this.goHome = this.goHome.bind(this);
        this.logout = this.logout.bind(this);
    }

    goHome() {
        this.props.navigate('Home');
    }

    render() {
        let username = Utils.getUserName();
        return (
            <Navbar bg="light" expand="lg">
                <button type="button"
                        className="btn btn-outline-secondary mr-2"
                        onClick={this.props.toggleSideBar}>
                    <FontAwesomeIcon icon={ faBars} />
                </button>
                <Nav.Link onClick={this.goHome}>Home</Nav.Link>
                <Navbar.Brand>SD</Navbar.Brand>

                { this.props.user &&
                    <Nav.Link onClick={this.logout}><FontAwesomeIcon icon={faUser} fixedWidth />{' '}Logout</Nav.Link>
                }
                { !this.props.user &&
                    <Nav.Link as={Link} to="/login"><FontAwesomeIcon icon={faUser} fixedWidth />{' '}Login</Nav.Link>
                }
            </Navbar>
        );
    }
    logout() {
        BackendService.logout()
            .then(() => {
                Utils.removeUser();
                this.props.dispatch(userActions.logout())
                this.props.navigate('Login');
            })
    }
}

const NavigationBar = props => {
    const navigate = useNavigate()

    return <NavigationBarClass navigate={navigate} {...props} />
}

const mapStateToProps = state => {
    const { user } = state.authentication;
    return { user };
}

export default  connect(mapStateToProps)(NavigationBar);