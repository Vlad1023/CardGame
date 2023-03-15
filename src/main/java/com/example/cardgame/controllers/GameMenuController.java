package com.example.cardgame.controllers;

import com.example.cardgame.DTO.NewGameDTO;
import com.example.cardgame.models.Game;
import com.example.cardgame.repositories.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class GameMenuController {
    @Autowired
    GameRepository gameRepository;

    @PostMapping(value = "/addNewGame")
    public String AddNewGame(@RequestBody NewGameDTO DTO){
        Game newGame = new Game(DTO.getGameName());
        newGame.AddPlayer(DTO.getUserNameHost());
        gameRepository.save(newGame);
        return "main";
    }
}
