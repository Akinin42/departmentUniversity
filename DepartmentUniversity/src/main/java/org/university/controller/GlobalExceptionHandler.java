package org.university.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.university.exceptions.AuthorisationFailException;
import org.university.exceptions.EntityAlreadyExistException;
import org.university.exceptions.InvalidAddressException;
import org.university.exceptions.InvalidClassroomCapacityException;
import org.university.exceptions.InvalidClassroomNumberException;
import org.university.exceptions.InvalidCourseNameException;
import org.university.exceptions.InvalidDescriptionException;
import org.university.exceptions.InvalidGroupNameException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({ InvalidClassroomNumberException.class, InvalidClassroomCapacityException.class,
            InvalidAddressException.class, InvalidCourseNameException.class, InvalidDescriptionException.class,
            InvalidGroupNameException.class, EntityAlreadyExistException.class })
    public ModelAndView handleClassroomException(Exception e, HttpServletRequest request) {
        ModelAndView response = new ModelAndView();
        response.addObject("message", e.getMessage());
        response.setViewName(String.format("redirect:%s", request.getServletPath()));
        return response;
    }

    @ExceptionHandler(AuthorisationFailException.class)
    public ModelAndView handleAuthorisationFailException(Exception e, HttpServletRequest request) {
        ModelAndView response = new ModelAndView();
        response.addObject("message", "passworddontcorrect");
        String view = String.format("redirect:%s", request.getServletPath());
        int lenghtViewWithoutLogin = view.length() - 6;
        response.setViewName(view.substring(0, lenghtViewWithoutLogin));
        return response;
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFoundException(Exception e, HttpServletRequest request) {
        return "error404";
    }
    
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleServerException(Exception e, HttpServletRequest request) {
        return "error500";
    }
}
