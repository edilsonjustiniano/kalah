package com.edilson.justiniano.kalah.unit.api;

import com.edilson.justiniano.kalah.exception.model.BaseError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

// A base exception handler to generate a JSON error message and the http status defined by each exception case
public class BaseExceptionHandler {

    public ResponseEntity<BaseError> generateError(String errorCode, String message, HttpStatus httpStatus) {
        return ResponseEntity
                .status(httpStatus)
                .body(buildBaseError(errorCode, message));
    }

    private BaseError buildBaseError(String errorCode, String message) {
        return BaseError.builder()
                .errorCode(errorCode)
                .message(message)
                .build();
    }
}
