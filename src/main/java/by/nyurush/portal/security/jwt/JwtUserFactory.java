package by.nyurush.portal.security.jwt;

import by.nyurush.portal.entity.User;
import by.nyurush.portal.entity.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;

public class JwtUserFactory {

    public static JwtUser create(User user) {
        return new JwtUser(
                user.getId(),
                user.getCompleteName(),
                user.getEmail(),
                user.getPhoto(),
                user.getUsername(),
                user.getPassword(),
                mapToGrantedAuthorities(singletonList(user.getRole()))
        );
    }

    private static List<GrantedAuthority> mapToGrantedAuthorities(List<UserRole> userRoles) {
        return userRoles.stream()
                .map(role ->
                        new SimpleGrantedAuthority(role.name())
                ).collect(Collectors.toList());
    }
}
