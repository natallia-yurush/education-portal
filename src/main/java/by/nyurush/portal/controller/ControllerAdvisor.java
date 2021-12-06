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
import by.nyurush.portal.util.MessageUtil;
import lombok.RequiredArgsConstructor;
import org.postgresql.util.PSQLException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Locale;
import java.util.ResourceBundle;

import static by.nyurush.portal.util.Constants.ERROR;
import static by.nyurush.portal.util.Constants.ERROR_MESSAGE;
import static by.nyurush.portal.util.Constants.HOST;
import static by.nyurush.portal.util.Constants.REDIRECT;
import static by.nyurush.portal.util.MessageUtil.getMessage;
import static org.springframework.http.HttpHeaders.REFERER;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ControllerAdvice
@RequiredArgsConstructor
public class ControllerAdvisor extends ResponseEntityExceptionHandler {

    private final LocaleResolver localeResolver;

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
        String errorMessage = getMessage(localeResolver, request, e.getMessageKey());
        redirectAttrs.addFlashAttribute(ERROR_MESSAGE, errorMessage);
        response.setStatus(BAD_REQUEST.value());
        return REDIRECT + request.getHeader(REFERER);
    }

    @ExceptionHandler({JwtAuthenticationException.class})
    public String handleNoPermissionException(HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttrs) {
        String errorMessage = getMessage(localeResolver, request, "error.message.token.expired");
        redirectAttrs.addFlashAttribute(ERROR_MESSAGE, errorMessage);
        response.setStatus(FORBIDDEN.value());
        return REDIRECT + HOST + "/index";
    }

    @ExceptionHandler({UserIsNotActiveException.class})
    public ModelAndView handleUserIsNotActiveException(HttpServletRequest request, UserIsNotActiveException e) {

        ModelAndView modelAndView = new ModelAndView("index");
        modelAndView.addObject("authInfo", new AuthorizationDto());
        String errorMessage = getMessage(localeResolver, request, e.getMessageKey());
        modelAndView.addObject(ERROR, errorMessage);
        return modelAndView;
    }

    @ExceptionHandler({BadCredentialsException.class})
    public String handleBadCredentialsException(HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttrs) {
        String errorMessage = getMessage(localeResolver, request, "error.bad.credentials");
        redirectAttrs.addFlashAttribute(ERROR, errorMessage);
        response.setStatus(FORBIDDEN.value());
        return REDIRECT + request.getHeader(REFERER);
    }

    @ExceptionHandler({PSQLException.class, EntityAlreadyExistException.class, UserNotFoundException.class})
    public String handlePSQLException(HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttrs) {
        String errorMessage = getMessage(localeResolver, request, "error.data.exist");
        redirectAttrs.addFlashAttribute(ERROR_MESSAGE, errorMessage);
        response.setStatus(BAD_REQUEST.value());
        return REDIRECT + request.getHeader(REFERER);
    }

    @ExceptionHandler({EntityNotFoundException.class})
    public String handleEntityNotFoundException(HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttrs) {
        String errorMessage = getMessage(localeResolver, request, "error.not.found.data");
        redirectAttrs.addFlashAttribute(ERROR_MESSAGE, errorMessage);
        response.setStatus(NOT_FOUND.value());
        return REDIRECT + request.getHeader(REFERER);
    }

    @ExceptionHandler({InvalidUserDataException.class})
    public String handleInvalidUserDataException(HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttrs,
                                                 InvalidUserDataException e) {
        String errorMessage = getMessage(localeResolver, request, e.getMessageKey());
        redirectAttrs.addFlashAttribute(ERROR_MESSAGE, errorMessage);
        response.setStatus(e.getHttpStatus().value());
        return REDIRECT + request.getHeader(REFERER);
    }

    @ExceptionHandler({InvalidUserAnswerException.class})
    public String handleInvalidUserAnswerException(HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttrs,
                                                   InvalidUserAnswerException e) {
        redirectAttrs.addFlashAttribute(ERROR_MESSAGE, e.getMessage());
        response.setStatus(BAD_REQUEST.value());
        return REDIRECT + request.getHeader(REFERER);
    }

    @ExceptionHandler({InvalidTestItemException.class})
    public String handleInvalidTestItemException(HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttrs,
                                                 InvalidTestItemException e) {
        redirectAttrs.addFlashAttribute(ERROR_MESSAGE, e.getMessage());
        response.setStatus(BAD_REQUEST.value());
        return REDIRECT + request.getHeader(REFERER);
    }

}
