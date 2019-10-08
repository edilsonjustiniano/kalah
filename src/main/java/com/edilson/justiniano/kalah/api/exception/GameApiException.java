package com.edilson.justiniano.kalah.api.exception;

import com.edilson.justiniano.kalah.exception.BaseException;

public class GameApiException extends BaseException {

    public GameApiException(GameApiErrorReason gameApiErrorReason) {
        super(gameApiErrorReason.getErrorCode(), gameApiErrorReason.getMessage(), gameApiErrorReason.getHttpStatus());
    }

}
