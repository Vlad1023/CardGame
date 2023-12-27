package com.example.cardgame.controllers;

import com.example.cardgame.DTO.GetCardDTO;
import com.example.cardgame.GameStatusAfterMove;
import com.example.cardgame.configuration.RequiresSignIn;
import com.example.cardgame.models.Card;
import com.example.cardgame.repositories.GameRepository;
import com.example.cardgame.repositories.UserRepository;
import com.example.cardgame.services.GameProcessService;
import com.example.cardgame.validators.GameIdConstraint;
import com.example.cardgame.validators.UserIdConstraint;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @Autowired
    private GameProcessService gameProcessService;

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

    @RequiresSignIn
    @GetMapping(value = "/game/getUserOpponentCards/{gameId}")
    public ResponseEntity<List<GetCardDTO>> GetUserOpponentCards(@PathVariable("gameId") @GameIdConstraint String gameId, HttpSession httpSession) {
        var userId = httpSession.getAttribute("userId").toString();
        List<Card> opponentCards = gameProcessService.GetUserOpponnentCards(userId, gameId);
        List<GetCardDTO> opponentCardsDTO
                = modelMapper.map(opponentCards, new TypeToken<List<Card>>() {}.getType());
        return ResponseEntity.ok(opponentCardsDTO);
    }

    @RequiresSignIn
    @MessageMapping("/game/{gameId}/{userId}")
    @SendTo("/game/{gameId}/{userId}")
    public GameStatusAfterMove MakeUsersMove(@DestinationVariable @GameIdConstraint String gameId, @DestinationVariable @UserIdConstraint String userId) {
        return gameProcessService.MakeUsersMove(userId, gameId);
    }
}
