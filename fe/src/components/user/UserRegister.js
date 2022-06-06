import React from 'react';
import axios from 'axios';

export default class UserRegister extends React.Component {

    constructor(props) {
        super(props);
        this.state = {username: '', password: ''};
    }


    handleUsernameChange = event => {
        this.setState({username: event.target.value});
    }

    handlePasswordChange = event => {
        this.setState({password: event.target.value});
    }

    handleSubmit = event => {
        event.preventDefault();
        const data = JSON.stringify(this.state);
        console.log(data);
        axios({
            url: "http://localhost:8080/users",
            method: "POST",
            headers: {
                'Content-Type': 'application/json'
            },
            data: data
        })
            .then(res => {
                debugger
                console.log(res);
                this.setState({username: '', password: ''});
                this.props.populateUsers();
            })
            .catch(error => {
                console.log(error);
            })
    }

    render() {
        return (
            <div>
                <label>
                    Username:<br/>
                    <input type="text" name="username" value={this.state.username}
                           onChange={this.handleUsernameChange}/> <br/>
                    Password: <br/>
                    <input type="password" name="password" value={this.state.password}
                           onChange={this.handlePasswordChange}/> <br/>
                    <input type="submit" name="submit" onClick={this.handleSubmit}/> <br/>
                </label>
            </div>
        )
    }
}
