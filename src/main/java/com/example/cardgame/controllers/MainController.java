package com.example.cardgame.controllers;


import com.example.cardgame.repositories.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.Console;

@Controller
public class MainController {
    @GetMapping(value = "/")
    public String Index(Model model){

        return "login";
    }
    @GetMapping(value = "/main")
    public String MainPage(@ModelAttribute("userName") String userName, Model model){
        model.addAttribute("userName", userName);
        return "main";
    }
    @PostMapping(value = "/addUser")
    public String AddUser(@RequestBody String userName, RedirectAttributes redirectAttributes){
        redirectAttributes.addFlashAttribute("userName", userName);
        return "redirect:/main";
    }
}