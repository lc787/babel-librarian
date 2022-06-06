import './App.css';
import UserList from './components/user/UserList';
import UserRegister from './components/user/UserRegister';
import React from 'react';
import axios from 'axios';

export default class App extends React.Component {

    constructor(props) {
        super(props);
        this.state = {users: []};
    }

    componentDidMount() {
        this.populateUsers();
    }

    populateUsers = () => {
        axios.get('http://localhost:8080/users')
            .then(res => {
                const users = res.data;
                this.setState({users: users});
            })
    }

    render() {
        return (
            <div className="App">
                <UserRegister populateUsers={this.populateUsers}/>
                <UserList users={this.state.users}/>
            </div>

        )
    }
}