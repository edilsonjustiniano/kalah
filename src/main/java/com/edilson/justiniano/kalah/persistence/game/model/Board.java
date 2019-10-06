package com.edilson.justiniano.kalah.persistence.game.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.beans.Transient;
import java.util.Arrays;
import java.util.stream.IntStream;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Board {

    private static final int PLAYER_ONE_KALAH = 7;
    private static final int PLAYER_TWO_KALAH = 14;

    private int[] pits;

    @Transient
    public boolean isPlayerOnePit(int selectedPit) {
        return selectedPit >= 0 && selectedPit < (PLAYER_ONE_KALAH - 1);
    }

    @Transient
    public boolean isPlayerTwoPit(int selectedPit) {
        return selectedPit >= PLAYER_ONE_KALAH && selectedPit < (PLAYER_TWO_KALAH - 1);
    }

    @Transient
    public boolean isPlayerOneKalah(int pitIndex) {
        return PLAYER_ONE_KALAH == (pitIndex + 1);
    }

    @Transient
    public boolean isNotPlayerOneKalah(int pitIndex) {
        return PLAYER_ONE_KALAH != (pitIndex + 1);
    }

    @Transient
    public boolean isPlayerTwoKalah(int pitIndex) {
        return PLAYER_TWO_KALAH == (pitIndex + 1);
    }

    @Transient
    public boolean isNotPlayerTwoKalah(int pitIndex) {
        return PLAYER_TWO_KALAH != (pitIndex + 1);
    }

    @Transient
    public boolean isPlayerOneKalahsEmtpy() {
        return IntStream.range(0 , (PLAYER_ONE_KALAH - 2))
                .noneMatch(pit -> pit > 0);
    }

    @Transient
    public boolean isPlayerTwoKalahsEmtpy() {
        return IntStream.range(0 , (PLAYER_ONE_KALAH))
                .noneMatch(pit -> pit > 0);
    }

}