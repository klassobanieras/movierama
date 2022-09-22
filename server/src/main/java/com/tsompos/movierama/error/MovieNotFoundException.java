package com.tsompos.movierama.error;

public class MovieNotFoundException extends RuntimeException {
    public MovieNotFoundException() {
        super("MovieWithUsersReaction not found.");
    }
}
