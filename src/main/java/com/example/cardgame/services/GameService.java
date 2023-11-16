package com.example.cardgame.services;

import com.example.cardgame.repositories.GameRepository;
import com.example.cardgame.repositories.UserRepository;
import com.example.cardgame.validators.GameIdConstraint;
import com.example.cardgame.validators.UserIdConstraint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PutMapping;

@Service
public class GameService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    GameRepository gameRepository;
    public void JoinGame(@UserIdConstraint String userId, @GameIdConstraint String gameId){
        var userToJoin = userRepository.findById(userId).get();
        var gameToJoin = gameRepository.findById(gameId).get();
        gameToJoin.AddPlayer(userToJoin);
        gameRepository.save(gameToJoin);
    }
}
