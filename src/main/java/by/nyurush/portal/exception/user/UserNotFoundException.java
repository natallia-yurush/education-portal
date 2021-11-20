package by.nyurush.portal.exception.user;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException() {
        super();
    }

    public UserNotFoundException(String message) {
        super(message);
    }
}
