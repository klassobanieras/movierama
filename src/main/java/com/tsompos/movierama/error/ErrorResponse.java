package com.tsompos.movierama.error;

import lombok.Value;

@Value
public class ErrorResponse {
    String errorMessage;
    int errorCode;
}
