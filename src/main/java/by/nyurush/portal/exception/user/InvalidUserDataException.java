package by.nyurush.portal.exception.user;

import by.nyurush.portal.exception.PortalException;

import static java.util.Collections.singletonList;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

public class InvalidUserDataException extends PortalException {

    public final static String MESSAGE_KEY = "error.invalid.message";

    public InvalidUserDataException(String message) {
        super(MESSAGE_KEY, BAD_REQUEST, singletonList(message));
    }
}
