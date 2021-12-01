package by.nyurush.portal.exception.user;

public class InvalidUserAnswerException extends RuntimeException {

    public InvalidUserAnswerException() {
    }

    public InvalidUserAnswerException(String message) {
        super(message);
    }
}
