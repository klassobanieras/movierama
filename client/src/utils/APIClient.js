import {
    API_BASE_URL,
    ACCESS_TOKEN,
    MOVIES_LIST_SIZE,
    MOVIES_URL,
    PREFIX_GET_MOVIES_URL,
    SIZE_PARAMETER,
} from '../constants/Constants';

const request = (options) => {
    const headers = new Headers({
        'Content-Type': 'application/json',
    })

    if (localStorage.getItem(ACCESS_TOKEN)) {
        headers.append('Authorization', `Bearer ${localStorage.getItem(ACCESS_TOKEN)}`)
    }

    const defaults = {headers: headers};
    options = Object.assign({}, defaults, options);

    return fetch(options.url, options).then(response =>
        response.json().then(json => {
            if (!response.ok) {
                return Promise.reject(json);
            }
            return json;
        })
    );
};

export function getUserProfile(username) {
    return request({
        url: API_BASE_URL + MOVIES_URL + username,
        method: 'GET'
    });
}

export function createMovie(movieData) {
    return request({
        url: API_BASE_URL + MOVIES_URL,
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
        url: API_BASE_URL + PREFIX_GET_MOVIES_URL + page + SIZE_PARAMETER + size + '&sort=countOfLikes,desc',
        method: 'GET'
    });
}

export function getAllMoviesOrderedByHates(page, size) {
    page = page || 0;
    size = size || MOVIES_LIST_SIZE;

    return request({
        url: API_BASE_URL + PREFIX_GET_MOVIES_URL + page + SIZE_PARAMETER + size + '&sort=countOfHates,desc',
        method: 'GET'
    });
}

export function getMoviesCreatedByUser(username, page, size) {
    page = page || 0;
    size = size || MOVIES_LIST_SIZE;

    return request({
        url: API_BASE_URL + MOVIES_URL + username + '?page=' + page + SIZE_PARAMETER + size,
        method: 'GET'
    });
}

export function expressReaction(reactionData) {

    return request({
        url: API_BASE_URL + MOVIES_URL + reactionData.movieId + '/reaction/' + reactionData.reaction,
        method: 'POST',
    });
}

export function removeReaction(movieId) {
    return request({
        url: API_BASE_URL + MOVIES_URL + movieId + '/reaction',
        method: 'DELETE',
    });
}