package com.edilson.justiniano.kalah.api.service;

import com.edilson.justiniano.kalah.api.model.GameResponse;
import com.edilson.justiniano.kalah.persistence.game.model.Board;
import com.edilson.justiniano.kalah.persistence.game.model.Game;
import com.edilson.justiniano.kalah.persistence.game.model.GameStatus;
import com.edilson.justiniano.kalah.persistence.game.model.Player;
import com.edilson.justiniano.kalah.persistence.game.repository.GameRepository;
import lombok.AllArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.time.Instant;
import java.util.UUID;

import static com.edilson.justiniano.kalah.persistence.game.model.GameStatus.RUNNING;
import static com.edilson.justiniano.kalah.persistence.game.model.Player.PLAYER_ONE;
import static java.time.Instant.now;

/**
 * This class is gonna be used to build any kind of objects regarding the game, even the response or Database entities
 */
@Service
@AllArgsConstructor
public class GameBuilder {

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
        return GameResponse.builder()
                .id(game.getGameId())
                .url(generateGameUrl(game.getGameId()))
                .build();

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
        String port = environment.getProperty("server.port");
        String host = InetAddress.getLoopbackAddress().getHostAddress();

        return new StringBuilder("http://")
                .append(host)
                .append(":")
                .append(port)
                .append("/games/")
                .append(gameId)
                .toString();

    }

}
