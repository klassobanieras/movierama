package com.tsompos.movierama.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;

@RestControllerAdvice
@Slf4j
public class RestExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse processException(Exception exception) {
        log.error(exception.getMessage());
        return new ErrorResponse(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @ExceptionHandler(EntityExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse processException(EntityExistsException exception) {
        return new ErrorResponse(exception.getMessage(), HttpStatus.CONFLICT.value());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse notFoundException(EntityNotFoundException exception) {
        return new ErrorResponse(exception.getMessage(), HttpStatus.NOT_FOUND.value());
    }

    @ExceptionHandler({MultipleReactionsException.class, OwnMovieRecommendation.class, IllegalArgumentException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse notFoundException(RuntimeException exception) {
        return new ErrorResponse(exception.getMessage(), HttpStatus.BAD_REQUEST.value());
    }

    public static record ErrorResponse(String errorMessage, int errorCode) {
    }
}
