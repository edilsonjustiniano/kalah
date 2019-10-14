package com.edilson.justiniano.kalah.integration.api;

import com.edilson.justiniano.kalah.api.model.GameResponse;
import com.edilson.justiniano.kalah.api.service.GameBuilder;
import com.edilson.justiniano.kalah.persistence.game.model.Board;
import com.edilson.justiniano.kalah.persistence.game.model.Game;
import com.edilson.justiniano.kalah.persistence.game.repository.GameRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.edilson.justiniano.kalah.persistence.game.model.GameStatus.RUNNING;
import static com.edilson.justiniano.kalah.persistence.game.model.Player.PLAYER_ONE;
import static java.time.Instant.now;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@EnableAutoConfiguration(exclude={MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})
public class GameControllerIt {

    private static final String GAME_ID = "gameId";
    private static final String GAME_URL = "gameUrl";
    private static final int PIT_ID = 1;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GameBuilder gameBuilder;

    @MockBean
    private GameRepository gameRepository;

    @Test
    public void createGame_shouldReturnCreatedGame() throws Exception {
        // given
        Game game = buildGame();
        given(gameBuilder.buildGame()).willReturn(game);
        given(gameRepository.save(any(Game.class))).willReturn(game);
        given(gameBuilder.buildGameResponse(game)).willReturn(buildGameResponse());

        // when
        mockMvc.perform(post("/games")
                                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(GAME_ID)));

        // then
        verify(gameRepository).save(any(Game.class));
    }

    @Test
    public void getGame_withValidId_shouldReturnTheGame() throws Exception {
        // given
        Game game = buildGame();
        given(gameRepository.findById(GAME_ID)).willReturn(Optional.of(game));
        given(gameBuilder.buildGameResponse(game)).willReturn(buildGameResponse());

        // when
        mockMvc.perform(get("/games/" + GAME_ID)
                                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(GAME_ID)));

        // then
        verify(gameRepository).findById(GAME_ID);
    }

    @Test
    public void getGame_withInvalidId_shouldReturnError() throws Exception {
        // given
        given(gameRepository.findById(GAME_ID)).willReturn(Optional.empty());

        // when
        mockMvc.perform(get("/games/" + GAME_ID)
                                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        // then
        verify(gameRepository).findById(GAME_ID);
    }


    @Test
    public void deleteGame_withValidId_shouldReturnNoContent() throws Exception {
        // given
        Game game = buildGame();
        given(gameRepository.findById(GAME_ID)).willReturn(Optional.of(game));

        // when
        mockMvc.perform(delete("/games/" + GAME_ID)
                                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        // then
        verify(gameRepository).findById(GAME_ID);
    }

    @Test
    public void deleteGame_withInvalidId_shouldReturnError() throws Exception {
        // given
        given(gameRepository.findById(GAME_ID)).willReturn(Optional.empty());

        // when
        mockMvc.perform(delete("/games/" + GAME_ID)
                                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        // then
        verify(gameRepository).findById(GAME_ID);
    }


    @Test
    public void moveGame_withValidId_shouldReturnOk() throws Exception {
        // given
        Game game = buildGame();
        given(gameRepository.findById(GAME_ID)).willReturn(Optional.of(game));
        given(gameBuilder.buildGameStatusResponse(game)).willReturn(buildGameStatusResponse());

        // when
        mockMvc.perform(put("/games/" + GAME_ID + "/pits/" + PIT_ID)
                                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // then
        verify(gameRepository).findById(GAME_ID);
        verify(gameRepository).save(any(Game.class));
    }

    @Test
    public void moveGame_withInvalidId_shouldReturnError() throws Exception {
        // given
        given(gameRepository.findById(GAME_ID)).willReturn(Optional.empty());

        // when
        mockMvc.perform(put("/games/" + GAME_ID + "/pits/" + PIT_ID)
                                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        // then
        verify(gameRepository).findById(GAME_ID);
    }

    private GameResponse buildGameResponse() {
        return GameResponse.builder()
                .id(GAME_ID)
                .url(GAME_URL)
                .build();
    }

    private GameResponse buildGameStatusResponse() {
        return GameResponse.builder()
                .id(GAME_ID)
                .url(GAME_URL)
                .status(buildGameStatus())
                .build();
    }

    private Map<Integer, Integer> buildGameStatus() {
        HashMap<Integer, Integer> status = new HashMap<>();
        status.put(0, 6);

        return status;
    }

    private Game buildGame() {
        return Game.builder()
                .nextPlayer(PLAYER_ONE)
                .gameStatus(RUNNING)
                .gameId(GAME_ID)
                .startedTime(now().toEpochMilli())
                .board(buildBoard())
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
}
