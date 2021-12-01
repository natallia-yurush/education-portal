package by.nyurush.portal.dto.converter;

import by.nyurush.portal.dto.UserDto;
import by.nyurush.portal.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Base64;

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
//        user.setPhoto(userDto.getPhoto());
        return user;
    }

    private String getCompleteName(UserDto userDto) {
        return userDto.getLastName() + " " + userDto.getFirstName() + " " + userDto.getMiddleName();
    }

    private static String convertBinImageToString(byte[] binImage) {
        if (binImage != null && binImage.length > 0) {
            return Base64.getEncoder().encodeToString(binImage);
        } else {
            return "";
        }
    }
}
