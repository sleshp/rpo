import axios from 'axios'
import Utils from "../utils/Utils";
import {store, alertActions} from "../utils/Rdx";

const API_URL = 'http://localhost:8080/api/v1'
const AUTH_URL = 'http://localhost:8080/auth'

const apiClient = axios.create();

function showError(msg) {
    store.dispatch(alertActions.error(msg))
}

apiClient.interceptors.request.use(
    config => {
        store.dispatch(alertActions.clear())
        let token = Utils.getToken();
        if (token)
            config.headers.Authorization = token;
        return config;
    },
    error => {
        showError(error.message)
        return Promise.reject(error);
    }
)

apiClient.interceptors.response.use(undefined,
    error => {
        if (error.response && error.response.status && [401, 403].indexOf(error.response.status) !== -1)
            showError("Authorization error from interceptor")
        else if (error.response && error.response.data && error.response.data.message)
            showError(error.response.data.message)
        else
            showError(error.message)
        return Promise.reject(error);
    })


class BackendService {

    login(login, password) {
        return axios.post(`${AUTH_URL}/login`, {login, password})
    }

    logout() {
        return axios.get(`${AUTH_URL}/logout`, { headers : {Authorization : Utils.getToken()}})
    }

    retrieveAllCountries(page = 0, limit) {
        console.log(page)
        return apiClient.get(`${API_URL}/countries?page=${page}&limit=${limit}`);
    }
    retrieveCountry(id) {
        return apiClient.get(`${API_URL}/countries/${id}`);
    }

    createCountry(country) {
        return apiClient.post(`${API_URL}/countries`, country);
    }

    updateCountry(country) {
        return apiClient.put(`${API_URL}/countries/${country.id}`, country);
    }

    deleteCountries(countries) {
        return apiClient.post(`${API_URL}/countries/deletecountries`, countries);
    }

    retrieveAllArtists(page = 0, limit) {
        return apiClient.get(`${API_URL}/artists?page=${page}&limit=${limit}`, { headers : {Authorization : Utils.getToken()}});
    }

    retrieveArtist(id) {
        return apiClient.get(`${API_URL}/artists/${id}`, { headers : {Authorization : Utils.getToken()}});
    }

    createArtist(artist) {
        return apiClient.post(`${API_URL}/artists`, artist, { headers : {Authorization : Utils.getToken()}});
    }

    updateArtist(artist) {
        return apiClient.put(`${API_URL}/artists/${artist.id}`, artist, { headers : {Authorization : Utils.getToken()}});
    }

    deleteArtists(artists) {
        return apiClient.post(`${API_URL}/artists/deleteartists`, artists, { headers : {Authorization : Utils.getToken()}});
    }

    // Таблица "Музеи"
    retrieveAllMuseums(page = 0, limit) {
        return apiClient.get(`${API_URL}/museums?page=${page}&limit=${limit}`, { headers : {Authorization : Utils.getToken()}});
    }

    retrieveMuseum(id) {
        return apiClient.get(`${API_URL}/museums/${id}`, { headers : {Authorization : Utils.getToken()}});
    }

    createMuseum(museum) {
        return apiClient.post(`${API_URL}/museums`, museum, { headers : {Authorization : Utils.getToken()}});
    }

    updateMuseum(museum) {
        return apiClient.put(`${API_URL}/museums/${museum.id}`, museum, { headers : {Authorization : Utils.getToken()}});
    }

    deleteMuseums(museums) {
        return apiClient.post(`${API_URL}/museums/deletemuseums`, museums, { headers : {Authorization : Utils.getToken()}});
    }

    // Таблица "Paintings"
    retrieveAllPaintings(page = 0, limit) {
        return apiClient.get(`${API_URL}/paintings?page=${page}&limit=${limit}`, { headers : {Authorization : Utils.getToken()}});
    }

    retrievePainting(id) {
        return apiClient.get(`${API_URL}/paintings/${id}`, { headers : {Authorization : Utils.getToken()}});
    }

    createPainting(painting) {
        return apiClient.post(`${API_URL}/paintings`, painting, { headers : {Authorization : Utils.getToken()}});
    }

    updatePainting(painting) {
        return apiClient.put(`${API_URL}/paintings/${painting.id}`, painting, { headers : {Authorization : Utils.getToken()}});
    }

    deletePaintings(painting) {
        return apiClient.post(`${API_URL}/paintings/deletepaintings`, painting, { headers : {Authorization : Utils.getToken()}});
    }

    // Таблица "Users"
    retrieveAllUsers(page = 0, limit) {
        return apiClient.get(`${API_URL}/users?page=${page}&limit=${limit}`, { headers : {Authorization : Utils.getToken()}});
    }

    retrieveUser(id) {
        return apiClient.get(`${API_URL}/users/${id}`, { headers : {Authorization : Utils.getToken()}});
    }

    createUser(user) {
        return apiClient.post(`${API_URL}/users`, user, { headers : {Authorization : Utils.getToken()}});
    }

    updateUser(user) {
        return apiClient.put(`${API_URL}/users/${user.id}`, user, { headers : {Authorization : Utils.getToken()}});
    }

    deleteUsers(user) {
        return apiClient.post(`${API_URL}/users/deleteusers`, user, { headers : {Authorization : Utils.getToken()}});
    }

}

export default new BackendService()
