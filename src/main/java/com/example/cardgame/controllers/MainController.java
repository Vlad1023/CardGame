package com.example.cardgame.controllers;

import com.example.cardgame.models.User;
import com.example.cardgame.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.ModelAndView;

import java.io.Console;

@Controller
public class MainController {
    @GetMapping(value = "/")
    public String Index(Model model){

        return "login";
    }
    @GetMapping(value = "/main")
    public String MainPage(Model model){

        return "main";
    }
    @PostMapping(value = "/addUser")
    public String AddUser(@RequestBody String userName){
        //ModelAndView modelAndView = new ModelAndView("main");
        return "redirect:/main";
    }
}