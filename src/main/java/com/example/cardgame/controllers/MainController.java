package com.example.cardgame.controllers;

import com.example.cardgame.models.User;
import com.example.cardgame.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.Console;

@Controller
public class MainController {
    @Autowired
    UserRepository userRepository;
    @GetMapping(value = "/")
    public String Index(Model model){

        return "index";
    }
    @PostMapping(value = "/addUser")
    public String AddUser(String userName){
        userRepository.save(new User(userName));
        return "index";
    }
}