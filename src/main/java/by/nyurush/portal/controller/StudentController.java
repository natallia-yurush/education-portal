package by.nyurush.portal.controller;

import by.nyurush.portal.dto.ExamResultDetailsDto;
import by.nyurush.portal.dto.QuestionDto;
import by.nyurush.portal.dto.UserAnswerDto;
import by.nyurush.portal.entity.Exam;
import by.nyurush.portal.entity.ExamResult;
import by.nyurush.portal.entity.Question;
import by.nyurush.portal.entity.User;
import by.nyurush.portal.entity.UserAnswer;
import by.nyurush.portal.repository.AnswerRepository;
import by.nyurush.portal.repository.ExamRepository;
import by.nyurush.portal.repository.ExamResultRepository;
import by.nyurush.portal.repository.QuestionRepository;
import by.nyurush.portal.repository.UserAnswerRepository;
import by.nyurush.portal.repository.UserRepository;
import by.nyurush.portal.service.ExamResultService;
import lombok.AllArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "/student")
@AllArgsConstructor
public class StudentController {

    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final ExamRepository examRepository;
    private final AnswerRepository answerRepository;
    private final UserAnswerRepository userAnswerRepository;
    private final ConversionService conversionService;
    private final ExamResultService examResultService;
    private final ExamResultRepository examResultRepository;

    @GetMapping("/index")
    public String getIndex(Model model) {
        return "student/index";
    }

    @GetMapping("/test/{examId}")
    public String getTest(@PathVariable Long examId, Model model) {
        List<Question> questionList = questionRepository.findByExam_Id(examId);
        // todo get user id by token
        Long userId = 3L;
        List<UserAnswer> userAnswers = userAnswerRepository.findAllByExam_IdAndUser_Id(examId, userId);
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
            return ""; //todo calculate  result
        }
    }

    @PostMapping("/test/{examId}")
    public String answerQuestion(@PathVariable Long examId, @ModelAttribute() QuestionDto question) {
        UserAnswer userAnswer = conversionService.convert(question, UserAnswer.class);
        Exam exam = examRepository.findById(examId).orElseThrow();
        userAnswer.setExam(exam);

        userAnswerRepository.save(userAnswer);
        return "redirect:" + examId;
    }

    @GetMapping("/test/result")
    public String viewResult(Model model) {

        //todo get with token
        User user = userRepository.getById(3L);
        List<ExamResult> examResultList = examResultRepository.findAllByUser_Id(user.getId());

        model.addAttribute("examResultList", examResultList);

        return "student/result";
    }

    @GetMapping("/test/calculate/{examId}")
    public String calculateResult(@PathVariable Long examId) {
        examResultService.calculateTestResult(examId);
        return "redirect:/student/test/result";
    }

    @GetMapping("/test/result/{examResultId}")
    public String viewExamResultDetails(@PathVariable Long examResultId, Model model) {
        List<ExamResultDetailsDto> examResultDetailsDtoList = examResultService.getExamResultDetails(examResultId);
        model.addAttribute("examResult", examResultDetailsDtoList);
        return "student/view-score";
    }


    @GetMapping("/upcoming-exams")
    public String getUpcomingExams(Model model) {
        //todo get with token
        User user = userRepository.getById(3L);
        List<Exam> upcomingExams = examRepository.findByUserListContains(user);

        model.addAttribute("upcomingExams", upcomingExams);

        return "student/upcoming-exam";
    }
}
