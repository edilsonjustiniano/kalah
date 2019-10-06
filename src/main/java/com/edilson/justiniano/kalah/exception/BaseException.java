package com.edilson.justiniano.kalah.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
//Create a base exception that could be used for all the new exceptions that could be appear in a future
public class BaseException extends Exception {

    private String message;
    private String errorCode;
    private HttpStatus httpStatus;

    public BaseException(String errorCode, String message, HttpStatus httpStatus) {
        this.message = message;
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }
}