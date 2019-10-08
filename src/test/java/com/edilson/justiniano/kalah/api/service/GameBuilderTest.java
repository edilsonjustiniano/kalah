package com.edilson.justiniano.kalah.api.service;

import com.edilson.justiniano.kalah.api.model.GameResponse;
import com.edilson.justiniano.kalah.persistence.game.model.Board;
import com.edilson.justiniano.kalah.persistence.game.model.Game;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.env.Environment;

import static com.edilson.justiniano.kalah.persistence.game.model.GameStatus.RUNNING;
import static com.edilson.justiniano.kalah.persistence.game.model.Player.PLAYER_ONE;
import static com.edilson.justiniano.kalah.persistence.game.model.Player.PLAYER_TWO;
import static java.time.Instant.now;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.BDDMockito.given;

/**
 * Unit tests for {@link GameBuilder} class
 */
@RunWith(MockitoJUnitRunner.class)
public class GameBuilderTest {

    private static final String GAME_ID = "gameId";
    private static final String APP_PORT = "port";
    private static final String GAME_URL = "127.0.0.1:" + APP_PORT + "/games/" + GAME_ID;
    private static final String PROPERTY_SERVER_PORT = "server.port";

    @Mock
    private Environment environment;

    @InjectMocks
    private GameBuilder gameBuilder;

    @Test
    public void buildGame_shouldReturnAGameSuccessfully() {
        // when
        Game result = gameBuilder.buildGame();

        // then
        assertNotNull(result);
        assertNotNull(result.getGameId());
        assertThat(result.getGameStatus(), equalTo(RUNNING));
        assertThat(result.getNextPlayer(), equalTo(PLAYER_ONE));
    }

    @Test
    public void buildGameResponse_shouldReturnAGameResponseSuccessfully() {
        // given
        Game game = buildGame();
        given(environment.getProperty("server.port")).willReturn(APP_PORT);

        // when
        GameResponse result = gameBuilder.buildGameResponse(game);

        // then
        assertThat(result.getId(), equalTo(GAME_ID));
        assertThat(result.getUrl(), equalTo(GAME_URL));
        assertNull(result.getStatus());
    }

    @Test
    public void buildGameStatusResponse_withPitsInitialized_shouldReturnAGameStatusResponseSuccessfully() {
        // given
        Game game = buildGame();
        given(environment.getProperty(PROPERTY_SERVER_PORT)).willReturn(APP_PORT);

        // when
        GameResponse result = gameBuilder.buildGameStatusResponse(game);

        // then
        assertThat(result.getId(), equalTo(GAME_ID));
        assertThat(result.getUrl(), equalTo(GAME_URL));
        assertNotNull(result.getStatus());
    }

    @Test
    public void buildGameStatusResponse_withPitsEmpty_shouldReturnAGameStatusResponseSuccessfully() {
        // given
        Game game = buildGameWithoutPits();
        given(environment.getProperty(PROPERTY_SERVER_PORT)).willReturn(APP_PORT);

        // when
        GameResponse result = gameBuilder.buildGameStatusResponse(game);

        // then
        assertThat(result.getId(), equalTo(GAME_ID));
        assertThat(result.getUrl(), equalTo(GAME_URL));
        assertNotNull(result.getStatus());
    }

    private Game buildGameWithoutPits() {
        return buildGameBuilder()
                .board(buildEmptyBoard())
                .build();
    }

    private Game buildGame() {
        return buildGameBuilder()
                .board(buildBoard())
                .build();
    }

    private Game.GameBuilder buildGameBuilder() {
        return Game.builder()
                .gameStatus(RUNNING)
                .nextPlayer(PLAYER_TWO)
                .gameId(GAME_ID)

                .startedTime(now().toEpochMilli());
    }

    private Board buildBoard() {
        return Board.builder()
                .pits(new int[14])
                .build();
    }

    private Board buildEmptyBoard() {
        return Board.builder()
                .pits(new int[0])
                .build();
    }



}
