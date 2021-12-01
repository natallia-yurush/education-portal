package by.nyurush.portal.validator;

import by.nyurush.portal.dto.AnswerDto;
import by.nyurush.portal.dto.TestItemDto;
import by.nyurush.portal.exception.InvalidTestItemException;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Validated
@Component
public class TestItemValidator {

    public void validate(TestItemDto testItemDto) {
        if(testItemDto.getAnswers().stream().noneMatch(AnswerDto::isCorrect)) {
            throw new InvalidTestItemException("Question should have at least one correct answer.");
        }
    }
}
