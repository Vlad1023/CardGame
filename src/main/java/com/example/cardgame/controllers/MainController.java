package com.example.cardgame.controllers;


import com.example.cardgame.configuration.RequiresSignIn;
import com.example.cardgame.models.User;
import com.example.cardgame.repositories.GameRepository;
import com.example.cardgame.repositories.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.Console;
import java.util.UUID;

@Controller
public class MainController {
    @Autowired
    UserRepository userRepository;
    @GetMapping(value = "/")
    public String Index(Model model){

        return "login";
    }
    @RequiresSignIn
    @GetMapping(value = "/main")
    public String MainPage(@ModelAttribute("userId") String userId, Model model){
        model.addAttribute("userId", userId);
        return "main";
    }
    @PostMapping(value = "/addUser")
    public String AddUser(@RequestParam String name, HttpSession session, RedirectAttributes redirectAttributes){
        User newUser = new User(name);
        String previousId = (String) session.getAttribute("userId");
        if(previousId != null && userRepository.existsById(previousId))
        {
            userRepository.deleteById(previousId);
        }
        userRepository.save(newUser);
        session.setAttribute("userId", newUser.getId());
        redirectAttributes.addFlashAttribute("userId", newUser.getId());
        return "redirect:/main";
    }
}