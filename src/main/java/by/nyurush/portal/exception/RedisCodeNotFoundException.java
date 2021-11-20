package by.nyurush.portal.exception;

public class RedisCodeNotFoundException extends RuntimeException {
    public RedisCodeNotFoundException() {
        super();
    }

    public RedisCodeNotFoundException(String message) {
        super(message);
    }
}
