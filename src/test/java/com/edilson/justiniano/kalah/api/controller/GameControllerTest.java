package com.edilson.justiniano.kalah.api.controller;

import com.edilson.justiniano.kalah.api.exception.GameApiException;
import com.edilson.justiniano.kalah.api.model.GameResponse;
import com.edilson.justiniano.kalah.api.service.GameService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import static java.util.Collections.emptyMap;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

/**
 * Unit tests for {@link GameController} class
 */
@RunWith(MockitoJUnitRunner.class)
public class GameControllerTest {

    private static final String VALID_GAME_ID = "gameId";
    private static final String INVALID_GAME_ID = "invalidGameId";
    private static final String GAME_URL = "gameUrl";
    private static final int VALID_PIT_ID = 1;
    private static final int INVALID_PIT_ID = 0;

    @Mock
    private GameService gameService;

    @InjectMocks
    private GameController gameController;

    @Test
    public void createGame_shouldCreateGameSuccessfully() {
        // given
        GameResponse expectedGameResponse = buildGameResponse();
        given(gameService.createGame()).willReturn(expectedGameResponse);

        // when
        ResponseEntity<GameResponse> result = gameController.createGame();

        // then
        assertThat(result.getStatusCode(), equalTo(CREATED));
        assertThat(result.getBody(), equalTo(expectedGameResponse));
        verify(gameService).createGame();
    }

    @Test
    public void getGame_withValidGameId_shouldReturnGameSuccessfully() throws GameApiException {
        // given
        GameResponse expectedGameResponse = buildGameResponse();
        given(gameService.searchGame(VALID_GAME_ID)).willReturn(expectedGameResponse);

        // when
        ResponseEntity<GameResponse> result = gameController.getGame(VALID_GAME_ID);

        // then
        assertThat(result.getStatusCode(), equalTo(OK));
        assertThat(result.getBody(), equalTo(expectedGameResponse));
        verify(gameService).searchGame(VALID_GAME_ID);

    }

    @Test(expected = GameApiException.class)
    public void getGame_withInvalidGameId_shouldThrowGameApiException() throws GameApiException {
        // given
        given(gameService.searchGame(INVALID_GAME_ID)).willThrow(GameApiException.class);

        // when
        gameController.getGame(INVALID_GAME_ID);

        // then throw GameApiException
        verify(gameService).searchGame(INVALID_GAME_ID);
    }

    @Test
    public void deleteGame_withValidGameId_shouldRemoveGameSuccessfully() throws GameApiException {
        // given
        doNothing().when(gameService).removeGame(VALID_GAME_ID);

        // when
        ResponseEntity result = gameController.deleteGame(VALID_GAME_ID);

        // then
        assertThat(result.getStatusCode(), equalTo(NO_CONTENT));
        assertNull(result.getBody());
        verify(gameService).removeGame(VALID_GAME_ID);

    }

    @Test(expected = GameApiException.class)
    public void deleteGame_withInvalidGameId_shouldThrowGameApiException() throws GameApiException {
        // given
        doThrow(GameApiException.class).when(gameService).removeGame(INVALID_GAME_ID);

        // when
        gameController.deleteGame(INVALID_GAME_ID);

        // then throw GameApiException
        verify(gameService).removeGame(INVALID_GAME_ID);
    }

    @Test
    public void moveGame_withValidParameters_shouldMoveGameSuccessfully() throws GameApiException {
        // given
        GameResponse expectedGameResponse = buildGameResponse();
        given(gameService.makeMovement(VALID_GAME_ID, VALID_PIT_ID)).willReturn(expectedGameResponse);

        // when
        ResponseEntity<GameResponse> result = gameController.moveGame(VALID_GAME_ID, VALID_PIT_ID);

        // then
        assertThat(result.getStatusCode(), equalTo(OK));
        assertThat(result.getBody(), equalTo(expectedGameResponse));
        verify(gameService).makeMovement(VALID_GAME_ID, VALID_PIT_ID);

    }

    @Test (expected = GameApiException.class)
    public void moveGame_withInvalidParameters_shouldThrowGameApiException() throws GameApiException {
        // given
        given(gameService.makeMovement(INVALID_GAME_ID, INVALID_PIT_ID)).willThrow(GameApiException.class);

        // when
        gameController.moveGame(INVALID_GAME_ID, INVALID_PIT_ID);

        // then throw GameApiException
        verify(gameService). makeMovement(INVALID_GAME_ID, INVALID_PIT_ID);

    }

    private GameResponse buildGameResponse() {
        return GameResponse.builder()
                .id(VALID_GAME_ID)
                .status(emptyMap())
                .url(GAME_URL)
                .build();
    }
}
