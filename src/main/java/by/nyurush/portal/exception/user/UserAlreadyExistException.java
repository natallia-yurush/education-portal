package by.nyurush.portal.exception.user;

public class UserAlreadyExistException extends RuntimeException {
    
    public UserAlreadyExistException(String message) {
        super(message);
    }
}
