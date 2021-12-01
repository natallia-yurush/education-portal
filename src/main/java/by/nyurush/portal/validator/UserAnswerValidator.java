package by.nyurush.portal.validator;

import by.nyurush.portal.dto.QuestionDto;
import by.nyurush.portal.dto.UserAnswerDto;
import by.nyurush.portal.exception.user.InvalidUserAnswerException;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Validated
@Component
public class UserAnswerValidator {

    public void validate(QuestionDto questionDto) {
        if (questionDto.getAnswerDtos().stream().noneMatch(UserAnswerDto::isSelected)) {
            throw new InvalidUserAnswerException("You should choose at least one answer option.");
        }
    }
}
