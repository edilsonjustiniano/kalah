package com.edilson.justiniano.kalah.api.service;

import com.edilson.justiniano.kalah.api.exception.GameApiException;
import com.edilson.justiniano.kalah.api.model.GameResponse;
import com.edilson.justiniano.kalah.persistence.game.model.Board;
import com.edilson.justiniano.kalah.persistence.game.model.Game;
import com.edilson.justiniano.kalah.persistence.game.repository.GameRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Optional;

import static com.edilson.justiniano.kalah.persistence.game.model.GameStatus.FINISHED;
import static com.edilson.justiniano.kalah.persistence.game.model.GameStatus.RUNNING;
import static com.edilson.justiniano.kalah.persistence.game.model.Player.PLAYER_ONE;
import static com.edilson.justiniano.kalah.persistence.game.model.Player.PLAYER_TWO;
import static java.time.Instant.now;
import static java.util.Collections.emptyMap;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

/**
 * Unit tests for {@link GameService} class
 */
@RunWith(MockitoJUnitRunner.class)
public class GameServiceTest {

    private static final String GAME_ID = "gameId";
    private static final String GAME_URL = "gameUrl";
    private static final int PLAYER_ONE_FIRST_PIT = 1;
    private static final int PLAYER_ONE_FIRST_PIT_INDEX = PLAYER_ONE_FIRST_PIT - 1;

    private static final int PLAYER_ONE_SIXTH_PIT = 6;
    private static final int PLAYER_ONE_SIXTH_PIT_INDEX = PLAYER_ONE_SIXTH_PIT - 1;

    private static final int PLAYER_TWO_THIRTEENTH_PIT = 13;
    private static final int PLAYER_TWO_THIRTEENTH_PIT_INDEX = PLAYER_TWO_THIRTEENTH_PIT - 1;

    @Mock
    private GameBuilder builder;

    @Mock
    private GameDataValidator validator;

    @Mock
    private GameRepository gameRepository;

    @InjectMocks
    private GameService gameService;

    @Test
    public void createGame_shouldCreateAGameSuccessfully() {
        // given
        Game game = buildGame();
        GameResponse gameResponse = buildGameResponse();
        given(builder.buildGame()).willReturn(game);
        given(gameRepository.save(game)).willReturn(game);
        given(builder.buildGameResponse(game)).willReturn(gameResponse);

        // when
        GameResponse result = gameService.createGame();

        // then
        assertThat(result, equalTo(gameResponse));
        inOrder(builder, gameRepository);
        verify(builder).buildGame();
        verify(gameRepository).save(game);
        verify(builder).buildGameResponse(game);
        verifyNoMoreInteractions(builder, gameRepository);
    }

    @Test
    public void searchGame_withValidGameId_shouldRetrieveTheGameSuccessfully() throws GameApiException {
        // given
        Game game = buildGame();
        GameResponse gameResponse = buildGameResponse();
        given(gameRepository.findById(GAME_ID)).willReturn(Optional.of(game));
        given(builder.buildGameResponse(game)).willReturn(gameResponse);

        // when
        GameResponse result = gameService.searchGame(GAME_ID);

        // then
        assertThat(result, equalTo(gameResponse));
        inOrder(builder, gameRepository);
        verify(gameRepository).findById(GAME_ID);
        verify(builder).buildGameResponse(game);
        verifyNoMoreInteractions(builder, gameRepository);
    }

    @Test (expected = GameApiException.class)
    public void searchGame_withInvalidGameId_shouldThrowGameApiException() throws GameApiException {
        // given
        given(gameRepository.findById(GAME_ID)).willReturn(Optional.empty());

        // when
        gameService.searchGame(GAME_ID);

        // then throw GameApiException
        inOrder(builder, gameRepository);
        verify(gameRepository).findById(GAME_ID);
        verifyNoMoreInteractions(builder, gameRepository);
    }

    @Test
    public void removeGame_withValidGameId_shouldDeleteTheGameSuccessfully() throws GameApiException {
        // given
        Game game = buildGame();
        given(gameRepository.findById(GAME_ID)).willReturn(Optional.of(game));
        doNothing().when(gameRepository).delete(game);

        // when
        gameService.removeGame(GAME_ID);

        // then
        inOrder(builder, gameRepository);
        verify(gameRepository).findById(GAME_ID);
        verify(gameRepository).delete(game);
        verifyNoMoreInteractions(builder, gameRepository);
    }

