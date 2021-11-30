package by.nyurush.portal.controller;

import by.nyurush.portal.dto.AuthorizationDto;
import by.nyurush.portal.entity.User;
import by.nyurush.portal.entity.UserRole;
import by.nyurush.portal.exception.user.UserAlreadyIsActiveException;
import by.nyurush.portal.exception.user.UserIsNotActiveException;
import by.nyurush.portal.security.jwt.JwtTokenProvider;
import by.nyurush.portal.service.impl.UserServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import static java.util.Collections.singletonList;

@Controller
@RequestMapping
@Slf4j
@AllArgsConstructor
public class AuthController {

    private final UserServiceImpl userService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

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

            // create a cookie
            Cookie cookie = new Cookie("Authorization", token);
            cookie.setMaxAge(7 * 24 * 60 * 60); // expires in 7 days

            //add cookie to response
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
    public String confirmUser(@PathVariable("hash_code") String hashCode) {
        userService.confirmUser(hashCode);
        return "index";
    }

    @GetMapping("/forgot_password")
    public String forgotPassword() {
        return "forgot-password";
    }

    @PostMapping("/forgot_password")
    public String forgotPassword(@RequestParam String email,
                                 HttpServletResponse response) {
        userService.resetPassword(email);
        //todo return page with message that everything is ok and that is needed to check email
        return "reset-password";
    }

    @GetMapping("/reset_password/{code}")
    public String resetPassword(@PathVariable String code, Model model) {
        model.addAttribute("code", code);
        return "reset-password";
    }

    @PostMapping("/reset_password/{code}")
    public String resetPassword(@PathVariable String code,
                                @RequestParam String newPassword) {
        userService.updatePassword(code, newPassword);
        return "redirect:http://localhost:8080";
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
}
