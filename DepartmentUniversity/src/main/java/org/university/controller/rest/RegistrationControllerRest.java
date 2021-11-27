package org.university.controller.rest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.university.exceptions.InvalidTokenException;
import org.university.service.TemporaryUserService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/register")
@AllArgsConstructor
public class RegistrationControllerRest {

    private final TemporaryUserService userService;

    @PostMapping("/{token}")
    @ResponseStatus(HttpStatus.OK)
    public void verifyUser(@PathVariable("token") String token, final Model model) {
        if (StringUtils.isBlank(token)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Secure token is empty!");
        }
        try {
            userService.verifyUser(token);
        } catch (InvalidTokenException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
