import React from 'react';
import { Link } from 'react-router-dom'
import { Nav } from 'react-bootstrap'
import {faGlobe, faImage, faPallet, faUniversity, faUser} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";


const SideBar = props => {
    return (
        <Nav className={"flex-column my-sidebar my-sidebar-expanded"}>
            {props.expanded &&
                <Nav.Item>
                    <Nav.Link as={Link} to="/countries"><FontAwesomeIcon icon={faGlobe}/>{' '}Countries</Nav.Link>
                </Nav.Item>
            }
            {!props.expanded &&
                <Nav.Item>
                    <Nav.Link as={Link} to="/countries"><FontAwesomeIcon icon={faGlobe} size="2x"/></Nav.Link>
                </Nav.Item>
            }
            {props.expanded &&
                <Nav.Item>
                    <Nav.Link as={Link} to="/artists"><FontAwesomeIcon icon={faPallet}/>{' '}Artists</Nav.Link>
                </Nav.Item>
            }

            {!props.expanded &&
                <Nav.Item>
                    <Nav.Link as={Link} to="/artists"><FontAwesomeIcon icon={faPallet} size="2x"/></Nav.Link>
                </Nav.Item>
            }

            {props.expanded &&
                <Nav.Item>
                    <Nav.Link as={Link} to="/museums"><FontAwesomeIcon icon={faUniversity}/>{' '}Museums</Nav.Link>
                </Nav.Item>
            }

            {!props.expanded &&
                <Nav.Item>
                    <Nav.Link as={Link} to="/museums"><FontAwesomeIcon icon={faUniversity} size="2x"/></Nav.Link>
                </Nav.Item>
            }

            {props.expanded &&
                <Nav.Item>
                    <Nav.Link as={Link} to="/paintings"><FontAwesomeIcon icon={faImage}/>{' '}Paintings</Nav.Link>
                </Nav.Item>
            }

            {!props.expanded &&
                <Nav.Item>
                    <Nav.Link as={Link} to="/paintings"><FontAwesomeIcon icon={faImage} size="2x"/></Nav.Link>
                </Nav.Item>
            }

            {props.expanded &&
                <Nav.Item>
                    <Nav.Link as={Link} to="/users"><FontAwesomeIcon icon={faUser}/>{' '}Users</Nav.Link>
                </Nav.Item>
            }
            {props.expanded &&
                <Nav.Item>
                    <Nav.Link as={Link} to="/my_account"><FontAwesomeIcon icon={faUser}/>{' '}Account</Nav.Link>
                </Nav.Item>
            }

            {!props.expanded &&
                <Nav.Item>
                    <Nav.Link as={Link} to="/my_account"><FontAwesomeIcon icon={faUser} size="2x"/></Nav.Link>
                </Nav.Item>
            }
        </Nav>
    )
}

export default SideBar;