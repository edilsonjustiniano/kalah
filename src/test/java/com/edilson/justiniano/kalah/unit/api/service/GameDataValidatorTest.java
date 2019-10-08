package com.edilson.justiniano.kalah.unit.api.service;

import com.edilson.justiniano.kalah.unit.api.exception.GameApiException;
import com.edilson.justiniano.kalah.persistence.game.model.Board;
import com.edilson.justiniano.kalah.persistence.game.model.Game;
import com.edilson.justiniano.kalah.persistence.game.model.GameStatus;
import com.edilson.justiniano.kalah.persistence.game.model.Player;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static com.edilson.justiniano.kalah.persistence.game.model.GameStatus.FINISHED;
import static com.edilson.justiniano.kalah.persistence.game.model.GameStatus.RUNNING;
import static com.edilson.justiniano.kalah.persistence.game.model.Player.PLAYER_ONE;
import static com.edilson.justiniano.kalah.persistence.game.model.Player.PLAYER_TWO;
import static java.time.Instant.now;

/**
 * Unit tests for {@link GameDataValidator} class
 */
@RunWith(JUnit4.class)
public class GameDataValidatorTest {

    private static final String GAME_ID = "gameId";
    private static final int PLAYER_ONE_VALID_PIT_ID = 1;
    private static final int PLAYER_TWO_VALID_PIT_ID = 8;

    private GameDataValidator gameDataValidator = new GameDataValidator();

    @Test
    public void validateMovement_withValidPlayerOneMovement_shouldSuccessValidate() throws GameApiException {
        // given
        Game game = buildGameRunning();

        // when
        gameDataValidator.validateMovement(game, PLAYER_ONE_VALID_PIT_ID);
    }

    @Test
    public void validateMovement_withValidPlayerTwoMovement_shouldSuccessValidate() throws GameApiException {
        // given
        Game game = buildGameRunning();
        game.setNextPlayer(PLAYER_TWO);

        // when
        gameDataValidator.validateMovement(game, PLAYER_TWO_VALID_PIT_ID);
    }

    @Test (expected = GameApiException.class)
    public void validateMovement_withGameOver_shouldThrowGameApiException() throws GameApiException {
        // given
        Game game = buildGameOver();

        // when
        gameDataValidator.validateMovement(game, PLAYER_ONE_VALID_PIT_ID);

        // then throw GameApiException
    }

    @Test (expected = GameApiException.class)
    public void validateMovement_withGameRunning_playerOneTurnAndPlayerTwoPitSelected_shouldThrowGameApiException() throws GameApiException {
        // given
        Game game = buildGameRunning();

        // when
        gameDataValidator.validateMovement(game, PLAYER_TWO_VALID_PIT_ID);

        // then throw GameApiException
    }

    @Test (expected = GameApiException.class)
    public void validateMovement_withGameRunning_playerTwoTurnAndPlayerOnePitSelected_shouldThrowGameApiException() throws GameApiException {
        // given
        Game game = buildGameRunning();
        game.setNextPlayer(PLAYER_TWO);

        // when
        gameDataValidator.validateMovement(game, PLAYER_ONE_VALID_PIT_ID);

        // then throw GameApiException
    }

    @Test (expected = GameApiException.class)
    public void validateMovement_withGameRunningAndSelectPitEmpty_shouldThrowGameApiException() throws GameApiException {
        // given
        Game game = buildGameRunning();
        game.getBoard().getPits()[PLAYER_ONE_VALID_PIT_ID] = 0;

        // when
        gameDataValidator.validateMovement(game, PLAYER_ONE_VALID_PIT_ID);

        // then throw GameApiException
    }

    private Game buildGameRunning() {
        return buildGameBuilder(RUNNING, PLAYER_ONE)
                .board(buildBoard())
                .build();
    }

    private Game buildGameOver() {
        return buildGameBuilder(FINISHED, PLAYER_ONE)
                .board(buildBoard())
                .build();
    }


    private Board buildBoard() {
        return Board.builder()
                .pits(new int[]{6, 6, 6, 6, 6, 6, 0,
                        6, 6, 6, 6, 6, 6, 0})
                .build();
    }

    private Game.GameBuilder buildGameBuilder(GameStatus gameStatus, Player player) {
        return Game.builder()
                .gameStatus(gameStatus)
                .nextPlayer(player)
                .gameId(GAME_ID)
                .startedTime(now().toEpochMilli());
    }
}
