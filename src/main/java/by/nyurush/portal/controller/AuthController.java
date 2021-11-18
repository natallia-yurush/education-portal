package by.nyurush.portal.controller;

import by.nyurush.portal.config.jwt.JwtProvider;
import by.nyurush.portal.dto.AuthorizationDto;
import by.nyurush.portal.entity.User;
import by.nyurush.portal.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.HashMap;
import java.util.Map;

@Controller
@AllArgsConstructor
public class AuthController {

    private final JwtProvider jwtProvider;
//    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    @GetMapping
    public String getHomePage(Model model) {
        model.addAttribute("authInfo", new AuthorizationDto());
        return "index";
    }

    @PostMapping("login")
    public String login(@ModelAttribute("authInfo") AuthorizationDto authorizationDto) {

        try {
            String username = authorizationDto.getUsername();
//            authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(username, authorizationDto.getPassword())
//            );
            User user = userService.findByUsername(username);

//            if (!user.isActive()) {
//                log.warn("User {} already is active", username);
//                throw new UserAlreadyIsActiveException("User " + username + " already is active");
//            }

            String token = jwtProvider.generateToken(username);


            return "admin/index";
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid username or password");
        }


    }
}
