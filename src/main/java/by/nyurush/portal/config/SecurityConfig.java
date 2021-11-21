package by.nyurush.portal.config;

import by.nyurush.portal.security.jwt.JwtConfigurer;
import by.nyurush.portal.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
//        http
//                .httpBasic().disable()
//                .csrf().disable()
//                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                .and()
//                .authorizeRequests()
//                .antMatchers("/admin/*").hasRole("ADMIN")
//
//                .anyRequest().authenticated()
//                .and()
//                .apply(new JwtConfigurer(jwtTokenProvider));

//        http
//                .httpBasic().disable()
//                .csrf().disable()
//                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                .and()
//                .authorizeRequests()
//                .antMatchers("/admin/*").hasRole("ADMIN")
//                .antMatchers("/user/*").hasRole("USER")
//                .antMatchers("/", "/register", "/auth").permitAll()
//                .and()
//                .apply(new JwtConfigurer(jwtTokenProvider));
//                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/",
                "/fontawesome/**",
                "/css/**",
                "/img/**",
                "/jquery/**",
                "/js/**",
                "/tables/**",
                "/login",

                "/auth/registration/**",
                "/auth/confirm/**",
                "/auth/forgot_password/**",
                "/auth/reset/**",
                "/auth/check_code",
                "/articles"
        );

    }
}
