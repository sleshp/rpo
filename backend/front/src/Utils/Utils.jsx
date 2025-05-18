
class Utils {

    saveUser(user) {
        let saveableUser = user.user;
        let authInfo = user;
        localStorage.setItem('user', JSON.stringify(saveableUser))
        localStorage.setItem('auth', JSON.stringify(authInfo))
    }

    removeUser() {
        localStorage.removeItem('user')
        localStorage.removeItem('auth')
    }

    getToken()
    {
        let user = JSON.parse(localStorage.getItem('auth'))
        console.log(user)
        return user && "Bearer " + user.token;
    }

    getUserName()
    {
        let user = JSON.parse(localStorage.getItem('user'))
        return user && user.login;
    }

    getUser()
    {
        return JSON.parse(localStorage.getItem('user'))
    }
}

export default new Utils()
