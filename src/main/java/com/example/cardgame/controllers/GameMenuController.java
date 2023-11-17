package com.example.cardgame.controllers;

import com.example.cardgame.DTO.GetGameDTO;
import com.example.cardgame.DTO.NewGameDTO;
import com.example.cardgame.configuration.RequiresSignIn;
import com.example.cardgame.models.Game;
import com.example.cardgame.models.User;
import com.example.cardgame.repositories.GameRepository;
import com.example.cardgame.repositories.UserRepository;
import com.example.cardgame.services.GameService;
import com.example.cardgame.validators.GameIdConstraint;
import com.example.cardgame.validators.UserIdConstraint;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Controller
@RestController
public class GameMenuController {
    @Autowired
    GameRepository gameRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    SimpMessagingTemplate messagingTemplate;
    @Autowired
    ObjectMapper mapper;

    @Autowired
    private GameService gameService;
    @Autowired
    private ModelMapper modelMapper;

    @MessageMapping("/addGame")
    @SendTo("/gamesInfo/gamesList")
    @RequiresSignIn
    public String addGameToList(NewGameDTO DTO) throws JsonProcessingException {
        Game newGame = new Game(DTO.getGameName());
        gameRepository.save(newGame);
        List<Game> games = (List<Game>) gameRepository.findAll();
        List<GetGameDTO> getGamesDTO = new ArrayList<GetGameDTO>();
        for (Game game : games) {
            getGamesDTO.add(modelMapper.map(game, GetGameDTO.class));
        }
        gameService.JoinGame(DTO.getUserId(), newGame.getId());
        return mapper.writeValueAsString(getGamesDTO);
    }


    @GetMapping(value = "/activePendingGames")
    @ResponseBody
    @RequiresSignIn
    public ResponseEntity<List<GetGameDTO>> GetAllActivePendingGames(){
        List<Game> games = (List<Game>) gameRepository.findAll();
        List<GetGameDTO> getGamesDTO = new ArrayList<GetGameDTO>();
        for (Game game : games) {
            getGamesDTO.add(modelMapper.map(game, GetGameDTO.class));
        }
        return new ResponseEntity<List<GetGameDTO>>(getGamesDTO, HttpStatus.OK);
    }

    @RequiresSignIn
    @GetMapping(value = "/getGameWhereUserParticipate")
    public ResponseEntity<GetGameDTO> GetGameWhereUserParticipate(@ModelAttribute("userId") @UserIdConstraint String userId, Model model){
        User user = userRepository.findById(userId).get();
        List<Game> allGames = (List<Game>) gameRepository.findAll();
        var foundGames = allGames.stream().filter(game -> game.getCurrentPlayers().containsKey(user.getId())).toList();
        if(foundGames.size() == 0){
            return new ResponseEntity<GetGameDTO>(HttpStatus.NOT_FOUND);
        }
        else if(foundGames.size() > 1){
            return new ResponseEntity<GetGameDTO>(HttpStatus.CONFLICT);
        }
        else{
            return new ResponseEntity<GetGameDTO>(modelMapper.map(foundGames.get(0), GetGameDTO.class), HttpStatus.OK);
        }
    }

    @RequiresSignIn
    @PatchMapping(value = "/joinGame/{gameId}/{userId}")
    public String joinToExistingGame(
            @PathVariable("userId") @UserIdConstraint String userId,
            @PathVariable("gameId") @GameIdConstraint String gameId) {

        gameService.JoinGame(userId, gameId);
        gameService.StartGame(gameId);

        return "redirect:/game/" + gameId;
    }


}
