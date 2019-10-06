package com.edilson.justiniano.kalah.persistence.game.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Document
public class Game {

    @Id
    private String gameId;
    private long startedTime;
    private long duration;

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public long getStartedTime() {
        return startedTime;
    }

    public void setStartedTime(long startedTime) {
        this.startedTime = startedTime;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public static class Builder {

        private Game game = new Game();

        public Builder gameId(String gameId) {
            game.setGameId(gameId);
            return this;
        }

        public Builder startedTime(long startedTime) {
            game.setStartedTime(startedTime);
            return this;
        }

        public Builder duration(long duration) {
            game.setDuration(duration);
            return this;
        }

        public Game build() {
            return game;
        }
    }
}
