package org.university.controller.rest;

import java.util.List;
import java.util.Locale;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.university.dto.UserDto;
import org.university.entity.TemporaryUser;
import org.university.exceptions.AuthorisationFailException;
import org.university.exceptions.EmailExistException;
import org.university.exceptions.InvalidPhotoException;
import org.university.service.PhotoService;
import org.university.service.TemporaryUserService;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/api/v1/temporary")
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class TemporaryUserControllerRest {

    TemporaryUserService temporaryUserService;
    PhotoService photoService;

    @GetMapping()
    public List<TemporaryUser> getAllUsers() {
        return temporaryUserService.findAllConfirmUser();
    }

    @PostMapping(consumes = { "multipart/form-data" })
    @ResponseStatus(HttpStatus.CREATED)
    public void addUser(@Valid @RequestPart("user") UserDto user, @RequestPart("photo") MultipartFile photo,
            Locale locale) {
        try {
            user.setPhoto(photo);
            String photoName = photoService.savePhoto(user);
            user.setPhotoName(photoName);
            user.setConfirm(true);
            user.setLocale(locale);
            temporaryUserService.register(user);
        } catch (EmailExistException | InvalidPhotoException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/{email}")
    public TemporaryUser getProfile(@PathVariable("email") String email) {
        return temporaryUserService.getByEmail(email);
    }

    @PostMapping(value = "/update", consumes = { "multipart/form-data" })
    @ResponseStatus(HttpStatus.OK)
    public TemporaryUser edit(@Valid @RequestPart("user") UserDto user, @RequestPart("photo") MultipartFile photo) {
        try {
            user.setPhoto(photo);
            String photoName = photoService.savePhoto(user);
            user.setPhotoName(photoName);
            user.setConfirm(true);
            user.setConfirmDescription(null);
            temporaryUserService.edit(user);
            return temporaryUserService.getByEmail(user.getEmail());
        } catch (EmailExistException | InvalidPhotoException | AuthorisationFailException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
