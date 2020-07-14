export const API_BASE_URL = 'http://localhost:8080/movierama';
export const API_AUTH_URL = 'https://tsompos-movierama.auth.eu-central-1.amazoncognito.com';

export const ACCESS_TOKEN = 'accessToken';

export const MOVIES_LIST_SIZE = 5;

export const NAME_MIN_LENGTH = 4;
export const NAME_MAX_LENGTH = 40;

export const USERNAME_MIN_LENGTH = 3;
export const USERNAME_MAX_LENGTH = 15;

export const EMAIL_MAX_LENGTH = 40;

export const PASSWORD_MIN_LENGTH = 6;
export const PASSWORD_MAX_LENGTH = 20;

export const MOVIE_DESCRIPTION_MAX_LENGTH = 400;
export const MOVIE_TITLE_MAX_LENGTH = 100;

export const APPLICATION_NAME = 'Movierama';
export const SUCCESSFULL_LOGIN = "You're successfully logged in.";
export const SUCCESSFULL_LOGOUT = "You're successfully logged out.";

export const NOT_FOUND_PAGE_MESSAGE = 'The Page you are looking for was not found';
export const NOT_FOUND_PAGE_CODE = 400;
export const SERVER_ERROR_PAGE_MESSAGE = 'Oops! Something went wrong at our Server.';
export const SERVER_ERROR_PAGE_CODE = 500;
export const LOGIN_AUTHORIZATION_ERROR_MESSAGE = 'Your Username or Password is incorrect. Please try again!';
export const LOGIN_GENERAL_ERROR = 'Sorry! Something went wrong. Please try again!';
export const AUTH_SIGN_IN_URL = "/auth/signin";
export const AUTH_SIGN_UP_URL = "/auth/signup";

export const USERS_URL = "/users/";
export const MOVIES_URL = "/movies/";
export const CHECK_USERNAME_AVAILABILITY_URL = "/users/checkUsernameAvailability?username=";
export const CHECK_EMAIL_AVAILABILITY_URL = "/users/checkEmailAvailability?email="
export const CURRENT_USER_URL = "/oauth2/userInfo";
export const ADD_MOVIE_URL = "/movies";
export const CLEAR_OPINION_URL = "/opinion/clear";
export const OPINION_URL = "/opinion";
export const PREFIX_GET_MOVIES_URL = "/movies?page=";
export const PREFIX_GET_MOVIES_ORDERED_BY_LIKE_URL = "/movies/ordered/like?page=";
export const PREFIX_GET_MOVIES_ORDERED_BY_HATE_URL = "/movies/ordered/hate?page=";
export const GET_MOVIES_OPINIONS_URL = "/movies/opinions?page=";
export const SIZE_PARAMETER = "&size=";