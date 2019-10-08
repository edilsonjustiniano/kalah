package com.edilson.justiniano.kalah.api.service;

import com.edilson.justiniano.kalah.api.model.GameResponse;
import com.edilson.justiniano.kalah.persistence.game.model.Board;
import com.edilson.justiniano.kalah.persistence.game.model.Game;
import lombok.AllArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.edilson.justiniano.kalah.persistence.game.model.GameStatus.RUNNING;
import static com.edilson.justiniano.kalah.persistence.game.model.Player.PLAYER_ONE;
import static java.time.Instant.now;

/**
 * This class is gonna be used to build any kind of objects regarding the game, even the response or Database entities
 */
@Service
@AllArgsConstructor
public class GameBuilder {

    private static final String PROPERTY_SERVER_PORT = "server.port";

    private Environment environment;

    public Game buildGame() {
        return Game.builder()
                .gameId(Game.generateGameId())
                .startedTime(now().toEpochMilli())
                .gameStatus(RUNNING)
                .nextPlayer(PLAYER_ONE)
                .board(buildBoard())
                .build();
    }

    public GameResponse buildGameResponse(Game game) {
        return buildGameResponseBuilder(game)
                .build();

    }

    public GameResponse buildGameStatusResponse(Game game) {
        return buildGameResponseBuilder(game)
                .status(buildGameStatus(game))
                .build();
    }

    private Map<Integer, Integer> buildGameStatus(Game game) {
        int[] pits = game.getBoard().getPits();
        Map<Integer, Integer> boardStatus = new HashMap<>();
        for (int i = 0; i < pits.length; i++) {
            boardStatus.put((i + 1), pits[i]);
        }

        return boardStatus;
    }

    private GameResponse.GameResponseBuilder buildGameResponseBuilder(Game game) {
        return GameResponse.builder()
                .id(game.getGameId())
                .url(generateGameUrl(game.getGameId()));
    }

    private Board buildBoard() {
        return Board.builder()
                .pits(initializePits())
                .build();
    }

    private int[] initializePits() {
        return new int[] {
                6, 6, 6, 6, 6, 6, 0,
                6, 6, 6, 6, 6, 6, 0
        };
    }

    private String generateGameUrl(String gameId) {
        String port = environment.getProperty(PROPERTY_SERVER_PORT);
        String host = InetAddress.getLoopbackAddress().getHostAddress();

        return new StringBuilder(host)
                .append(":")
                .append(port)
                .append("/games/")
                .append(gameId)
                .toString();

    }


}
