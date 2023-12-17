package com.example.cardgame.configuration;

import com.example.cardgame.DTO.GetCardDTO;
import com.example.cardgame.DTO.GetGameDTO;
import com.example.cardgame.DTO.GetUserDTO;
import com.example.cardgame.models.Card;
import com.example.cardgame.models.Game;
import com.example.cardgame.models.User;
import com.example.cardgame.repositories.UserRepository;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Autowired
    UserRepository userRepository;

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        // Configuration for Game to GetGameDTO mapping
        modelMapper.createTypeMap(Game.class, GetGameDTO.class)
                .addMappings(mapper -> {
                    mapper.map(Game::getId, GetGameDTO::setGameId);
                    mapper.map(Game::getName, GetGameDTO::setGameName);
                    mapper.map(Game::getIsGameStarted, GetGameDTO::setIsGameStarted);
                    mapper.using(ctx -> {
                        Map<String, String> players = (Map<String, String>) ctx.getSource();
                        return players.size();
                    }).map(Game::getCurrentPlayers, GetGameDTO::setUsersCount);
                });

        // Configuration for Card to GetCardDTO mapping
        modelMapper.createTypeMap(Card.class, GetCardDTO.class)
                .addMappings(mapper -> {
                    mapper.map(Card::getId, GetCardDTO::setId);
                    mapper.map(Card::getRepresentation, GetCardDTO::setRepresentation);
                    mapper.map(Card::getRank, GetCardDTO::setRank);
                });

        // Configuration for User to GetUserDTO mapping
        modelMapper.createTypeMap(User.class, GetUserDTO.class)
                .addMappings(mapper -> {
                    mapper.map(User::getId, GetUserDTO::setUserId);
                    mapper.map(User::getName, GetUserDTO::setName);
                    mapper.using(getUserCardsDtoConverter()).map(User::getCurrentCards, GetUserDTO::setCurrentCards);
                });

        return modelMapper;
    }

    private Converter<List<Card>, List<GetCardDTO>> getUserCardsDtoConverter() {
        return ctx -> {
            Type type = new TypeToken<List<GetCardDTO>>() {}.getType();
            return modelMapper().map(ctx.getSource(), type);
        };
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SignInInterceptor(userRepository));
    }
}
