package by.nyurush.portal.config;

import by.nyurush.portal.security.jwt.JwtTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

//@EnableWebMvc
@Configuration
public class ApplicationConfiguration {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
//
//    @Bean
//    public JwtTokenProvider jwtTokenProvider() {
//        return new JwtTokenProvider();
//    }

}
