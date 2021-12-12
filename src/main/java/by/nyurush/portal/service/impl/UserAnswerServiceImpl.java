package by.nyurush.portal.service.impl;

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
import by.nyurush.portal.repository.UserAnswerRepository;
import by.nyurush.portal.service.UserAnswerService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toSet;

@Service
@AllArgsConstructor
@Slf4j
public class UserAnswerServiceImpl implements UserAnswerService {

    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final ExamRepository examRepository;
    private final UserAnswerRepository userAnswerRepository;

    @Override
    public UserAnswer save(QuestionDto questionDto, Long examId, User user) {
        UserAnswer userAnswer = new UserAnswer();
        Question questionToSave = questionRepository.findByText(questionDto.getText()).orElseThrow(EntityNotFoundException::new);
        userAnswer.setQuestion(questionToSave);
        List<UserAnswerDto> answerDtoList = questionDto.getAnswerDtos().stream().filter(UserAnswerDto::isSelected).collect(Collectors.toList());
        Set<Answer> chosenAnswers = answerDtoList.stream().map(
                answerDto -> answerRepository.findByText(answerDto.getText()).orElseThrow(EntityNotFoundException::new)
        ).collect(toSet());
        userAnswer.setAnswerList(chosenAnswers);
        Exam exam = examRepository.findById(examId).orElseThrow(EntityNotFoundException::new);
        userAnswer.setExam(exam);
        userAnswer.setUser(user);

        return userAnswerRepository.save(userAnswer);
    }
}
