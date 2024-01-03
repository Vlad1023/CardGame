package com.example.cardgame.controllers;

import com.example.cardgame.DTO.GetCardDTO;
import com.example.cardgame.DTO.GetGameDTO;
import com.example.cardgame.DTO.GetUserOpponentDTO;
import com.example.cardgame.GameStatusAfterMove;
import com.example.cardgame.configuration.RequiresSignIn;
import com.example.cardgame.models.Card;
import com.example.cardgame.models.Game;
import com.example.cardgame.repositories.GameRepository;
import com.example.cardgame.repositories.UserRepository;
import com.example.cardgame.services.GameProcessService;
import com.example.cardgame.services.UserService;
import com.example.cardgame.validators.GameIdConstraint;
import com.example.cardgame.validators.UserIdConstraint;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
public class GameProcessController {

    @Autowired
    GameRepository gameRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserService userService;
    @Autowired
    ObjectMapper mapper;
    @Autowired
    SimpMessagingTemplate messagingTemplate;

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
    @GetMapping(value = "/game/getUserOpponentInfo/{gameId}")
    public ResponseEntity<GetUserOpponentDTO> GetUserOpponentInfo(@PathVariable("gameId") @GameIdConstraint String gameId, HttpSession httpSession) {
        var userId = httpSession.getAttribute("userId").toString();
        var user = userRepository.findById(userId).get();
        var game = gameRepository.findById(gameId).get();
        var opponentUserId = userService.GetUserOpponentId(game, user);
        var foundUser = userRepository.findById(opponentUserId).get();
        List<Card> opponentCards = foundUser.getCurrentCards();
        List<GetCardDTO> opponentCardsDTO
                = modelMapper.map(opponentCards, new TypeToken<List<Card>>() {}.getType());
        return ResponseEntity.ok(new GetUserOpponentDTO(foundUser.getName(), opponentCardsDTO));
    }

    @RequiresSignIn
    @PostMapping(value = "/game/makeUserMove/{gameId}")
    @ResponseBody
    public ResponseEntity<GameStatusAfterMove> MakeUsersMove(@PathVariable("gameId") @GameIdConstraint String gameId, HttpSession httpSession) throws JsonProcessingException {
        var userId = httpSession.getAttribute("userId").toString();
        var status = gameProcessService.MakeUsersMove(userId, gameId);
        var user = userRepository.findById(userId).get();
        var game = gameRepository.findById(gameId).get();
        var opponentUserId = userService.GetUserOpponentId(game, user);
        this.notifyUserThatOpponentMadeAMove(opponentUserId, status);
        return ResponseEntity.ok(status);
    }

    private void notifyUserThatOpponentMadeAMove(String opponentUserId, GameStatusAfterMove gameStatusAfterMove) throws JsonProcessingException {
        messagingTemplate.convertAndSend("/game/opponentMadeMove/" + opponentUserId, gameStatusAfterMove);
    }
}
