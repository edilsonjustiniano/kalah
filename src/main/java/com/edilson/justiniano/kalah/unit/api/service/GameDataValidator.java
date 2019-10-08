package com.edilson.justiniano.kalah.unit.api.service;

import com.edilson.justiniano.kalah.unit.api.exception.GameApiException;
import com.edilson.justiniano.kalah.persistence.game.model.Board;
import com.edilson.justiniano.kalah.persistence.game.model.Game;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.edilson.justiniano.kalah.unit.api.exception.GameApiErrorReason.GAME_IS_OVER;
import static com.edilson.justiniano.kalah.unit.api.exception.GameApiErrorReason.INVALID_MOVEMENT;
import static com.edilson.justiniano.kalah.unit.api.exception.GameApiErrorReason.PIT_WITH_NO_STONE;

@Slf4j
@AllArgsConstructor
@Service
public class GameDataValidator {

    public void validateMovement(Game game, int selectedPitId) throws GameApiException {
        Board board = game.getBoard();
        if (game.isGameOver()) {
            log.error("The provided game is already over. GameId: {}.", game.getGameId());
            throw new GameApiException(GAME_IS_OVER);
        } else if (game.isPlayerOneTurn() && !board.isPlayerOnePit(selectedPitId))  {
            log.error("The selected Pit does not belong to the Player one. GameId: {} and PitId: {}.", game.getGameId(), selectedPitId);
            throw new GameApiException(INVALID_MOVEMENT);
        } else if (game.isPlayerTwoTurn() && !board.isPlayerTwoPit(selectedPitId)) {
            log.error("The selected Pit does not belong to the Player two. GameId: {} and PitId: {}.", game.getGameId(), selectedPitId);
            throw new GameApiException(INVALID_MOVEMENT);
        } else if (board.getPits()[selectedPitId] == 0) {
            log.error("There is no more stone in the selected pit. GameId: {} and PitId: {}.", game.getGameId(), selectedPitId);
            throw new GameApiException(PIT_WITH_NO_STONE);
        }
    }
}
