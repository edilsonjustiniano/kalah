package com.edilson.justiniano.kalah.api.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum GameApiErrorReason {

    GAME_NOT_FOUND("game.notfound", "The game was not found.", HttpStatus.NOT_FOUND);

    //ErrorCode: Used for Front-end applications to replace the message properly. It does also help the app internationalization
    private String errorCode;
    //message: This is the message used by the Back-end to let us know about what happened on the request
    private String message;
    //HttpStatus: To avoid the unnecessary new Exceptions since I can handle all the possible error scenarios using only one with the HTTP status variation only
    private HttpStatus httpStatus;
}
