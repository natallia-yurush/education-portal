package by.nyurush.portal.exception.user;

public class InvalidUserDataException extends RuntimeException {

    public InvalidUserDataException() {
    }

    public InvalidUserDataException(String message) {
        super(message);
    }
}
