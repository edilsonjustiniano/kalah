package com.edilson.justiniano.kalah.api.service;

import com.edilson.justiniano.kalah.api.exception.GameApiException;
import com.edilson.justiniano.kalah.api.model.GameResponse;
import com.edilson.justiniano.kalah.persistence.game.model.Board;
import com.edilson.justiniano.kalah.persistence.game.model.Game;
import com.edilson.justiniano.kalah.persistence.game.model.Player;
import com.edilson.justiniano.kalah.persistence.game.repository.GameRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.edilson.justiniano.kalah.api.exception.GameApiErrorReason.GAME_NOT_FOUND;
import static com.edilson.justiniano.kalah.persistence.game.model.GameStatus.FINISHED;
import static com.edilson.justiniano.kalah.persistence.game.model.Player.PLAYER_ONE;
import static com.edilson.justiniano.kalah.persistence.game.model.Player.PLAYER_TWO;

/**
 * Class used to attend all the game controller requests. Create a game, delete, update and retrieve it.
 */
@Slf4j
@AllArgsConstructor
@Service
public class GameService {

    private static final int PLAYER_ONE_KALAH_INDEX = 6;
    private static final int PLAYER_TWO_KALAH_INDEX = 13;
    private static final int PLAYER_TWO_LAST_PIT_INDEX = 12;


    private final GameBuilder builder;
    private final GameDataValidator validator;
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

    public GameResponse makeMovement(String gameId, int pitId) throws GameApiException {
        log.info("Making a movement. GameId: {} and PitId: {}.", gameId, pitId);

        int normalizePitId = pitId - 1;
        Game game = retrieveGame(gameId);

        validator.validateMovement(game, normalizePitId);

        applyMovement(game, normalizePitId);

        log.debug("The movement has been applied successfully. GameId: {} and PitId: {}.", game.getGameId(), pitId);
        return builder.buildGameStatusResponse(game);
    }

    /*
     * Method that is responsible only to apply the movement according the selected pitId
     */
    private void applyMovement(Game game, int pitId) {
        Board board = game.getBoard();
        int numberOfStones = board.getPits()[pitId];
        board.getPits()[pitId] = 0;

        int nextPitIndex = pitId;

        do {
            if (board.isPlayerTwoKalah(nextPitIndex)) {
                nextPitIndex = 0;
            } else {
                nextPitIndex++;
            }

            if ((game.isPlayerOneTurn() && board.isNotPlayerTwoKalah(nextPitIndex)) ||
                    (game.isPlayerTwoTurn() && board.isNotPlayerOneKalah(nextPitIndex))) {
                board.getPits()[nextPitIndex] += 1;
                numberOfStones--;
            }
        } while (numberOfStones > 0);

        // TODO: Check the opposite pit is empty
        //nextPitIndex is own pit and contains only one pit get all from the opposite
        if (game.isPlayerOneTurn() && board.isPlayerOnePit(nextPitIndex)) {
            if (board.getPits()[nextPitIndex] == 1) {
                board.getPits()[nextPitIndex] = 0;
                board.getPits()[PLAYER_TWO_LAST_PIT_INDEX - nextPitIndex] = 0;
                int oppositeStones = board.getPits()[PLAYER_TWO_LAST_PIT_INDEX - nextPitIndex];
                board.getPits()[PLAYER_ONE_KALAH_INDEX] += (oppositeStones + 1);
            }
        } else if (game.isPlayerTwoTurn() && board.isPlayerTwoPit(nextPitIndex)) {
            if (board.getPits()[nextPitIndex] == 1) {
                board.getPits()[nextPitIndex] = 0;
                board.getPits()[PLAYER_TWO_LAST_PIT_INDEX - nextPitIndex] = 0;
                int oppositeStones = board.getPits()[PLAYER_TWO_LAST_PIT_INDEX - nextPitIndex];
                board.getPits()[PLAYER_TWO_KALAH_INDEX] += (oppositeStones + 1);
            }
        }

        // TODO: Check the end's game after all turns
        boolean isGameOver = false;
        if (board.isPlayerOneKalahsEmtpy()) {
            log.info("Player one has no more stone on its kalah. So, Player one lose, unfortunately. But thanks for play!");
            isGameOver = true;
        } else if (board.isPlayerTwoKalahsEmtpy()) {
            log.info("Player two has no more stone on its kalah. So, Player two lose, unfortunately. But thanks for play!");
            isGameOver = true;
        }

        if (isGameOver) {
            game.setGameStatus(FINISHED);
        } else {
            // TODO: Set the next player according the rules
            if (game.isPlayerOneTurn() && board.isNotPlayerOneKalah(nextPitIndex)) {
                game.setNextPlayer(PLAYER_TWO);
            } else if (game.isPlayerTwoTurn() && board.isNotPlayerTwoKalah(nextPitIndex)) {
                game.setNextPlayer(PLAYER_ONE);
            }
        }

        gameRepository.save(game);

    }

    private Game retrieveGame(String gameId) throws GameApiException {
        return gameRepository.findById(gameId).orElseThrow(() -> {
            log.error("The game was not found. GameId: {}.", gameId);
            return new GameApiException(GAME_NOT_FOUND);
        });
    }
}
