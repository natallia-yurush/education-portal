package by.nyurush.portal.controller;

import by.nyurush.portal.dto.AuthorizationDto;
import by.nyurush.portal.entity.User;
import by.nyurush.portal.entity.UserRole;
import by.nyurush.portal.exception.user.UserAlreadyIsActiveException;
import by.nyurush.portal.security.jwt.JwtTokenProvider;
import by.nyurush.portal.service.RedisService;
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

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

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
    public String login(@ModelAttribute("authInfo") AuthorizationDto authorizationDto) {
        try {
            String email = authorizationDto.getUsername();
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, authorizationDto.getPassword())
            );
            User user = userService.findByEmailOrUsername(email);

            if (!user.isActive()) {
                log.warn("User {} already is not active", email);
                throw new UserAlreadyIsActiveException("User " + email + " already is not active");
            }

            String token = jwtTokenProvider.createToken(email, singletonList(user.getRole()));

            // TODO: where I should store token ?
            Map<Object, Object> response = new HashMap<>();
            response.put("email", email);
            response.put("token", token);

            if (user.getRole() == UserRole.ROLE_ADMIN) {
                return "admin/index";
            } else if (user.getRole() == UserRole.ROLE_STUDENT) {
                return "student/index";
            } else if (user.getRole() == UserRole.ROLE_TEACHER) {
                return "teacher/index";
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
        //todo return page with message that everything is alright and that is needed to check email
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

}
