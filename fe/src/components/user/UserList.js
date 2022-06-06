import React from 'react';

export default class UserList extends React.Component {

    constructor(props) {
        super(props);
        this.state = {};
    }

    render() {
        return (
            <ul>
                {
                    this.props.users
                        .map(user =>
                            <li key={user.id}>{user.username}</li>)
                }
            </ul>
        )
    }
}