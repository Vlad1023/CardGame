package com.example.cardgame.controllers;

import com.example.cardgame.DTO.NewGameDTO;
import com.example.cardgame.models.Game;
import com.example.cardgame.repositories.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Controller
public class GameMenuController {
    @Autowired
    GameRepository gameRepository;

    @PostMapping(value = "/addNewGame")
    public String AddNewGame(@RequestBody NewGameDTO DTO){
        Game newGame = new Game(DTO.getGameName());
        newGame.AddPlayer(DTO.getUserNameHost());
        gameRepository.save(newGame);
        addGameToList();
        return "main";
    }

    @MessageMapping("/gamesInfo")
    @SendTo("/games/gamesList")
    public List<Game> addGameToList(){
        List<Game> games = (List<Game>) gameRepository.findAll();
        return games;
    }
}
