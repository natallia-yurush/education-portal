package by.nyurush.portal.dto;

import lombok.Data;

@Data
public class UserDto {

    private Long id;
    private String firstName;
    private String lastName;
    private String middleName;
    private String email;
    private String username;
    private String password;
}
