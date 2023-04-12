package com.example.cardgame.configuration;

import com.example.cardgame.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class SignInInterceptor implements HandlerInterceptor {
    UserRepository userRepository;
    @Autowired
    public SignInInterceptor(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            RequiresSignIn annotation = handlerMethod.getMethodAnnotation(RequiresSignIn.class);
            if (annotation != null) {
                HttpSession session = request.getSession(false);
                String currentUserid = session != null ? (String) session.getAttribute("userId") : null;
                if (currentUserid == null || !userRepository.existsById(currentUserid)){
                    response.sendRedirect("/");
                    return false;
                }
            }
        }
        return true;
    }
}