    @Test
    public void removeGame_withGameOver_shouldDeleteTheGameSuccessfully() throws GameApiException {
        // given
        Game game = buildGame();
        game.setGameStatus(FINISHED);
        given(gameRepository.findById(GAME_ID)).willReturn(Optional.of(game));
        doNothing().when(gameRepository).delete(game);

        // when
        gameService.removeGame(GAME_ID);

        // then
        inOrder(builder, gameRepository);
        verify(gameRepository).findById(GAME_ID);
        verify(gameRepository).delete(game);
        verifyNoMoreInteractions(builder, gameRepository);
    }

    @Test (expected = GameApiException.class)
    public void removeGame_withInvalidGameId_shouldThrowGameApiException() throws GameApiException {
        // given
        given(gameRepository.findById(GAME_ID)).willReturn(Optional.empty());

        // when
        gameService.removeGame(GAME_ID);

        // then throw GameApiException
        inOrder(builder, gameRepository);
        verify(gameRepository).findById(GAME_ID);
        verifyNoMoreInteractions(builder, gameRepository);
    }

    @Test
    public void makeMovement_firstMove_playerOneSelectPitOne_shouldNotChangeNextPlayer() throws GameApiException {
        // given
        Game game = buildGame();
        GameResponse gameResponse = buildGameResponse();
        HashMap<Integer, Integer> pitStatusPlayerOneFirstMovePitOne = buildPitForPlayerOneFirstMovePitOne();
        gameResponse.setStatus(pitStatusPlayerOneFirstMovePitOne);

        given(gameRepository.findById(GAME_ID)).willReturn(Optional.of(game));
        doNothing().when(validator).validateMovement(game, PLAYER_ONE_FIRST_PIT_INDEX);
        given(gameRepository.save(game)).willReturn(game);
        given(builder.buildGameStatusResponse(game)).willReturn(gameResponse);

        // when
        GameResponse result = gameService.makeMovement(GAME_ID, PLAYER_ONE_FIRST_PIT);

        // then
        assertThat(result, equalTo(gameResponse));
        inOrder(builder, gameRepository, validator);
        verify(gameRepository).findById(GAME_ID);
        verify(validator).validateMovement(game, PLAYER_ONE_FIRST_PIT_INDEX);
        verify(gameRepository).save(game);
        verify(builder).buildGameStatusResponse(game);
        verifyNoMoreInteractions(builder, gameRepository, validator);
    }

    @Test
    public void makeMovement_secondMove_playerOneSelectPitSix_shouldChangeNextPlayer() throws GameApiException {
        // given
        Game game = buildGame();
        GameResponse gameResponse = buildGameResponse();
        HashMap<Integer, Integer> pitStatusPlayerOneSecondMovePitSix = buildPitForPlayerOneSecondMovePitSix();
        gameResponse.setStatus(pitStatusPlayerOneSecondMovePitSix);

        given(gameRepository.findById(GAME_ID)).willReturn(Optional.of(game));
        doNothing().when(validator).validateMovement(game, PLAYER_ONE_SIXTH_PIT_INDEX);
        given(gameRepository.save(game)).willReturn(game);
        given(builder.buildGameStatusResponse(game)).willReturn(gameResponse);

        // when
        GameResponse result = gameService.makeMovement(GAME_ID, PLAYER_ONE_SIXTH_PIT);

        // then
        assertThat(result, equalTo(gameResponse));
        inOrder(builder, gameRepository, validator);
        verify(gameRepository).findById(GAME_ID);
        verify(validator).validateMovement(game, PLAYER_ONE_SIXTH_PIT_INDEX);
        verify(gameRepository).save(game);
        verify(builder).buildGameStatusResponse(game);
        verifyNoMoreInteractions(builder, gameRepository, validator);
    }


