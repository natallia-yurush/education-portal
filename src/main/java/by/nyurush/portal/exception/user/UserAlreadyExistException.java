package by.nyurush.portal.exception.user;

import by.nyurush.portal.exception.PortalException;
import org.springframework.http.HttpStatus;

import static java.util.Collections.singletonList;

public class UserAlreadyExistException extends PortalException {

    public static final String USER_ALREADY_EXIST_MESSAGE_KEY = "com.sap.ibx.upload.file-size";

    public UserAlreadyExistException(String username) {
        super(USER_ALREADY_EXIST_MESSAGE_KEY, HttpStatus.BAD_REQUEST, singletonList(username));
    }
}
