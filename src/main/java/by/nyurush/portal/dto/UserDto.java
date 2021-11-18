package by.nyurush.portal.dto;

import lombok.Data;

@Data
public class UserDto {

    private long id;
    private String firstName;
    private String lastName;
    private String middleName;
    private String email;
    private byte[] photo;
    private String username;
    private String password;
}
