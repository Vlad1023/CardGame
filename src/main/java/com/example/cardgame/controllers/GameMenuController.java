package com.example.cardgame.controllers;

import com.example.cardgame.DTO.GetGameDTO;
import com.example.cardgame.DTO.NewGameDTO;
import com.example.cardgame.models.Game;
import com.example.cardgame.repositories.GameRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;

@Controller
public class GameMenuController {
    @Autowired
    GameRepository gameRepository;
    @Autowired
    SimpMessagingTemplate messagingTemplate;
    @Autowired
    ObjectMapper mapper;

    @MessageMapping("/addGame")
    @SendTo("/gamesInfo/gamesList")
    public String addGameToList(NewGameDTO DTO) throws JsonProcessingException {
        Game newGame = new Game(DTO.getGameName());
        newGame.AddPlayer(DTO.getUserNameHost());
        gameRepository.save(newGame);
        List<Game> games = (List<Game>) gameRepository.findAll();
        List<GetGameDTO> getGamesDTO = new ArrayList<GetGameDTO>();
        for (Game game : games) {
            GetGameDTO gameDTO = new GetGameDTO();
            gameDTO.setGameId(game.getId());
            gameDTO.setGameName(game.getName());
            gameDTO.setUsersCount((int) game.getCurrentPlayers().stream().count());
            getGamesDTO.add(gameDTO);
        }
        return mapper.writeValueAsString(getGamesDTO);
    }
}
