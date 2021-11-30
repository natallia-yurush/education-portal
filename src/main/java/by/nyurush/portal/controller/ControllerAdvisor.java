package by.nyurush.portal.controller;

import by.nyurush.portal.exception.EntityAlreadyExistException;
import by.nyurush.portal.exception.RedisCodeNotFoundException;
import by.nyurush.portal.exception.user.UserAlreadyExistException;
import by.nyurush.portal.exception.user.UserAlreadyIsActiveException;
import by.nyurush.portal.exception.user.UserNotFoundException;
import by.nyurush.portal.security.jwt.JwtAuthenticationException;
import org.postgresql.util.PSQLException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.naming.NoPermissionException;
import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ControllerAdvice
public class ControllerAdvisor extends ResponseEntityExceptionHandler {

    @ExceptionHandler({
            UserNotFoundException.class,
            RedisCodeNotFoundException.class
    })
    public ResponseEntity<?> handleNotFoundException(Model model, HttpServletRequest request) {
        model.addAttribute("error", "error");
        request.setAttribute("error", "error");
        return new ResponseEntity<>(NOT_FOUND);
    }

    @ExceptionHandler({UserAlreadyExistException.class, UserAlreadyIsActiveException.class})
    public ResponseEntity<?> handleAlreadyExistException() {
        return new ResponseEntity<>(BAD_REQUEST);
    }

    @ExceptionHandler({NoPermissionException.class, JwtAuthenticationException.class})
    public ResponseEntity<?> handleNoPermissionException() {
        return new ResponseEntity<>(FORBIDDEN);
    }

    @ExceptionHandler({BadCredentialsException.class})
    public String handleBadCredentialsException(HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttrs) {
        redirectAttrs.addFlashAttribute("error", "Incorrect login or password.");
        response.setStatus(FORBIDDEN.value());
        return "redirect:" + request.getHeader("referer");
    }

    @ExceptionHandler({PSQLException.class, EntityAlreadyExistException.class})
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

}
