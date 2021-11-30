package by.nyurush.portal.exception.user;

public class UserIsNotActiveException extends RuntimeException {

    public UserIsNotActiveException() {
        super();
    }

    public UserIsNotActiveException(String message) {
        super(message);
    }
}
