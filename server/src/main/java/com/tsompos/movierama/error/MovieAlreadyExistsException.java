package com.tsompos.movierama.error;

public class MovieAlreadyExistsException extends RuntimeException {
    public MovieAlreadyExistsException() {
        super("The Movie already exists");
    }
}
