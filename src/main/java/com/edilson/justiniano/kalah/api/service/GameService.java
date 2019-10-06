package com.edilson.justiniano.kalah.api.service;

import com.edilson.justiniano.kalah.api.exception.GameApiErrorReason;
import com.edilson.justiniano.kalah.api.exception.GameApiException;
import com.edilson.justiniano.kalah.api.model.GameResponse;
import com.edilson.justiniano.kalah.persistence.game.model.Game;
import com.edilson.justiniano.kalah.persistence.game.repository.GameRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.edilson.justiniano.kalah.api.exception.GameApiErrorReason.GAME_NOT_FOUND;

@Slf4j
@AllArgsConstructor
@Service
public class GameService {

    private final GameBuilder builder;
    private final GameRepository gameRepository;

    public GameResponse createGame() {
        log.info("Creating a new game.");

        Game game = builder.buildGame();

        log.debug("Saving the new game. GameId: {}.", game.getGameId());
        gameRepository.save(game);

        log.debug("Game successfully created. GameId: {}.", game.getGameId());
        return builder.buildGameResponse(game);
    }

    public GameResponse searchGame(String gameId) throws GameApiException {
        log.info("Searching for the game. GameId: {}.", gameId);

        Game game = retrieveGame(gameId);

        log.debug("Game successfully found. GameId: {}.", game.getGameId());
        return builder.buildGameResponse(game);
    }

    public void removeGame(String gameId) throws GameApiException {
        log.info("Deleting a game. GameId: {}.", gameId);

        Game game = retrieveGame(gameId);

        if (game.isGameRunning()) {
            //Here I could implement a data validation and disallow such operation for game still pending
            log.info("The game is still running...");
        }

        log.debug("Deleting the game. GameId: {} and status: {}.", game.getGameId(), game.getGameStatus());
        gameRepository.delete(game);

        log.debug("Game successfully deleted. GameId: {} and status: {}.", game.getGameId(), game.getGameStatus());
    }

    private Game retrieveGame(String gameId) throws GameApiException {
        return gameRepository.findById(gameId).orElseThrow(() -> {
            log.error("The game was not found. GametId: {}.", gameId);
            return new GameApiException(GAME_NOT_FOUND);
        });
    }
}
