package by.nyurush.portal.service.impl;

import by.nyurush.portal.dto.AnswerDto;
import by.nyurush.portal.dto.TestItemDto;
import by.nyurush.portal.entity.Answer;
import by.nyurush.portal.entity.Exam;
import by.nyurush.portal.entity.Question;
import by.nyurush.portal.repository.AnswerRepository;
import by.nyurush.portal.repository.ExamRepository;
import by.nyurush.portal.repository.QuestionRepository;
import by.nyurush.portal.service.QuestionService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class QuestionServiceImpl implements QuestionService {

    private AnswerRepository answerRepository;
    private ExamRepository examRepository;
    private QuestionRepository questionRepository;

    @Override
    public Question saveQuestion(TestItemDto testItemDto) {
        Question question = new Question();
        Exam exam = examRepository.findById(testItemDto.getExamId()).orElseThrow(EntityNotFoundException::new); //todo
        question.setText(testItemDto.getQuestionText());
        question.setExam(exam);
        question.setAnswerList(new HashSet<>());
        question.setCorrectAnswerList(new HashSet<>());
        List<AnswerDto> answerDtoList = testItemDto.getAnswers();

        answerDtoList.forEach(answerDto -> setAnswersToQuestion(question, answerDto));

        return questionRepository.save(question);
    }

    private void setAnswersToQuestion(Question question, AnswerDto answerDto) {
        Optional<Answer> optionalAnswer = answerRepository.findByText(answerDto.getText());
        Answer answer = optionalAnswer.orElseGet(() -> convertToAnswer(answerDto));
        question.getAnswerList().add(answer);
        if (answerDto.isCorrect()) {
            question.getCorrectAnswerList().add(answer);
        }
    }

    private Answer convertToAnswer(AnswerDto answerDto) {
        Answer answer = new Answer();
        answer.setText(answerDto.getText());
        return answer;
    }

}