    @Test
    public void makeMovement_playerTwoGameOver_shouldFinishTheGame() throws GameApiException {
        // given
        Game game = buildPlayerTwoGameOver();
        GameResponse gameResponse = buildGameResponse();
        HashMap<Integer, Integer> pitStatusGameOver = buildPitForPlayerTwoGameOver();
        gameResponse.setStatus(pitStatusGameOver);

        given(gameRepository.findById(GAME_ID)).willReturn(Optional.of(game));
        doNothing().when(validator).validateMovement(game, PLAYER_TWO_THIRTEENTH_PIT_INDEX);
        given(gameRepository.save(game)).willReturn(game);
        given(builder.buildGameStatusResponse(game)).willReturn(gameResponse);

        // when
        GameResponse result = gameService.makeMovement(GAME_ID, PLAYER_TWO_THIRTEENTH_PIT);

        // then
        assertThat(result, equalTo(gameResponse));
        inOrder(builder, gameRepository, validator);
        verify(gameRepository).findById(GAME_ID);
        verify(validator).validateMovement(game, PLAYER_TWO_THIRTEENTH_PIT_INDEX);
        verify(gameRepository).save(game);
        verify(builder).buildGameStatusResponse(game);
        verifyNoMoreInteractions(builder, gameRepository, validator);
    }

    @Test
    public void makeMovement_playerOneGameOver_shouldFinishTheGame() throws GameApiException {
        // given
        Game game = buildPlayerOneGameOver();
        GameResponse gameResponse = buildGameResponse();
        HashMap<Integer, Integer> pitStatusGameOver = buildPitForPlayerOneGameOver();
        gameResponse.setStatus(pitStatusGameOver);

        given(gameRepository.findById(GAME_ID)).willReturn(Optional.of(game));
        doNothing().when(validator).validateMovement(game, PLAYER_ONE_SIXTH_PIT_INDEX);
        given(gameRepository.save(game)).willReturn(game);
        given(builder.buildGameStatusResponse(game)).willReturn(gameResponse);

        // when
        GameResponse result = gameService.makeMovement(GAME_ID, PLAYER_ONE_SIXTH_PIT);

        // then
        assertThat(result, equalTo(gameResponse));
        inOrder(builder, gameRepository, validator);
        verify(gameRepository).findById(GAME_ID);
        verify(validator).validateMovement(game, PLAYER_ONE_SIXTH_PIT_INDEX);
        verify(gameRepository).save(game);
        verify(builder).buildGameStatusResponse(game);
        verifyNoMoreInteractions(builder, gameRepository, validator);
    }

    @Test
    public void makeMovement_playerOneSkipPlayerTwoKalah_shouldChangeNextPlayer() throws GameApiException {
        // given
        Game game = buildPlayerOneBoardToSkipPlayerTwoKalah();
        GameResponse gameResponse = buildGameResponse();
        HashMap<Integer, Integer> pitStatusGameOver = buildPitForPlayerOneToSkipPlayerTwoKalah();
        gameResponse.setStatus(pitStatusGameOver);

        given(gameRepository.findById(GAME_ID)).willReturn(Optional.of(game));
        doNothing().when(validator).validateMovement(game, PLAYER_ONE_SIXTH_PIT_INDEX);
        given(gameRepository.save(game)).willReturn(game);
        given(builder.buildGameStatusResponse(game)).willReturn(gameResponse);

        // when
        GameResponse result = gameService.makeMovement(GAME_ID, PLAYER_ONE_SIXTH_PIT);

        // then
        assertThat(result, equalTo(gameResponse));
        inOrder(builder, gameRepository, validator);
        verify(gameRepository).findById(GAME_ID);
        verify(validator).validateMovement(game, PLAYER_ONE_SIXTH_PIT_INDEX);
        verify(gameRepository).save(game);
        verify(builder).buildGameStatusResponse(game);
        verifyNoMoreInteractions(builder, gameRepository, validator);
    }

