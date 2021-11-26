package by.nyurush.portal.service.impl;

import by.nyurush.portal.dto.ExamResultDetailsDto;
import by.nyurush.portal.dto.ExamResultSummaryDto;
import by.nyurush.portal.entity.Exam;
import by.nyurush.portal.entity.ExamResult;
import by.nyurush.portal.entity.Question;
import by.nyurush.portal.entity.User;
import by.nyurush.portal.entity.UserAnswer;
import by.nyurush.portal.repository.ExamRepository;
import by.nyurush.portal.repository.ExamResultRepository;
import by.nyurush.portal.repository.UserAnswerRepository;
import by.nyurush.portal.repository.UserRepository;
import by.nyurush.portal.service.ExamResultService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class ExamResultServiceImpl implements ExamResultService {

    private final ExamResultRepository examResultRepository;
    private final UserRepository userRepository;
    private final ExamRepository examRepository;
    private final UserAnswerRepository userAnswerRepository;

    @Override
    public ExamResult calculateTestResult(Long examId) {
        //todo get user with token
        User user = userRepository.getById(3L);
        Exam exam = examRepository.findById(examId).orElseThrow();

        List<Question> questionList = exam.getQuestionList();

        double allCorrectAnswers = 0;
        double correctUserAnswers = 0;

        for (Question question : questionList) {
            UserAnswer userAnswer = userAnswerRepository
                    .findByExam_IdAndUser_IdAndQuestion_Id(examId, user.getId(), question.getId())
                    .orElseThrow();

            allCorrectAnswers += question.getCorrectAnswerList().size();
            correctUserAnswers += userAnswer.getAnswerList().stream()
                    .filter(answer -> question.getAnswerList().contains(answer))
                    .count();

        }

        double score = correctUserAnswers / allCorrectAnswers * 100;
        boolean passed = score >= exam.getPassingScore();

        ExamResult examResult = new ExamResult();
        examResult.setExam(exam);
        examResult.setUser(user);
        examResult.setScore(score);
        examResult.setPassed(passed);

        examResultRepository.save(examResult);
        return examResult;
    }

    @Override
    public List<ExamResultDetailsDto> getExamResultDetails(Long examResultId) {
        ExamResult examResult = examResultRepository.findById(examResultId).orElseThrow();

        List<ExamResultDetailsDto> examResultDetailsDtoList = new ArrayList<>();

        Exam exam = examResult.getExam();
        User user = examResult.getUser();

        List<Question> questionList = exam.getQuestionList();
        for(Question question : questionList) {
            ExamResultDetailsDto examResultDetailsDto = new ExamResultDetailsDto();
            examResultDetailsDto.setQuestion(question);
            UserAnswer userAnswer = userAnswerRepository.findByExam_IdAndUser_IdAndQuestion_Id(exam.getId(), user.getId(), question.getId()).orElseThrow();
            examResultDetailsDto.setSelectedAnswers(userAnswer.getAnswerList());

            examResultDetailsDtoList.add(examResultDetailsDto);
        }

        return examResultDetailsDtoList;
    }

//    @Override
//    public List<ExamResultSummaryDto> getExamResultSummary() {
//        List<ExamResultSummaryDto> examResultSummaryList = new ArrayList<>();
//        List<ExamResult> examResultList = examResultRepository.findAll();
//
//        examResultList.forEach(result -> );
//
//        return null;
//    }

}
