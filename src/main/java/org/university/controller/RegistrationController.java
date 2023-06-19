package org.university.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.university.exceptions.InvalidTokenException;
import org.university.service.TemporaryUserService;

import lombok.AllArgsConstructor;

@Controller
@RequestMapping("/register")
@AllArgsConstructor
public class RegistrationController {

    private static final String REDIRECT_LOGIN = "redirect:/login";

    private final TemporaryUserService userService;

    @GetMapping()
    public String verifyUser(@RequestParam String token, final Model model) {
        if (StringUtils.isEmpty(token)) {
            return REDIRECT_LOGIN;
        }
        try {
            userService.verifyUser(token);
        } catch (InvalidTokenException e) {
            return REDIRECT_LOGIN;
        }
        return REDIRECT_LOGIN;
    }
}
