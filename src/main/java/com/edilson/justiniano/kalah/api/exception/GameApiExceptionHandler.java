package com.edilson.justiniano.kalah.api.exception;

import com.edilson.justiniano.kalah.api.BaseExceptionHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GameApiExceptionHandler extends BaseExceptionHandler {

    @ExceptionHandler(GameApiException.class)
    public ResponseEntity handleError(GameApiException ex) {
        return this.generateError(ex.getErrorCode(), ex.getMessage(), ex.getHttpStatus());
    }
}
