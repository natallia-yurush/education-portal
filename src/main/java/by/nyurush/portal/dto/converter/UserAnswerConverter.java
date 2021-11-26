package by.nyurush.portal.dto.converter;

import by.nyurush.portal.dto.QuestionDto;
import by.nyurush.portal.dto.UserAnswerDto;
import by.nyurush.portal.entity.Answer;
import by.nyurush.portal.entity.Exam;
import by.nyurush.portal.entity.Question;
import by.nyurush.portal.entity.User;
import by.nyurush.portal.entity.UserAnswer;
import by.nyurush.portal.repository.AnswerRepository;
import by.nyurush.portal.repository.ExamRepository;
import by.nyurush.portal.repository.QuestionRepository;
import by.nyurush.portal.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class UserAnswerConverter implements Converter<QuestionDto, UserAnswer> {

    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final AnswerRepository answerRepository;

    @Override
    public UserAnswer convert(QuestionDto question) {
        UserAnswer userAnswer = new UserAnswer();
        Question questionToSave = questionRepository.findByText(question.getText()).orElseThrow();
        userAnswer.setQuestion(questionToSave);
        //todo find by token
        User user = userRepository.findById(3L).orElseThrow();
        userAnswer.setUser(user);
        List<UserAnswerDto> answerDtoList = question.getAnswerDtos().stream().filter(UserAnswerDto::isSelected).collect(Collectors.toList());
        List<Answer> chosenAnswers = answerDtoList.stream().map(
                answerDto -> answerRepository.findByText(answerDto.getText()).orElseThrow()
        ).collect(Collectors.toList());
        userAnswer.setAnswerList(chosenAnswers);
        return userAnswer;
    }
}
