package com.edilson.justiniano.kalah.api.controller;

import com.edilson.justiniano.kalah.api.exception.GameApiException;
import com.edilson.justiniano.kalah.api.model.GameResponse;
import com.edilson.justiniano.kalah.api.service.GameService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.edilson.justiniano.kalah.api.ApiConstants.PATH_PARAM_GAME_ID;
import static com.edilson.justiniano.kalah.api.ApiConstants.PATH_PARAM_PIT_ID;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@AllArgsConstructor
@RestController
public class GameController {

    private static final String GAME_URL = "/games";
    private static final String GAME_URL_WITH_GAME_ID = "/games/{gameId}";
    private static final String MOVEMENT_URL = "/games/{gameId}/pits/{pitId}";

    private final GameService gameService;

    @PostMapping(value = GAME_URL, consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<GameResponse> createGame() {
        log.info("Requesting a new game.");

        return ResponseEntity
                .status(CREATED)
                .body(gameService.createGame());
    }

    @GetMapping(GAME_URL_WITH_GAME_ID)
    public ResponseEntity<GameResponse> getGame(@PathVariable(PATH_PARAM_GAME_ID) String gameId) throws GameApiException {
        log.info("Getting the game. GameId: {}.", gameId);

        return ResponseEntity
                .ok(gameService.searchGame(gameId));
    }

    @DeleteMapping(GAME_URL_WITH_GAME_ID)
    public ResponseEntity deleteGame(@PathVariable(PATH_PARAM_GAME_ID) String gameId) throws GameApiException {
        log.info("Requesting the game deletion. GameId: {}.", gameId);

        gameService.removeGame(gameId);

        return ResponseEntity
                .noContent()
                .build();
    }

    @PutMapping(value = MOVEMENT_URL, consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<GameResponse> moveGame(@PathVariable(PATH_PARAM_GAME_ID) String gameId,
                                                 @PathVariable(PATH_PARAM_PIT_ID) int pitId) throws GameApiException {
        log.info("Making a movement in the game. GameId: {} and PitId: {}.", gameId, pitId);


        return ResponseEntity
                .ok(gameService.makeMovement(gameId, pitId));
    }
}
