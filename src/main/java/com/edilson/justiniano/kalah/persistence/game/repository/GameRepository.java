package com.edilson.justiniano.kalah.persistence.game.repository;

import com.edilson.justiniano.kalah.persistence.game.model.Game;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GameRepository extends MongoRepository<Game, String> {
}
