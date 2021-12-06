package by.nyurush.portal.controller;

import by.nyurush.portal.dto.AuthorizationDto;
import by.nyurush.portal.dto.UserDto;
import by.nyurush.portal.entity.User;
import by.nyurush.portal.entity.UserRole;
import by.nyurush.portal.exception.user.UserIsNotActiveException;
import by.nyurush.portal.security.jwt.JwtTokenProvider;
import by.nyurush.portal.service.impl.UserServiceImpl;
import by.nyurush.portal.validator.UserDataValidator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static by.nyurush.portal.util.Constants.AUTH_INFO;
import static by.nyurush.portal.util.Constants.HOST;
import static by.nyurush.portal.util.Constants.IMG_UTIL;
import static by.nyurush.portal.util.Constants.INDEX;
import static by.nyurush.portal.util.Constants.REDIRECT;
import static by.nyurush.portal.util.Constants.SUCCESS_MESSAGE;
import static by.nyurush.portal.util.Constants.USER;
import static by.nyurush.portal.util.MessageUtil.getMessage;
import static java.io.File.separator;
import static java.util.Collections.singletonList;
import static org.springframework.http.HttpHeaders.REFERER;

@Controller
@RequestMapping
@Slf4j
@AllArgsConstructor
public class AuthController {

    private final UserServiceImpl userService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final ConversionService conversionService;
    private final UserDataValidator userDataValidator;
    private final LocaleResolver localeResolver;

    @GetMapping("/")
    public String getHomePage(Model model) {
        model.addAttribute(AUTH_INFO, new AuthorizationDto());
        return INDEX;
    }

    @PostMapping("login")
    public String login(@ModelAttribute(AUTH_INFO) AuthorizationDto authorizationDto, HttpServletResponse response) {
        try {
            String email = authorizationDto.getUsername();
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, authorizationDto.getPassword())
            );
            User user = userService.findByEmailOrUsername(email);

            if (!user.isActive()) {
                log.warn("User {} is not active", email);
                throw new UserIsNotActiveException();
            }

            String token = jwtTokenProvider.createToken(email, singletonList(user.getRole()));
            Cookie cookie = new Cookie("Authorization", token);
            cookie.setMaxAge(7 * 24 * 60 * 60); // expires in 7 days
            response.addCookie(cookie);

            if (user.getRole() == UserRole.ROLE_ADMIN) {
                return REDIRECT + HOST + "/admin/index";
            } else if (user.getRole() == UserRole.ROLE_STUDENT) {
                return REDIRECT + HOST + "/student/index";
            } else if (user.getRole() == UserRole.ROLE_TEACHER) {
                return REDIRECT + HOST + "/teacher/index";
            } else {
                return INDEX;
            }
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid username or password");
        }
    }

    @GetMapping("/auth/confirm/{hash_code}")
    public ModelAndView confirmUser(@PathVariable("hash_code") String hashCode,
                                    HttpServletRequest request) {
        userService.confirmUser(hashCode);
        ModelAndView modelAndView = new ModelAndView(INDEX);
        modelAndView.addObject(AUTH_INFO, new AuthorizationDto());
        modelAndView.addObject(SUCCESS_MESSAGE, getMessage(localeResolver, request,
                "success.message.activated.user"));
        return modelAndView;
    }

    @GetMapping("/forgot_password")
    public String forgotPassword() {
        return "forgot-password";
    }

    @PostMapping("/forgot_password")
    public String forgotPassword(@RequestParam String email,
                                 HttpServletRequest request,
                                 Model model) {
        userService.resetPassword(email, request);
        model.addAttribute(AUTH_INFO, new AuthorizationDto());
        model.addAttribute(SUCCESS_MESSAGE, getMessage(localeResolver, request,
                "success.message.email.sent"));
        return "index";
    }

    @GetMapping("/reset_password/{code}")
    public String resetPassword(@PathVariable String code, Model model) {
        model.addAttribute("code", code);
        return "reset-password";
    }

    @PostMapping("/reset_password/{code}")
    public ModelAndView resetPassword(@PathVariable String code,
                                      @RequestParam String newPassword,
                                      HttpServletRequest request) {
        userService.updatePassword(code, newPassword);
        ModelAndView modelAndView = new ModelAndView(INDEX);
        modelAndView.addObject(AUTH_INFO, new AuthorizationDto());
        modelAndView.addObject(SUCCESS_MESSAGE, getMessage(localeResolver, request,
                "success.message.password.changed"));
        return modelAndView;
    }

    @GetMapping("/logout")
    public String logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("Authorization", null);
        cookie.setMaxAge(0);
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
        return REDIRECT + separator;
    }

    @RequestMapping("/access-denied")
    public String accessdenied() {
        return "fragment/access-denied";
    }

    @GetMapping("/profile")
    public String viewProfile(Model model, HttpServletRequest request) {
        String email = jwtTokenProvider.getEmail(request);
        User user = userService.findByUsername(email);
        model.addAttribute(USER, user);
        model.addAttribute(IMG_UTIL, new AdminController.ImageUtil());
        return "student/profile";
    }

    @PostMapping("/profile")
    public String updateProfileInfo(@ModelAttribute("user") UserDto userDto, HttpServletRequest request) {
        User user = conversionService.convert(userDto, User.class);
        userDataValidator.validate(user);
        User existsUser = userService.findById(user.getId());
        user.setRole(existsUser.getRole());
        user.setActive(existsUser.isActive());
        user.setPhoto(existsUser.getPhoto());
        userService.save(user);
        return REDIRECT + request.getHeader(REFERER);
    }

    @PostMapping("/uploadFile/{userId}")
    public String uploadFile(@RequestParam("myfile") MultipartFile myfile,
                             @PathVariable Long userId,
                             HttpServletRequest request) throws IOException {
        User user = userService.findById(userId);
        user.setPhoto(myfile.getBytes());
        userService.save(user);
        return REDIRECT + request.getHeader(REFERER);
    }
}
