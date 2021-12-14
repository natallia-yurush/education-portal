package by.nyurush.portal.builder;

import by.nyurush.portal.dto.UserDto;
import lombok.NoArgsConstructor;

import static by.nyurush.portal.util.TestConstants.TEST;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class UserBuilder {

    public static UserDto buildUserDto() {
        UserDto userDto = new UserDto();
        userDto.setFirstName(TEST);
        userDto.setLastName(TEST);
        userDto.setMiddleName(TEST);
        userDto.setUsername(TEST);
        userDto.setPassword(TEST);
        userDto.setEmail(TEST + "@gmail.com");
        return userDto;
    }
}
