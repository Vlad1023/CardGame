package com.example.cardgame.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.Console;

@Controller
public class MainController {
    @GetMapping(value = "/index")
    public String index(Model model){
        System.out.println("Hello World!");
        return "index";
    }
}