    @Test
    public void makeMovement_playerTwoSkipPlayerOneKalah_shouldChangeNextPlayer() throws GameApiException {
        // given
        Game game = buildPlayerTwoBoardToSkipPlayerOneKalah();
        GameResponse gameResponse = buildGameResponse();
        HashMap<Integer, Integer> pitStatusGameOver = buildPitForPlayerTwoToSkipPlayerOneKalah();
        gameResponse.setStatus(pitStatusGameOver);

        given(gameRepository.findById(GAME_ID)).willReturn(Optional.of(game));
        doNothing().when(validator).validateMovement(game, PLAYER_TWO_THIRTEENTH_PIT_INDEX);
        given(gameRepository.save(game)).willReturn(game);
        given(builder.buildGameStatusResponse(game)).willReturn(gameResponse);

        // when
        GameResponse result = gameService.makeMovement(GAME_ID, PLAYER_TWO_THIRTEENTH_PIT);

        // then
        assertThat(result, equalTo(gameResponse));
        inOrder(builder, gameRepository, validator);
        verify(gameRepository).findById(GAME_ID);
        verify(validator).validateMovement(game, PLAYER_TWO_THIRTEENTH_PIT_INDEX);
        verify(gameRepository).save(game);
        verify(builder).buildGameStatusResponse(game);
        verifyNoMoreInteractions(builder, gameRepository, validator);
    }

    private HashMap<Integer, Integer> buildPitForPlayerOneFirstMovePitOne() {
        HashMap<Integer, Integer> pits = new HashMap<>();

        pits.put(0, 0);
        pits.put(1, 7);
        pits.put(2, 7);
        pits.put(3, 7);
        pits.put(4, 7);
        pits.put(5, 7);
        pits.put(6, 1);
        pits.put(7, 6);
        pits.put(8, 6);
        pits.put(9, 6);
        pits.put(10, 6);
        pits.put(11, 6);
        pits.put(12, 6);
        pits.put(13, 0);

        return pits;
    }

    private HashMap<Integer, Integer> buildPitForPlayerOneSecondMovePitSix() {
        HashMap<Integer, Integer> pits = new HashMap<>();

        pits.put(0, 0);
        pits.put(1, 7);
        pits.put(2, 7);
        pits.put(3, 7);
        pits.put(4, 7);
        pits.put(5, 0);
        pits.put(6, 2);
        pits.put(7, 7);
        pits.put(8, 7);
        pits.put(9, 7);
        pits.put(10, 7);
        pits.put(11, 7);
        pits.put(12, 7);
        pits.put(13, 0);

        return pits;
    }

    private HashMap<Integer, Integer> buildPitForPlayerTwoGameOver() {
        HashMap<Integer, Integer> pits = new HashMap<>();

        pits.put(0, 1);
        pits.put(1, 6);
        pits.put(2, 0);
        pits.put(3, 6);
        pits.put(4, 4);
        pits.put(5, 6);
        pits.put(6, 25);
        pits.put(7, 0);
        pits.put(8, 0);
        pits.put(9, 0);
        pits.put(10, 0);
        pits.put(11, 0);
        pits.put(12, 0);
        pits.put(13, 24);

        return pits;
    }

    private HashMap<Integer, Integer> buildPitForPlayerOneGameOver() {
        HashMap<Integer, Integer> pits = new HashMap<>();

        pits.put(0, 0);
        pits.put(1, 0);
        pits.put(2, 0);
        pits.put(3, 0);
        pits.put(4, 0);
        pits.put(5, 0);
        pits.put(6, 26);
        pits.put(7, 0);
        pits.put(8, 0);
        pits.put(9, 0);
        pits.put(10, 0);
        pits.put(11, 0);
        pits.put(12, 1);
        pits.put(13, 23);

        return pits;
    }

    private HashMap<Integer, Integer> buildPitForPlayerOneToSkipPlayerTwoKalah() {
        HashMap<Integer, Integer> pits = new HashMap<>();

        pits.put(0, 1);
        pits.put(1, 1);
        pits.put(2, 1);
        pits.put(3, 0);
        pits.put(4, 0);
        pits.put(5, 0);
        pits.put(6, 26);
        pits.put(7, 1);
        pits.put(8, 1);
        pits.put(9, 1);
        pits.put(10, 1);
        pits.put(11, 1);
        pits.put(12, 2);
        pits.put(13, 23);

        return pits;
    }

