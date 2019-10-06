package com.edilson.justiniano.kalah.api.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
public class GameController {

    private static final Logger LOG = LoggerFactory.getLogger(GameController.class);

    private static final String GAME_URL = "/games";

    @PostMapping(GAME_URL)
    public ResponseEntity createGame() {
        LOG.info("Creating a new game.");

        return ResponseEntity
                .status(CREATED)
                .body(null);
    }
}
