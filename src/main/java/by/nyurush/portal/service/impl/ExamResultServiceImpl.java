package by.nyurush.portal.service.impl;

import by.nyurush.portal.dto.ExamResultDetailsDto;
import by.nyurush.portal.dto.ExamResultSummaryDto;
import by.nyurush.portal.dto.SummaryDto;
import by.nyurush.portal.entity.Exam;
import by.nyurush.portal.entity.ExamResult;
import by.nyurush.portal.entity.Question;
import by.nyurush.portal.entity.User;
import by.nyurush.portal.entity.UserAnswer;
import by.nyurush.portal.repository.ExamRepository;
import by.nyurush.portal.repository.ExamResultRepository;
import by.nyurush.portal.repository.UserAnswerRepository;
import by.nyurush.portal.service.ExamResultService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@AllArgsConstructor
@Slf4j
public class ExamResultServiceImpl implements ExamResultService {

    private final ExamResultRepository examResultRepository;
    private final ExamRepository examRepository;
    private final UserAnswerRepository userAnswerRepository;

    @Override
    public ExamResult calculateTestResult(User user, Long examId) {
        Exam exam = examRepository.findById(examId).orElseThrow(EntityNotFoundException::new);
        List<Question> questionList = exam.getQuestionList();

        double allCorrectAnswers = 0;
        double correctUserAnswers = 0;

        for (Question question : questionList) {
            UserAnswer userAnswer = userAnswerRepository
                    .findByExam_IdAndUser_IdAndQuestion_Id(examId, user.getId(), question.getId())
                    .orElseThrow(EntityNotFoundException::new);

            allCorrectAnswers += question.getCorrectAnswerList().size();
            correctUserAnswers += userAnswer.getAnswerList().stream()
                    .filter(answer -> question.getAnswerList().contains(answer))
                    .count();

        }

        double score = correctUserAnswers / allCorrectAnswers * 100;
        score = (double) Math.round(score * 100) / 100;
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
        ExamResult examResult = examResultRepository.findById(examResultId).orElseThrow(EntityNotFoundException::new);

        List<ExamResultDetailsDto> examResultDetailsDtoList = new ArrayList<>();

        Exam exam = examResult.getExam();
        User user = examResult.getUser();

        List<Question> questionList = exam.getQuestionList();
        for (Question question : questionList) {
            ExamResultDetailsDto examResultDetailsDto = new ExamResultDetailsDto();
            examResultDetailsDto.setQuestion(question);
            UserAnswer userAnswer = userAnswerRepository
                    .findByExam_IdAndUser_IdAndQuestion_Id(exam.getId(), user.getId(), question.getId())
                    .orElseThrow(EntityNotFoundException::new);
            examResultDetailsDto.setSelectedAnswers(userAnswer.getAnswerList());

            examResultDetailsDtoList.add(examResultDetailsDto);
        }

        return examResultDetailsDtoList;
    }

    @Override
    public List<ExamResultSummaryDto> getExamResultSummaryDtoList() {
        List<SummaryDto> summaryDtos = examResultRepository.getExamResultSummary();
        return summaryDtos.stream()
                .map(summaryDto -> {
                    ExamResultSummaryDto examResultSummaryDto = new ExamResultSummaryDto();
                    examResultSummaryDto.setName(summaryDto.getName());
                    examResultSummaryDto.setPassed(summaryDto.getPassed());
                    examResultSummaryDto.setFailed(summaryDto.getFailed());
                    return examResultSummaryDto;
                })
                .collect(toList());
    }

    @Override
    public List<String> getExamNameList() {
        List<ExamResultSummaryDto> examResultSummaryDtoList = getExamResultSummaryDtoList();
        return examResultSummaryDtoList.stream()
                .map(ExamResultSummaryDto::getName)
                .collect(toList());
    }

    @Override
    public List<Integer> getPassedList() {
        List<ExamResultSummaryDto> examResultSummaryDtoList = getExamResultSummaryDtoList();
        return examResultSummaryDtoList.stream()
                .map(ExamResultSummaryDto::getPassed)
                .collect(toList());
    }

    @Override
    public List<Integer> getFailedList() {
        List<ExamResultSummaryDto> examResultSummaryDtoList = getExamResultSummaryDtoList();
        return examResultSummaryDtoList.stream()
                .map(ExamResultSummaryDto::getFailed)
                .collect(toList());
    }

}
