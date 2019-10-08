package com.edilson.justiniano.kalah.api.service;

import com.edilson.justiniano.kalah.api.exception.GameApiException;
import com.edilson.justiniano.kalah.api.model.GameResponse;
import com.edilson.justiniano.kalah.persistence.game.model.Board;
import com.edilson.justiniano.kalah.persistence.game.model.Game;
import com.edilson.justiniano.kalah.persistence.game.repository.GameRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;

import static com.edilson.justiniano.kalah.api.exception.GameApiErrorReason.GAME_NOT_FOUND;
import static com.edilson.justiniano.kalah.persistence.game.model.GameStatus.FINISHED;
import static com.edilson.justiniano.kalah.persistence.game.model.Player.PLAYER_ONE;
import static com.edilson.justiniano.kalah.persistence.game.model.Player.PLAYER_TWO;
import static java.time.Duration.between;
import static java.time.Instant.now;

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

    /*
     * Method used to create a new game
     */
    public GameResponse createGame() {
        log.info("Creating a new game.");

        Game game = builder.buildGame();

        log.debug("Saving the new game. GameId: {}.", game.getGameId());
        gameRepository.save(game);

        log.debug("Game successfully created. GameId: {}.", game.getGameId());
        return builder.buildGameResponse(game);
    }

    /*
     * Method used to retrieve a game based on its id
     */
    public GameResponse searchGame(String gameId) throws GameApiException {
        log.info("Searching for the game. GameId: {}.", gameId);

        Game game = retrieveGame(gameId);

        log.debug("Game successfully found. GameId: {}.", game.getGameId());
        return builder.buildGameResponse(game);
    }

    /*
     * Method used to remove a game. So far, no validation is done about the game status, but it could be done easily
     */
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

    /*
     * Method used to start the movement selected by the player one or two
     */
    public GameResponse makeMovement(String gameId, int pitId) throws GameApiException {
        log.info("Making a movement. GameId: {} and PitId: {}.", gameId, pitId);

        int normalizePitId = pitId - 1;
        Game game = retrieveGame(gameId);

        validator.validateMovement(game, normalizePitId);

        startMovement(game, normalizePitId);

        log.debug("The movement has been done successfully. GameId: {} and PitId: {}.", game.getGameId(), pitId);
        return builder.buildGameStatusResponse(game);
    }

    /*
     * Method that is responsible only to apply the movement according the selected pitId
     */
    private void startMovement(Game game, int pitId) {

        int nextPitIndex = moveStones(game, pitId);

        // Check the opposite pit is empty
        takeOppositeStones(game, nextPitIndex);

        // Check the end's game after all turns
        boolean isGameOver = isGameOver(game.getBoard());

        if (isGameOver) {
            setGameAsFinished(game);
        } else {
            // Set the next player according the rules
            setNextPlayer(game, nextPitIndex);
        }

        log.debug("Saving the game after apply the movement. GameId: {}.", game.getGameId());
        gameRepository.save(game);

    }

    private int moveStones(Game game, int pitId) {
        Board board = game.getBoard();
        int numberOfStones = board.getPits()[pitId];
        board.getPits()[pitId] = 0;

        int nextPitIndex = pitId;

        do {
            // We need to reset the next Pit index if it is the Player two's kalah
            if (board.isPlayerTwoKalah(nextPitIndex)) {
                nextPitIndex = 0;
            } else {
                nextPitIndex++;
            }

            // The stones cannot be put in the opponent's kalah. So, skip it
            if ((game.isPlayerOneTurn() && board.isNotPlayerTwoKalah(nextPitIndex)) ||
                    (game.isPlayerTwoTurn() && board.isNotPlayerOneKalah(nextPitIndex))) {
                board.getPits()[nextPitIndex] += 1;
                numberOfStones--;
            }
        } while (numberOfStones > 0);

        return nextPitIndex;
    }

    private void takeOppositeStones(Game game, int nextPitIndex) {
        Board board = game.getBoard();
        // If it is the player one turn and the last stone is put in his/her owns pit, get the opposite pits stone
        if (game.isPlayerOneTurn() && board.isPlayerOnePit(nextPitIndex)) {
            if (board.getPits()[nextPitIndex] == 1) {
                collectOppositeStones(board, nextPitIndex, PLAYER_ONE_KALAH_INDEX);
            }
        } else if (game.isPlayerTwoTurn() && board.isPlayerTwoPit(nextPitIndex)) {
            if (board.getPits()[nextPitIndex] == 1) {
                collectOppositeStones(board, nextPitIndex, PLAYER_TWO_KALAH_INDEX);
            }
        }
    }

    private void collectOppositeStones(Board board, int nextPitIndex, int kalahPalyerIndex) {
        // In order to find the opposite pit index I did the following calc (assuming the following pit positions):
        //   12 11 10  9  8  7
        // 13                  6
        //    0  1  2  3  4  5
        // 12 = Last Player two pit
        // So to get the opposite pit is just subtract the last player two pit index to the nextPitId.
        // For example: 12 - 3 (nextPitId) = 9. So, the opposite pit of 3 is the pit of index 9
        // The same logic but in inverse way
        board.getPits()[nextPitIndex] = 0;
        board.getPits()[PLAYER_TWO_LAST_PIT_INDEX - nextPitIndex] = 0;
        int oppositeStones = board.getPits()[PLAYER_TWO_LAST_PIT_INDEX - nextPitIndex];
        board.getPits()[kalahPalyerIndex] += (oppositeStones + 1);
    }

    private void setNextPlayer(Game game, int nextPitIndex) {
        Board board = game.getBoard();
        if (game.isPlayerOneTurn() && board.isNotPlayerOneKalah(nextPitIndex)) {
            game.setNextPlayer(PLAYER_TWO);
        } else if (game.isPlayerTwoTurn() && board.isNotPlayerTwoKalah(nextPitIndex)) {
            game.setNextPlayer(PLAYER_ONE);
        }
    }

    private boolean isGameOver(Board board) {
        boolean isGameOver = false;
        if (board.isPlayerOnePitsEmpty()) {
            log.info("Player one has no more stone on its kalah. So, Player one lose, unfortunately. But thanks for play!");
            isGameOver = true;
        } else if (board.isPlayerTwoPitsEmpty()) {
            log.info("Player two has no more stone on its kalah. So, Player two lose, unfortunately. But thanks for play!");
            isGameOver = true;
        }

        return isGameOver;
    }

    private void setGameAsFinished(Game game) {
        game.setGameStatus(FINISHED);

        // I added by myself at the model entity the field duration... It could be used in the future to check how long
        // the game is taken. I though it could be helpful to have it
        Instant startedTime = Instant.ofEpochMilli(game.getStartedTime());
        Instant currentTime = now();
        long duration = between(startedTime, currentTime).getSeconds();
        game.setDuration(duration);
    }

    private Game retrieveGame(String gameId) throws GameApiException {
        return gameRepository.findById(gameId).orElseThrow(() -> {
            log.error("The game was not found. GameId: {}.", gameId);
            return new GameApiException(GAME_NOT_FOUND);
        });
    }
}
