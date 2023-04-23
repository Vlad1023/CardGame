package com.example.cardgame.repositories;

import com.example.cardgame.models.Game;
import com.example.cardgame.models.User;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameRepository extends CrudRepository<Game, String> {
    List<Game> findByCurrentPlayersContains(User player);
}
