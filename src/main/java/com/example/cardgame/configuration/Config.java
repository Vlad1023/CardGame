package com.example.cardgame.configuration;


import com.example.cardgame.DTO.GetGameDTO;
import com.example.cardgame.models.Game;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.List;

@Configuration
@EnableWebSocketMessageBroker
public class Config implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/gamesInfo");
        config.setApplicationDestinationPrefixes("/games");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/cardGame-websocket").withSockJS();
    }

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.createTypeMap(Game.class, GetGameDTO.class)
                .addMappings(mapper -> {
                    mapper.map(Game::getId, GetGameDTO::setGameId);
                    mapper.map(Game::getName, GetGameDTO::setGameName);
                    mapper.using(ctx -> {
                        List<String> players = (List<String>) ctx.getSource();
                        return players.size();
                    }).map(Game::getCurrentPlayers, GetGameDTO::setUsersCount);
                });
        return modelMapper;
    }
}
