package com.example.cardgame.controllers;

import com.example.cardgame.configuration.RequiresSignIn;
import com.example.cardgame.repositories.GameRepository;
import com.example.cardgame.repositories.UserRepository;
import com.example.cardgame.validators.GameIdConstraint;
import com.example.cardgame.validators.UserIdConstraint;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class GameProcessController {

    @Autowired
    GameRepository gameRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ObjectMapper mapper;

    @Autowired
    private ModelMapper modelMapper;

    @RequiresSignIn
    @GetMapping(value = "/gameLobby/{gameId}")
    public String OpenGameLobby(@PathVariable("gameId") @GameIdConstraint String gameId, HttpSession httpSession) {
        var game = gameRepository.findById(gameId).get();
        var userId = httpSession.getAttribute("userId");
        httpSession.setAttribute("gameId", gameId);
        return game.getCurrentPlayers().containsKey(userId) ? "gameLobby" : "redirect:/main";
    }

    @RequiresSignIn
    @GetMapping(value = "/game/{gameId}")
    public String OpenGame(@PathVariable("gameId") @GameIdConstraint String gameId, HttpSession httpSession) {
        var game = gameRepository.findById(gameId).get();
        var userId = httpSession.getAttribute("userId");
        var isGameStarted = game.getIsGameStarted();
        return (game.getCurrentPlayers().containsKey(userId) && isGameStarted) ? "game" : "redirect:/main";
    }
}
