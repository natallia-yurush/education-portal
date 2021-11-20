package by.nyurush.portal.security;

import by.nyurush.portal.entity.User;
import by.nyurush.portal.security.jwt.JwtUser;
import by.nyurush.portal.security.jwt.JwtUserFactory;
import by.nyurush.portal.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtUserDetailsService implements UserDetailsService {

    public final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.findByEmailOrUsername(username);

        JwtUser jwtUser = JwtUserFactory.create(user);
        log.info("IN loadUserByUsername - user with username: {} successfully loaded", username);
        return jwtUser;
    }
}