    private HashMap<Integer, Integer> buildPitForPlayerTwoToSkipPlayerOneKalah() {
        HashMap<Integer, Integer> pits = new HashMap<>();

        pits.put(0, 1);
        pits.put(1, 1);
        pits.put(2, 1);
        pits.put(3, 1);
        pits.put(4, 1);
        pits.put(5, 11);
        pits.put(6, 25);
        pits.put(7, 1);
        pits.put(8, 1);
        pits.put(9, 1);
        pits.put(10, 0);
        pits.put(11, 0);
        pits.put(12, 0);
        pits.put(13, 24);

        return pits;
    }

    private Game buildGame() {
        return Game.builder()
                .gameId(GAME_ID)
                .nextPlayer(PLAYER_ONE)
                .gameStatus(RUNNING)
                .startedTime(now().toEpochMilli())
                .board(buildBoard())
                .build();
    }

    private Game buildPlayerTwoGameOver() {
        return Game.builder()
                .gameId(GAME_ID)
                .nextPlayer(PLAYER_TWO)
                .gameStatus(RUNNING)
                .startedTime(now().toEpochMilli())
                .board(buildPlayerTwoFinishingBoard())
                .build();
    }

    private Game buildPlayerOneGameOver() {
        return Game.builder()
                .gameId(GAME_ID)
                .nextPlayer(PLAYER_ONE)
                .gameStatus(RUNNING)
                .startedTime(now().toEpochMilli())
                .board(buildPlayerOneFinishingBoard())
                .build();
    }

    private Game buildPlayerOneBoardToSkipPlayerTwoKalah() {
        return Game.builder()
                .gameId(GAME_ID)
                .nextPlayer(PLAYER_ONE)
                .gameStatus(RUNNING)
                .startedTime(now().toEpochMilli())
                .board(buildPlayerOneBoardSkipPlayerTwoKalah())
                .build();
    }

    private Board buildPlayerOneBoardSkipPlayerTwoKalah() {
        return Board.builder()
                .pits(initializePitsForPlayerOneSkipPlayerTwoKalah())
                .build();
    }

    private int[] initializePitsForPlayerOneSkipPlayerTwoKalah() {
        return new int[] {
                0, 0, 0, 0, 0, 10, 25,
                0, 0, 0, 0, 0, 1, 23
        };
    }

    private Game buildPlayerTwoBoardToSkipPlayerOneKalah() {
        return Game.builder()
                .gameId(GAME_ID)
                .nextPlayer(PLAYER_TWO)
                .gameStatus(RUNNING)
                .startedTime(now().toEpochMilli())
                .board(buildPlayerTwoBoardSkipPlayerOneKalah())
                .build();
    }

    private Board buildPlayerTwoBoardSkipPlayerOneKalah() {
        return Board.builder()
                .pits(initializePitsForPlayerTwoSkipPlayerOneKalah())
                .build();
    }

    private int[] initializePitsForPlayerTwoSkipPlayerOneKalah() {
        return new int[] {
                0, 0, 0, 0, 0, 10, 25,
                0, 0, 0, 0, 0, 10, 23
        };
    }

    private Board buildPlayerOneFinishingBoard() {
        return Board.builder()
                .pits(initializePitsForPlayerOneGameOver())
                .build();
    }

    private int[] initializePitsForPlayerOneGameOver() {
        return new int[] {
                0, 0, 0, 0, 0, 1, 25,
                0, 0, 0, 0, 0, 1, 23
        };
    }

    private Board buildPlayerTwoFinishingBoard() {
        return Board.builder()
                .pits(initializePitsForPlayerTwoGameOver())
                .build();
    }

    private int[] initializePitsForPlayerTwoGameOver() {
        return new int[] {
                1, 6, 0, 6, 4, 6, 25,
                0, 0, 0, 0, 0, 1, 23
        };
    }

    private Board buildBoard() {
        return Board.builder()
                .pits(initializePits()).build();
    }

    private int[] initializePits() {
        return new int[] {
                6, 6, 6, 6, 6, 6, 0,
                6, 6, 6, 6, 6, 6, 0
        };
    }

    private GameResponse buildGameResponse() {
        return GameResponse.builder()
                .id(GAME_ID)
                .url(GAME_URL)
                .status(emptyMap())
                .build();
    }
}
