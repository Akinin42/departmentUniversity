package org.university.controller;

import java.util.Locale;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.university.dto.UserDto;
import org.university.exceptions.AuthorisationFailException;
import org.university.exceptions.EmailExistException;
import org.university.exceptions.InvalidPhotoException;
import org.university.service.PhotoService;
import org.university.service.TemporaryUserService;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

@Controller
@RequestMapping("/temporary")
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class TemporaryUserController {

    TemporaryUserService temporaryUserService;
    PhotoService photoService;

    private static final String USER_FORM = "userform";
    private static final String UPDATE_STUDENT_FORM = "updateforms/user";

    @GetMapping()
    public String getAllUsers(@ModelAttribute("message") String message, Model model) {
        model.addAttribute("user", new UserDto());
        model.addAttribute("temporaryUsers", temporaryUserService.findAllConfirmUser());
        return "requests";
    }

    @GetMapping("/new")
    public String newUser(Model model) {
        model.addAttribute("user", new UserDto());
        return USER_FORM;
    }

    @PostMapping()
    public String addUser(@ModelAttribute("user") @Valid UserDto user, BindingResult bindingResult, Model model,
            Locale locale) {
        if (bindingResult.hasErrors()) {
            if (bindingResult.hasFieldErrors("password")) {
                model.addAttribute("message", bindingResult.getFieldError("password").getDefaultMessage());
            }
            return USER_FORM;
        }
        try {
            String photoName = photoService.savePhoto(user);
            user.setPhotoName(photoName);
            user.setConfirm(true);
            user.setLocale(locale);
            temporaryUserService.register(user);
            return "mainmenu";
        } catch (EmailExistException | InvalidPhotoException e) {
            model.addAttribute("message", e.getMessage());
            return USER_FORM;
        }
    }

    @PostMapping("/profile")
    public String getProfile(@ModelAttribute("user") UserDto userDto, Model model) {
        model.addAttribute("user", temporaryUserService.getByEmail(userDto.getEmail()));
        return "userprofile";
    }

    @PostMapping("/edit")
    public String getEditForm(@ModelAttribute("user") UserDto user, @ModelAttribute("message") String message,
            Model model) {
        model.addAttribute("user", user);
        return UPDATE_STUDENT_FORM;
    }

    @PostMapping("/update")
    public String edit(@ModelAttribute("user") @Valid UserDto user, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            if (bindingResult.hasFieldErrors("password")) {
                model.addAttribute("message", bindingResult.getFieldError("password").getDefaultMessage());
            }
            return UPDATE_STUDENT_FORM;
        }
        try {
            String photoName = photoService.savePhoto(user);
            user.setPhotoName(photoName);
            user.setConfirm(true);
            user.setConfirmDescription(null);
            temporaryUserService.edit(user);
            model.addAttribute("user", temporaryUserService.getByEmail(user.getEmail()));
            return "userprofile";
        } catch (EmailExistException | InvalidPhotoException | AuthorisationFailException e) {
            model.addAttribute("message", e.getMessage());
            return UPDATE_STUDENT_FORM;
        }
    }
}
