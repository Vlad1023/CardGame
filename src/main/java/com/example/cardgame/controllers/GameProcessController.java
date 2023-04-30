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
    @GetMapping(value = "/game/{gameId}")
    public String ProceedUserToGame(@PathVariable("gameId") @GameIdConstraint String gameId, HttpSession httpSession, Model model) {
        var game = gameRepository.findById(gameId).get();
        var userId = httpSession.getAttribute("userId");
        return game.getCurrentPlayers().containsKey(userId) ? "game" : "redirect:/main";
    }
}
