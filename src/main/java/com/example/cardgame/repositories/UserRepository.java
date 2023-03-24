package com.example.cardgame.repositories;

import com.example.cardgame.models.Game;
import com.example.cardgame.models.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, String> {
}
