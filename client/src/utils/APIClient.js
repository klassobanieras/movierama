import {
    API_BASE_URL,
    ACCESS_TOKEN,
    MOVIES_LIST_SIZE,
    AUTH_SIGN_IN_URL,
    AUTH_SIGN_UP_URL,
    CHECK_USERNAME_AVAILABILITY_URL,
    CHECK_EMAIL_AVAILABILITY_URL,
    CURRENT_USER_URL,
    USERS_URL,
    ADD_MOVIE_URL,
    MOVIES_URL,
    CLEAR_OPINION_URL,
    PREFIX_GET_MOVIES_URL,
    OPINION_URL,
    PREFIX_GET_MOVIES_ORDERED_BY_LIKE_URL,
    PREFIX_GET_MOVIES_ORDERED_BY_HATE_URL,
    GET_MOVIES_OPINIONS_URL,
    SIZE_PARAMETER, API_AUTH_URL,
} from '../constants/Constants';

const request = (options) => {
    const headers = new Headers({
        'Content-Type': 'application/json',
    })
    
    if(localStorage.getItem(ACCESS_TOKEN)) {
        headers.append('Authorization', 'Bearer ' + localStorage.getItem(ACCESS_TOKEN))
    }

    const defaults = {headers: headers};
    options = Object.assign({}, defaults, options);

    return fetch(options.url, options).then(response =>
        response.json().then(json => {
            if(!response.ok) {
                return Promise.reject(json);
            }
            return json;
        })
    );
};

export function login(loginRequest) {
    return request({
        url: API_AUTH_URL + AUTH_SIGN_IN_URL,
        method: 'POST',
        body: JSON.stringify(loginRequest)
    });
}

export function signup(signupRequest) {
    return request({
        url: API_AUTH_URL + AUTH_SIGN_UP_URL,
        method: 'POST',
        body: JSON.stringify(signupRequest)
    });
}

export function checkUsernameAvailability(username) {
    return request({
        url: API_AUTH_URL + CHECK_USERNAME_AVAILABILITY_URL + username,
        method: 'GET'
    });
}

export function checkEmailAvailability(email) {
    return request({
        url: API_AUTH_URL + CHECK_EMAIL_AVAILABILITY_URL + email,
        method: 'GET'
    });
}

export function getCurrentUser() {
    if(!localStorage.getItem(ACCESS_TOKEN)) {
        return Promise.reject("No access token set.");
    }

    return request({
        url: API_AUTH_URL + CURRENT_USER_URL,
        method: 'GET'
    });
}

export function getUserProfile(username) {
    return request({
        url: API_BASE_URL + MOVIES_URL + username,
        method: 'GET'
    });
}

export function createMovie(username, movieData) {
    return request({
        url: API_BASE_URL + USERS_URL + username + ADD_MOVIE_URL,
        method: 'POST',
        body: JSON.stringify(movieData)         
    });
}

export function getAllMovies(page, size) {
    page = page || 0;
    size = size || MOVIES_LIST_SIZE;

    return request({
        url: API_BASE_URL + PREFIX_GET_MOVIES_URL + page + SIZE_PARAMETER + size,
        method: 'GET'
    });
}

export function getAllMoviesOrderedByLikes(page, size) {
    page = page || 0;
    size = size || MOVIES_LIST_SIZE;

    return request({
        url: API_BASE_URL + PREFIX_GET_MOVIES_ORDERED_BY_LIKE_URL + page + SIZE_PARAMETER + size,
        method: 'GET'
    });
}

export function getAllMoviesOrderedByHates(page, size) {
    page = page || 0;
    size = size || MOVIES_LIST_SIZE;

    return request({
        url: API_BASE_URL + PREFIX_GET_MOVIES_ORDERED_BY_HATE_URL + page + SIZE_PARAMETER + size,
        method: 'GET'
    });
}

export function getMoviesCreatedByUser(username, page, size) {
    page = page || 0;
    size = size || MOVIES_LIST_SIZE;

    return request({
        url: API_BASE_URL + USERS_URL + username + PREFIX_GET_MOVIES_URL + page + SIZE_PARAMETER + size,
        method: 'GET'
    });
}

export function getOpinions(username, page, size) {
    page = page || 0;
    size = size || MOVIES_LIST_SIZE;

    return request({
        url: API_BASE_URL + USERS_URL + username + GET_MOVIES_OPINIONS_URL + page + SIZE_PARAMETER + size,
        method: 'GET'
    });
}

export function expressOpinion(opinionData) {
    const opinionRequest = {
        opinionText : opinionData.opinion
    };
    return request({
        url: API_BASE_URL + MOVIES_URL + opinionData.movieId + OPINION_URL,
        method: 'POST',
        body: JSON.stringify(opinionRequest)
    });
}

export function clearOpinion(movieId) {
    return request({
        url: API_BASE_URL + MOVIES_URL + movieId + CLEAR_OPINION_URL,
        method: 'POST',
    });
}