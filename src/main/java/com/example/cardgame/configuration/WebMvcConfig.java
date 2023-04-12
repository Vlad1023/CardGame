package com.example.cardgame.configuration;

import com.example.cardgame.DTO.GetGameDTO;
import com.example.cardgame.models.Game;
import com.example.cardgame.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Autowired
    UserRepository userRepository;
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

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SignInInterceptor(userRepository));
    }
}
