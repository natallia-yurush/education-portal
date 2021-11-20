package by.nyurush.portal.exception.user;

public class UserAlreadyIsActiveException extends RuntimeException {
    public UserAlreadyIsActiveException() {
        super();
    }

    public UserAlreadyIsActiveException(String message) {
        super(message);
    }
}
