package com.example.cardgame.controllers;

import com.example.cardgame.DTO.GetUserDTO;
import com.example.cardgame.configuration.RequiresSignIn;
import com.example.cardgame.models.User;
import com.example.cardgame.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class MainController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping(value = "/")
    public String index(Model model) {
        return "login";
    }

    @RequiresSignIn
    @GetMapping(value = "/main")
    public String mainPage(@RequestParam(name = "userId") String userId, Model model) {
        model.addAttribute("userId", userId);
        return "main";
    }

    @PostMapping(value = "/addUser")
    public String addUser(@RequestParam String name, HttpSession session, RedirectAttributes redirectAttributes) {
        User newUser = new User(name);
        String previousId = (String) session.getAttribute("userId");
        if (previousId != null && userRepository.existsById(previousId)) {
            userRepository.deleteById(previousId);
        }
        userRepository.save(newUser);
        session.setAttribute("userId", newUser.getId());
        redirectAttributes.addFlashAttribute("userId", newUser.getId());
        return "redirect:/main?userId=" + newUser.getId();
    }

    @GetMapping(value = "/getUser")
    public ResponseEntity<GetUserDTO> getCurrentUser(HttpSession session) {
        var userId = session.getAttribute("userId");
        var foundUser = userRepository.findById(userId.toString()).orElse(null);
        if (foundUser != null) {
            return new ResponseEntity<>(modelMapper.map(foundUser, GetUserDTO.class), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
