
import React from 'react';
import BackendService from "../services/BackendService";
import Utils from "../utils/Utils";


class LoginComponentClass extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            username: '',
            password: '',
            loggingIn: false,
            submitted: false,
            errorMessage: null,
        };

        this.handleChange = this.handleChange.bind(this)
        this.handleSubmit = this.handleSubmit.bind(this)
    }

    handleChange(e) {
        const { name, value } = e.target;
        this.setState({ [name]: value });
    }
    handleSubmit(e){
        e.preventDefault();
        this.setState({ submitted: true, loggingIn: true, errorMessage: null });
        const { username, password } = this.state;
        BackendService.login(username, password)
            .then(
                resp => {
                    console.log(resp.data);
                    Utils.saveUser(resp.data)
                }
            )
            .catch(
                err => {
                    if (err.response && err.response.status === 401)
                        this.setState( { errorMessage : "Authorization error", loggingIn: false } );
                    else
                        this.setState( { errorMessage : err.message, loggingIn: false  } );
                }
            )
            .finally(()=> this.setState({ loggingIn : false }));
    }

    render() {
        console.log("render");
        let { submitted, username, password, loggingIn } = this.state;
        return (
            <div className="col-md-6 me-0">
                <h2>Вход</h2>
                <form name="form" onSubmit={this.handleSubmit}>
                    <div className="form-group">
                        <label htmlFor="username">Логин</label>
                        <input type="text" className={'form-control' + (submitted && !username ? ' is-invalid' : '' )}
                               name="username" value={username}
                               onChange={this.handleChange} />
                        {submitted && !username && <div className="help-block text-danger">Введите имя пользователя</div>}
                    </div>
                    <div className="form-group">
                        <label htmlFor="password">Пароль</label>
                        <input type="password" className={'form-control' + (submitted && !password ? ' is-invalid' : '' )}
                               name="password" value={password}
                               onChange={this.handleChange} />
                        {submitted && !password &&
                            <div className="help-block text-danger">Введите пароль</div>
                        }
                    </div>
                    <div className="form-group mt-2">
                        <button className="btn btn-primary">
                            {loggingIn && <span className="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>}
                            Вход
                        </button>
                    </div>
                    <div className="col-md-6 me-0">
                        {this.state.errorMessage && <div className="alert alert-danger mt-1 me-0 ms-0">{this.state.errorMessage}</div>}
                    </div>
                </form>
            </div>
        );
    }
}

export default LoginComponentClass