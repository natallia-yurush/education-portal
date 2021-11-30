package by.nyurush.portal.dto.converter;

import by.nyurush.portal.dto.QuestionDto;
import by.nyurush.portal.dto.UserAnswerDto;
import by.nyurush.portal.entity.Answer;
import by.nyurush.portal.entity.Question;
import by.nyurush.portal.entity.UserAnswer;
import by.nyurush.portal.repository.AnswerRepository;
import by.nyurush.portal.repository.QuestionRepository;
import lombok.AllArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toSet;

@Component
@AllArgsConstructor
public class UserAnswerConverter implements Converter<QuestionDto, UserAnswer> {

    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;

    @Override
    public UserAnswer convert(QuestionDto question) {
        UserAnswer userAnswer = new UserAnswer();
        Question questionToSave = questionRepository.findByText(question.getText()).orElseThrow(EntityNotFoundException::new);
        userAnswer.setQuestion(questionToSave);
        List<UserAnswerDto> answerDtoList = question.getAnswerDtos().stream().filter(UserAnswerDto::isSelected).collect(Collectors.toList());
        Set<Answer> chosenAnswers = answerDtoList.stream().map(
                answerDto -> answerRepository.findByText(answerDto.getText()).orElseThrow(EntityNotFoundException::new)
        ).collect(toSet());
        userAnswer.setAnswerList(chosenAnswers);
        return userAnswer;
    }
}
