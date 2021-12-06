package by.nyurush.portal.exception.user;

import by.nyurush.portal.exception.PortalException;
import org.springframework.http.HttpStatus;

public class UserIsNotActiveException extends PortalException {

    public static final String USER_IS_NOT_ACTIVE_MESSAGE = "error.message.user.is.not.active";

    public UserIsNotActiveException() {
        super(USER_IS_NOT_ACTIVE_MESSAGE, HttpStatus.BAD_REQUEST);
    }
}
