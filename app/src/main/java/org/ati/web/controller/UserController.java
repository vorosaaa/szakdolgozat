package org.ati.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.ati.core.model.UserDTO;
import org.ati.core.service.ConfirmationTokenService;
import org.ati.core.service.SecurityService;
import org.ati.core.service.UserService;
import org.ati.core.validators.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
    private BCryptPasswordEncoder bCryptPasswordEncoder;

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

    @PostMapping("/update")
    public String update(@ModelAttribute("user") UserDTO userForm, Principal principal, BindingResult errors) {
        UserDTO user = userService.findByUsername(principal.getName());
        String username = userForm.getUsername();
        String oldPw = userForm.getPassword();
        String pw = userForm.getNewPassword();
        String matching = userForm.getMatchingPassword();

        user.setName(userForm.getName());
        if (!username.equals(user.getUsername())) {
            if (userService.findByUsername(username) != null) {
                errors.rejectValue("username", "Duplicate.userForm.username");
            } else if (username.length() < 6 || username.length() > 32) {
                errors.rejectValue("username", "Size.userForm.username");
            } else {
                user.setUsername(username);
            }
        }
        if (!StringUtils.isEmpty(pw)) {
            if (oldPw == null || !bCryptPasswordEncoder.matches(oldPw, user.getPassword())) {
                errors.rejectValue("password", "Must.enter.prev.pw");
            } else if (!pw.equals(matching)) {
                errors.rejectValue("matchingPassword", "Diff.userForm.passwordConfirm");
            } else {
                user.setPassword(pw);
            }
        }
        if (errors.hasErrors()) {
            return "myprofile";
        }
        userService.save(user);
        SecurityContextHolder.getContext().setAuthentication(null);

        return "redirect:/welcome";
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
