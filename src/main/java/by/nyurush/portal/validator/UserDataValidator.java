package by.nyurush.portal.validator;

import by.nyurush.portal.entity.User;
import by.nyurush.portal.exception.user.InvalidUserDataException;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Validated
@Component
public class UserDataValidator {

    private static final String EMAIL_REGEX = "[-a-z0-9!#$%&'*+/=?^_`{|}~]+(\\.[-a-z0-9!#$%&'*+/=?^_`{|}~]+)*@" +
            "([a-z0-9]([-a-z0-9]{0,40}[a-z0-9])?\\.)*([a-z]{2,4})";
    private static final String PASSWORD_REGEX = "(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,30}";
    private static final String USERNAME_REGEX = "^[a-zA-Z][\\w-_.]{2,20}$";
    private static final String NAME_REGEX = "[а-яА-ЯёЁa-zA-Z ]{2,60}";
    private static final int MAX_INPUT_LENGTH = 40;

    public void validate(User user) {
        if (!user.getCompleteName().matches(NAME_REGEX)) {
            throw new InvalidUserDataException("User name is incorrect.");
        }
        if (!user.getEmail().matches(EMAIL_REGEX)) {
            throw new InvalidUserDataException("Email is incorrect.");
        }
        if (!user.getUsername().matches(USERNAME_REGEX) && user.getUsername().length() > MAX_INPUT_LENGTH) {
            throw new InvalidUserDataException("Username is incorrect");
        }
//        if (!user.getPassword().matches(PASSWORD_REGEX)) {
//            throw new InvalidUserDataException("Password is not strong enough.");
//        }
    }
}
