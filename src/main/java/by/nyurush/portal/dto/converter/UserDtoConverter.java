package by.nyurush.portal.dto.converter;

import by.nyurush.portal.dto.UserDto;
import by.nyurush.portal.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserDtoConverter implements Converter<User, UserDto> {
    @Override
    public UserDto convert(User source) {
        UserDto userDto = new UserDto();
        //todo
//        userDto.set
        return null;
    }
}
