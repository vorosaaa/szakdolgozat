package org.ati.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.ati.core.model.UserDTO;
import org.ati.core.service.ConfirmationTokenService;
import org.ati.core.service.SecurityService;
import org.ati.core.service.UserService;
import org.ati.core.validators.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

@Controller
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private ConfirmationTokenService confirmationTokenService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private UserValidator userValidator;


    @GetMapping("/registration")
    public String registration(Model model) {
        if (securityService.isAuthenticated()) {
            return "redirect:/";
        }

        model.addAttribute("userForm", new UserDTO());
        return "registration";
    }

    @PostMapping("/registration")
    public String registration(@ModelAttribute("userForm") UserDTO userForm, BindingResult bindingResult) {
        userValidator.validate(userForm, bindingResult);

        if (bindingResult.hasErrors()) {
            return "registration";
        }

        userService.save(userForm);
        securityService.autoLogin(userForm.getUsername(), userForm.getMatchingPassword());

        return "redirect:/welcome";
    }

    @GetMapping("/login")
    public String login(Model model, String error, String logout, HttpServletRequest request) {
        if (securityService.isAuthenticated()) {
            return "redirect:/";
        }
        if (error != null) {
            model.addAttribute("error", "Your username and password is invalid.");
            log.error(error);
        }
        if (logout != null) {
            model.addAttribute("message", "You have been logged out successfully.");
        }
        return "login";
    }

    @GetMapping({"/", "/welcome", "/myprofile"})
    public String welcome(Model model, Principal principal) {
        model.addAttribute("user", userService.findByUsername(principal.getName()));
        return "myprofile";
    }
}
