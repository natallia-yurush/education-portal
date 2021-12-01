package by.nyurush.portal.controller;

import by.nyurush.portal.dto.AuthorizationDto;
import by.nyurush.portal.exception.EntityAlreadyExistException;
import by.nyurush.portal.exception.InvalidTestItemException;
import by.nyurush.portal.exception.RedisCodeNotFoundException;
import by.nyurush.portal.exception.user.InvalidUserAnswerException;
import by.nyurush.portal.exception.user.InvalidUserDataException;
import by.nyurush.portal.exception.user.UserAlreadyExistException;
import by.nyurush.portal.exception.user.UserIsNotActiveException;
import by.nyurush.portal.exception.user.UserNotFoundException;
import by.nyurush.portal.security.jwt.JwtAuthenticationException;
import org.postgresql.util.PSQLException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ControllerAdvice
public class ControllerAdvisor extends ResponseEntityExceptionHandler {

    @ExceptionHandler({
            RedisCodeNotFoundException.class
    })
    public ResponseEntity<?> handleNotFoundException(Model model, HttpServletRequest request) {
        model.addAttribute("error", "error");
        request.setAttribute("error", "error");
        return new ResponseEntity<>(NOT_FOUND);
    }

    @ExceptionHandler({UserAlreadyExistException.class})
    public String handleAlreadyExistException(HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttrs,
                                              UserAlreadyExistException e) {
        redirectAttrs.addFlashAttribute("errorMessage", e.getMessage());
        response.setStatus(BAD_REQUEST.value());
        return "redirect:" + request.getHeader("referer");
    }

    @ExceptionHandler({JwtAuthenticationException.class})
    public String handleNoPermissionException(HttpServletResponse response, RedirectAttributes redirectAttrs) {
        redirectAttrs.addFlashAttribute("errorMessage", "Your token is expires. Please log in again.");
        response.setStatus(FORBIDDEN.value());
        return "redirect:" + "http://localhost:8080/index";
    }

    @ExceptionHandler({UserIsNotActiveException.class})
    public ModelAndView handleUserIsNotActiveException(HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttrs) {

        ModelAndView modelAndView = new ModelAndView("index");
        modelAndView.addObject("authInfo", new AuthorizationDto());
        modelAndView.addObject("error", "User is not active. Please check your email.");
        return modelAndView;

//        redirectAttrs.addFlashAttribute("error", "User is not active. Please check your email.");
//        response.setStatus(FORBIDDEN.value());
//        return "redirect:" + request.getHeader("referer");
    }

    @ExceptionHandler({BadCredentialsException.class})
    public String handleBadCredentialsException(HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttrs) {
        redirectAttrs.addFlashAttribute("error", "Incorrect login or password.");
        response.setStatus(FORBIDDEN.value());
        return "redirect:" + request.getHeader("referer");
    }

    @ExceptionHandler({PSQLException.class, EntityAlreadyExistException.class, UserNotFoundException.class})
    public String handlePSQLException(HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttrs) {
        redirectAttrs.addFlashAttribute("errorMessage", "Can't add data because it already exists.");
        response.setStatus(BAD_REQUEST.value());
        return "redirect:" + request.getHeader("referer");
    }

    @ExceptionHandler({EntityNotFoundException.class})
    public String handleEntityNotFoundException(HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttrs) {
        redirectAttrs.addFlashAttribute("errorMessage", "Some data cannot be found. Try again.");
        response.setStatus(NOT_FOUND.value());
        return "redirect:" + request.getHeader("referer");
    }

    @ExceptionHandler({InvalidUserDataException.class})
    public String handleInvalidUserDataException(HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttrs,
                                                 InvalidUserDataException e) {
        redirectAttrs.addFlashAttribute("errorMessage", e.getMessage());
        response.setStatus(BAD_REQUEST.value());
        return "redirect:" + request.getHeader("referer");
    }

    @ExceptionHandler({InvalidUserAnswerException.class})
    public String handleInvalidUserAnswerException(HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttrs,
                                                   InvalidUserAnswerException e) {
        redirectAttrs.addFlashAttribute("errorMessage", e.getMessage());
        response.setStatus(BAD_REQUEST.value());
        return "redirect:" + request.getHeader("referer");
    }

    @ExceptionHandler({InvalidTestItemException.class})
    public String handleInvalidTestItemException(HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttrs,
                                                 InvalidTestItemException e) {
        redirectAttrs.addFlashAttribute("errorMessage", e.getMessage());
        response.setStatus(BAD_REQUEST.value());
        return "redirect:" + request.getHeader("referer");
    }
}
