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
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static java.util.Collections.singletonList;

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

    @GetMapping("/")
    public String getHomePage(Model model) {
        model.addAttribute("authInfo", new AuthorizationDto());
        return "index";
    }

    @PostMapping("login")
    public String login(@ModelAttribute("authInfo") AuthorizationDto authorizationDto, HttpServletResponse response) {
        try {
            String email = authorizationDto.getUsername();
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, authorizationDto.getPassword())
            );
            User user = userService.findByEmailOrUsername(email);

            if (!user.isActive()) {
                log.warn("User {} is not active", email);
                throw new UserIsNotActiveException("User " + email + " is not active");
            }

            String token = jwtTokenProvider.createToken(email, singletonList(user.getRole()));
            Cookie cookie = new Cookie("Authorization", token);
            cookie.setMaxAge(7 * 24 * 60 * 60); // expires in 7 days
            response.addCookie(cookie);

            if (user.getRole() == UserRole.ROLE_ADMIN) {
                return "redirect:http://localhost:8080/admin/index";
            } else if (user.getRole() == UserRole.ROLE_STUDENT) {
                return "redirect:http://localhost:8080/student/index";
            } else if (user.getRole() == UserRole.ROLE_TEACHER) {
                return "redirect:http://localhost:8080/teacher/index";
            } else {
                return "index";
            }
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid username or password");
        }
    }

    @GetMapping("/auth/confirm/{hash_code}")
    public ModelAndView confirmUser(@PathVariable("hash_code") String hashCode) {
        userService.confirmUser(hashCode);
        ModelAndView modelAndView = new ModelAndView("index");
        modelAndView.addObject("authInfo", new AuthorizationDto());
        modelAndView.addObject("successMessage", "Your profile.html was activated!");
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
        model.addAttribute("authInfo", new AuthorizationDto());
        model.addAttribute("successMessage", "An email has been sent. Please check your inbox.");
        return "index";
    }

    @GetMapping("/reset_password/{code}")
    public String resetPassword(@PathVariable String code, Model model) {
        model.addAttribute("code", code);
        return "reset-password";
    }

    @PostMapping("/reset_password/{code}")
    public ModelAndView resetPassword(@PathVariable String code,
                                      @RequestParam String newPassword) {
        userService.updatePassword(code, newPassword);
        ModelAndView modelAndView = new ModelAndView("index");
        modelAndView.addObject("authInfo", new AuthorizationDto());
        modelAndView.addObject("successMessage", "Your password was successfully changed!");
        return modelAndView;
    }

    @GetMapping("/logout")
    public String logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("Authorization", null);
        cookie.setMaxAge(0);
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
        return "redirect:/";
    }

    @RequestMapping("/access-denied")
    public String accessdenied() {
        return "fragment/access-denied";
    }

    @GetMapping("/profile")
    public String viewProfile(Model model, HttpServletRequest request) {
        String email = jwtTokenProvider.getEmail(request);
        User user = userService.findByUsername(email);
        model.addAttribute("user", user);
        model.addAttribute("imgUtil", new AdminController.ImageUtil());
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
        return "redirect:" + request.getHeader("referer");
    }

    @PostMapping("/uploadFile/{userId}")
    public String uploadFile(@RequestParam("myfile") MultipartFile myfile,
                             @PathVariable Long userId,
                             HttpServletRequest request) throws IOException {
        User user = userService.findById(userId);
        user.setPhoto(myfile.getBytes());
        userService.save(user);
        return "redirect:" + request.getHeader("referer");
    }
}
