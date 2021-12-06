package by.nyurush.portal.controller;

import by.nyurush.portal.dto.ExamResultDetailsDto;
import by.nyurush.portal.dto.QuestionDto;
import by.nyurush.portal.dto.UserAnswerDto;
import by.nyurush.portal.entity.Exam;
import by.nyurush.portal.entity.ExamResult;
import by.nyurush.portal.entity.Question;
import by.nyurush.portal.entity.User;
import by.nyurush.portal.entity.UserAnswer;
import by.nyurush.portal.repository.ExamRepository;
import by.nyurush.portal.repository.ExamResultRepository;
import by.nyurush.portal.repository.QuestionRepository;
import by.nyurush.portal.repository.UserAnswerRepository;
import by.nyurush.portal.security.jwt.JwtTokenProvider;
import by.nyurush.portal.service.ExamResultService;
import by.nyurush.portal.service.UserService;
import by.nyurush.portal.validator.UserAnswerValidator;
import lombok.AllArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

import static by.nyurush.portal.util.Constants.HOST;
import static by.nyurush.portal.util.Constants.INDEX;
import static by.nyurush.portal.util.Constants.REDIRECT;
import static by.nyurush.portal.util.Constants.STUDENT;

@Controller
@RequestMapping(value = STUDENT)
@AllArgsConstructor
public class StudentController {

    private final JwtTokenProvider jwtTokenProvider;
    private final QuestionRepository questionRepository;
    private final UserService userService;
    private final ExamRepository examRepository;
    private final UserAnswerRepository userAnswerRepository;
    private final ConversionService conversionService;
    private final ExamResultService examResultService;
    private final ExamResultRepository examResultRepository;
    private final UserAnswerValidator userAnswerValidator;

    @GetMapping(INDEX)
    public String getIndex(Model model, HttpServletRequest request) {
        String email = jwtTokenProvider.getEmail(request);
        User user = userService.findByUsername(email);

        List<ExamResult> examResultList = examResultRepository.findAllByUser_Id(user.getId());
        long passed = examResultList.stream().filter(ExamResult::isPassed).count();
        long failed = examResultList.stream().filter(examResult -> !examResult.isPassed()).count();

        model.addAttribute("totalExams", examRepository.count());
        model.addAttribute("upcomingExams", user.getExamList().size());
        model.addAttribute("passedExams", passed);
        model.addAttribute("failedExams", failed);

        return "student/index";
    }

    @GetMapping("/test/{examId}")
    public String getTest(@PathVariable Long examId, Model model, HttpServletRequest request) {
        List<Question> questionList = questionRepository.findByExam_Id(examId);
        String email = jwtTokenProvider.getEmail(request);
        User user = userService.findByUsername(email);

        List<UserAnswer> userAnswers = userAnswerRepository.findAllByExam_IdAndUser_Id(examId, user.getId());
        userAnswers.forEach(userAnswer -> questionList.remove(userAnswer.getQuestion()));

        if (!questionList.isEmpty()) {
            Question question = questionList.get(0);

            QuestionDto questionDto = new QuestionDto();
            questionDto.setText(question.getText());
            questionDto.setAnswerDtos(question.getAnswerList().stream()
                    .map(answer -> {
                        UserAnswerDto answerDto = new UserAnswerDto();
                        answerDto.setText(answer.getText());
                        return answerDto;
                    })
                    .collect(Collectors.toList()));
            model.addAttribute("question", questionDto);
            model.addAttribute("examId", examId);
            return "student/test";
        } else {
            return REDIRECT + HOST + "/student/test/calculate/" + examId;
        }
    }

    @PostMapping("/test/{examId}")
    public String answerQuestion(@PathVariable Long examId, @ModelAttribute() QuestionDto question, HttpServletRequest request) {
        userAnswerValidator.validate(question);
        UserAnswer userAnswer = conversionService.convert(question, UserAnswer.class);
        Exam exam = examRepository.findById(examId).orElseThrow(EntityNotFoundException::new);
        userAnswer.setExam(exam);
        String email = jwtTokenProvider.getEmail(request);
        User user = userService.findByUsername(email);
        userAnswer.setUser(user);

        userAnswerRepository.save(userAnswer);
        return REDIRECT + examId;
    }

    @GetMapping("/test/result")
    public String viewResult(Model model, HttpServletRequest request) {
        String email = jwtTokenProvider.getEmail(request);
        User user = userService.findByUsername(email);
        List<ExamResult> examResultList = examResultRepository.findAllByUser_Id(user.getId());

        model.addAttribute("examResultList", examResultList);

        return "student/result";
    }

    @GetMapping("/test/calculate/{examId}")
    public String calculateResult(@PathVariable Long examId, HttpServletRequest request) {
        String email = jwtTokenProvider.getEmail(request);
        User user = userService.findByUsername(email);
        examResultService.calculateTestResult(user, examId);

        Exam exam = examRepository.findById(examId).orElseThrow(EntityNotFoundException::new);
        user.getExamList().remove(exam);
        userService.save(user);

        return REDIRECT + HOST + "/student/test/result";
    }

    @GetMapping("/test/result/{examResultId}")
    public String viewExamResultDetails(@PathVariable Long examResultId, Model model) {
        List<ExamResultDetailsDto> examResultDetailsDtoList = examResultService.getExamResultDetails(examResultId);
        model.addAttribute("examResult", examResultDetailsDtoList);
        return "student/view-score";
    }

    @GetMapping("/upcoming-exams")
    public String getUpcomingExams(Model model, HttpServletRequest request) {
        String email = jwtTokenProvider.getEmail(request);
        User user = userService.findByUsername(email);
        List<Exam> upcomingExams = examRepository.findByUserListContains(user);

        model.addAttribute("upcomingExams", upcomingExams);
        return "student/upcoming-exam";
    }
}
