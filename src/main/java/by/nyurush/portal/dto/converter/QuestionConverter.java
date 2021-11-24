package by.nyurush.portal.dto.converter;

import by.nyurush.portal.dto.AnswerDto;
import by.nyurush.portal.dto.TestItemDto;
import by.nyurush.portal.entity.Answer;
import by.nyurush.portal.entity.Exam;
import by.nyurush.portal.entity.Question;
import by.nyurush.portal.repository.ExamRepository;
import lombok.AllArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
public class QuestionConverter implements Converter<TestItemDto, Question> {

    private final ExamRepository examRepository;

    @Override
    public Question convert(TestItemDto testItemDto) {
        Question question = new Question();
        Exam exam = examRepository.findById(testItemDto.getExamId()).orElseThrow(); //todo
        question.setText(testItemDto.getQuestionText());
        question.setExam(exam);
        question.setAnswerList(new ArrayList<>());
        question.setCorrectAnswerList(new ArrayList<>());
        List<AnswerDto> answerDtoList = testItemDto.getAnswers();

        answerDtoList.forEach(answerDto -> setAnswersToQuestion(question, answerDto));

        return question;
    }

    private Answer convertToAnswer(AnswerDto answerDto) {
        Answer answer = new Answer();
        answer.setText(answerDto.getText());
        return answer;
    }

    private void setAnswersToQuestion(Question question, AnswerDto answerDto) {
        Answer answer = convertToAnswer(answerDto);
        question.getAnswerList().add(answer);
        if (answerDto.isCorrect()) {
            question.getCorrectAnswerList().add(answer);
        }
    }

}
