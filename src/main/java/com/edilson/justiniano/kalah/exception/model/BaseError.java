package com.edilson.justiniano.kalah.exception.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
//Create a base error to be presented to front-end in a JSON format for any exception
public class BaseError {

    private String message;
    private String errorCode;
}
