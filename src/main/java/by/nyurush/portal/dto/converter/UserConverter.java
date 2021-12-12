package by.nyurush.portal.dto.converter;

import by.nyurush.portal.dto.UserDto;
import by.nyurush.portal.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserConverter implements Converter<UserDto, User> {

    @Override
    public User convert(UserDto userDto) {
        User user = new User();
        user.setId(userDto.getId());
        user.setUsername(userDto.getUsername());
        user.setPassword(userDto.getPassword());
        user.setEmail(userDto.getEmail());
        user.setCompleteName(getCompleteName(userDto));
        return user;
    }

    private String getCompleteName(UserDto userDto) {
        return userDto.getLastName() + " " + userDto.getFirstName() + " " + userDto.getMiddleName();
    }
}
