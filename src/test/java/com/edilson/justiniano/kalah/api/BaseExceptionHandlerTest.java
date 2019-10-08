package com.edilson.justiniano.kalah.api;

import com.edilson.justiniano.kalah.exception.model.BaseError;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Unit tests for {@link BaseExceptionHandler} class
 */
@RunWith(JUnit4.class)
public class BaseExceptionHandlerTest {

    private static final String ERROR_CODE = "ERROR_CODE";
    private static final String ERROR_MESSAGE = "ERROR_MESSAGE";
    private static final HttpStatus HTTP_STATUS_ERROR = HttpStatus.BAD_REQUEST;

    private BaseExceptionHandler baseExceptionHandler = new BaseExceptionHandler();

    @Test
    public void generateError_shouldGenerateErrorSuccessfully() {
        // given
        BaseError baseError = buildBaseError();

        // when
        ResponseEntity<BaseError> result = baseExceptionHandler.generateError(ERROR_CODE,
                                                                              ERROR_MESSAGE,
                                                                              HTTP_STATUS_ERROR);

        // then
        assertThat(result.getStatusCode(), equalTo(HTTP_STATUS_ERROR));
        assertThat(result.getBody().getErrorCode(), equalTo(baseError.getErrorCode()));
        assertThat(result.getBody().getMessage(), equalTo(baseError.getMessage()));
    }

    private BaseError buildBaseError() {
        return BaseError.builder()
                .errorCode(ERROR_CODE)
                .message(ERROR_MESSAGE)
                .build();
    }
}
