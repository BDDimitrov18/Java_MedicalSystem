package com.medicalrecords.controller;

import com.medicalrecords.config.CurrentUserHelper;
import com.medicalrecords.model.Role;
import com.medicalrecords.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final CurrentUserHelper currentUserHelper;

    @GetMapping("/")
    public String home(Model model) {
        User currentUser = currentUserHelper.getCurrentUser();
        model.addAttribute("currentUser", currentUser);
        if (currentUser != null && currentUser.getRole() == Role.PATIENT) {
            return "redirect:/patients/my";
        }
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
