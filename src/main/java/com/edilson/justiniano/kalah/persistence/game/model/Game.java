package com.edilson.justiniano.kalah.persistence.game.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document
public class Game {

    @Id
    private String gameId;
    private long startedTime;
    private Long duration;

    private GameStatus gameStatus;
    private Board board;

    private Player nextPlayer;

    //Using tell don't ask design pattern to encapsulate some business logic and avoid split them around the code
    // These transient annotated methods will not be stored on our db
    @Transient
    public boolean isGameOver() {
        return GameStatus.FINISHED.equals(getGameStatus());
    }

    //Using tell don't ask design pattern to encapsulate some business logic and avoid split them around the code
    @Transient
    public boolean isGameRunning() {
        return GameStatus.RUNNING.equals(getGameStatus());
    }

    @Transient
    public boolean isPlayerOneTurn() {
        return Player.PLAYER_ONE.equals(getNextPlayer());
    }

    @Transient
    public boolean isPlayerTwoTurn() {
        return Player.PLAYER_TWO.equals(getNextPlayer());
    }

    //Using tell don't ask design pattern to encapsulate some business logic and avoid split them around the code
    @Transient
    public static String generateGameId() {
        return UUID.randomUUID().toString();
    }
}
