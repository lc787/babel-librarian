import React from 'react';
import axios from 'axios';

export default class UserRegister extends React.Component {
    state = {
        username: '',
        password: ''
    }

    handleChange = event => {
        this.setState({username: event.target.value1, password: event.target.value2});
    }

    handleSubmit = event => {
        event.preventDefault();

        const user = {
            username: this.state.username,
            password: this.state.password
        };

        axios.post('http://localhost:8080/users', { user })
            .then(res => {
                console.log(res);
                console.log(res.data);
            })
    }

    render() {
        return (
            <div>
                <form onSubmit={this.handleSubmit}>
                    <label>
                        Username:
                        <input type="text" name="username" onChange={this.handleChange} />
                        Password:
                        <input type="hidden" name="password" onChange={this.handleChange} />
                        submit:
                        <input type="submit" name="submit"/>
                    </label>
                </form>
            </div>
        )
    }
}