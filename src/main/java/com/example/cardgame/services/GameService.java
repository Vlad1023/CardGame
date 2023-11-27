package com.example.cardgame.services;

import com.example.cardgame.models.Game;
import com.example.cardgame.repositories.GameRepository;
import com.example.cardgame.repositories.UserRepository;
import com.example.cardgame.validators.GameIdConstraint;
import com.example.cardgame.validators.UserIdConstraint;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PutMapping;

@Service
@Transactional
public class GameService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    GameRepository gameRepository;
    @Autowired
    SimpMessagingTemplate messagingTemplate;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    ObjectMapper mapper;

    public Game JoinGame(@UserIdConstraint String userId, @GameIdConstraint String gameId) throws JsonProcessingException {
        Game gameToJoin = this.joinGameUtil(userId, gameId);
        if(gameToJoin.getCurrentPlayers().size() == 2)
            gameToJoin = this.StartGame(gameId);
        return gameToJoin;
    }

    public Game StartGame(@GameIdConstraint String gameId) throws JsonProcessingException {
        var game = gameRepository.findById(gameId).get();
        game.setIsGameStarted(true);
        gameRepository.save(game);
        var message = MessageBuilder.withPayload("").build();
        messagingTemplate.convertAndSend("/game/startGame/" + game.getId(), mapper.writeValueAsString(game));
        return game;
    }

    private Game joinGameUtil(String userId, String gameId){
        var userToJoin = userRepository.findById(userId).get();
        var gameToJoin = gameRepository.findById(gameId).get();
        gameToJoin.AddPlayer(userToJoin);
        gameRepository.save(gameToJoin);
        return gameToJoin;
    }
}
