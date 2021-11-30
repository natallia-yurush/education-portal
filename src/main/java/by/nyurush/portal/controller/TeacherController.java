package by.nyurush.portal.controller;

import by.nyurush.portal.dto.AnswerDto;
import by.nyurush.portal.dto.AssignExamDto;
import by.nyurush.portal.dto.ExamDto;
import by.nyurush.portal.dto.ExamResultDetailsDto;
import by.nyurush.portal.dto.ExamResultSummaryDto;
import by.nyurush.portal.dto.TestItemDto;
import by.nyurush.portal.entity.Exam;
import by.nyurush.portal.entity.ExamResult;
import by.nyurush.portal.entity.Question;
import by.nyurush.portal.entity.User;
import by.nyurush.portal.entity.UserRole;
import by.nyurush.portal.repository.ExamRepository;
import by.nyurush.portal.repository.ExamResultRepository;
import by.nyurush.portal.repository.QuestionRepository;
import by.nyurush.portal.repository.UserRepository;
import by.nyurush.portal.service.ExamResultService;
import by.nyurush.portal.service.UserService;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "/teacher")
@AllArgsConstructor
public class TeacherController {

    private static final String AJAX_HEADER_NAME = "X-Requested-With";
    private static final String AJAX_HEADER_VALUE = "XMLHttpRequest";

    private final ExamRepository examRepository;
    private final QuestionRepository questionRepository;
    private final ExamResultRepository examResultRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final ConversionService conversionService;
    private final ExamResultService examResultService;

    @GetMapping("/index")
    public String getIndex(Model model) {
        model.addAttribute("studentsNumber", userRepository.countUserByRole(UserRole.ROLE_STUDENT));
        model.addAttribute("examNumber", examRepository.count());
        return "teacher/index";
    }

    @GetMapping("/exams")
    public String getExams(Model model) {
        model.addAttribute("exam", new ExamDto());
        List<Exam> exams = examRepository.findAll();
        List<ExamDto> examDtos = exams.stream()
                .map(exam -> conversionService.convert(exam, ExamDto.class))
                .collect(Collectors.toList());
        model.addAttribute("exams", examDtos);

        return "teacher/exam";
    }

    @PostMapping("/exam")
    public String addExam(@ModelAttribute("exam") ExamDto examDto) {
        Exam exam = conversionService.convert(examDto, Exam.class);
        examRepository.save(exam);
        return "redirect:exams";
    }

    @PostMapping("/exam/delete/{id}")
    public String deleteExam(@PathVariable Long id) {
        examRepository.deleteById(id);
        return "redirect:exams";
    }

    @GetMapping(path = {"/question", "/question/{id}"})
    public String showOrder(@PathVariable(required = false) Long id, Model model) {
        List<Question> questionList = questionRepository.findAll();
        model.addAttribute("questions", questionList);
        TestItemDto testItemDto = new TestItemDto();
        testItemDto.setAnswers(List.of(
                new AnswerDto("", false),
                new AnswerDto("", false)));
        model.addAttribute("testItem", testItemDto);
        return "teacher/question";
    }

    @PostMapping(path = {"/question", "/question/{id}"})
    public String saveOrder(@ModelAttribute TestItemDto testItemDto) {
        Question question = conversionService.convert(testItemDto, Question.class);
        questionRepository.save(question);
        return "redirect:question";
    }

    @PostMapping(path = {"/addItem"})
    public String addOrder(TestItemDto testItemDto, HttpServletRequest request, Model model) {
        testItemDto.getAnswers().add(new AnswerDto());
        if (AJAX_HEADER_VALUE.equals(request.getHeader(AJAX_HEADER_NAME))) {
            model.addAttribute("testItem", testItemDto);
            return "teacher/question::#answers";
        } else {
            return "teacher/question";
        }
    }

    @PostMapping("/question/delete/{id}")
    public String deleteQuestion(@PathVariable Long id) {
        questionRepository.deleteById(id);
        return "redirect:question";
    }

    @PostMapping("/question/edit/{id}")
    public String editQuestion(@PathVariable Long id, Model model) {
        TestItemDto testItemDto = new TestItemDto();
        testItemDto.setAnswers(new ArrayList<>());
        Question question = questionRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        question.getAnswerList().forEach(answer -> testItemDto.getAnswers().add(new AnswerDto()));
        model.addAttribute("itemToEdit", testItemDto);

        return "redirect:question";
    }

    @GetMapping("/assign-exam")
    public String assignExam(Model model) {
        Set<Exam> examList = examRepository.findByUserListIsNotNull();
        model.addAttribute("exams", examList);
        model.addAttribute("assignExam", new AssignExamDto());
        return "teacher/assign-exam";
    }

    @PostMapping("/assign-exam")
    public String assignExam(@ModelAttribute("exam") AssignExamDto assignExamDto) {
        Exam exam = examRepository.findById(assignExamDto.getExamId()).orElseThrow(EntityNotFoundException::new);
        User user = userRepository.findById(assignExamDto.getUserId()).orElseThrow(EntityNotFoundException::new);
        user.getExamList().add(exam);
        userRepository.save(user);
        return "redirect:assign-exam";
    }

    @PostMapping("/unassign/{userId}/{examId}")
    public String unassignExam(@PathVariable("userId") Long userId,
                               @PathVariable("examId") Long examId) {
        userService.unassignExam(userId, examId);
        return "redirect:/teacher/assign-exam";
    }

    @GetMapping("/exam-results")
    public String viewExamResults(Model model) {
        List<ExamResult> examResultList = examResultRepository.findAll();
        model.addAttribute("examResults", examResultList);
        return "teacher/exam-result";
    }

    @GetMapping("/score-card/{examResultId}")
    public String viewExamResults(@PathVariable Long examResultId,
                                  Model model) {
        List<ExamResultDetailsDto> examResultDetailsDtoList = examResultService.getExamResultDetails(examResultId);
        model.addAttribute("examResult", examResultDetailsDtoList);
        return "teacher/view-score";
    }

    @PostMapping("/exam-result/delete/{resultId}")
    public String viewExamResults(@PathVariable Long resultId) {
        examResultRepository.deleteById(resultId);
        return "redirect:exam-results";
    }

    @GetMapping("summary")
    public String viewExamResultSummary(Model model) {
        List<ExamResultSummaryDto> examResultSummaryDtoList = examResultService.getExamResultSummaryDtoList();
        model.addAttribute("summaryList", examResultSummaryDtoList);
        model.addAttribute("examNameList", examResultService.getExamNameList());
        model.addAttribute("passedList", examResultService.getPassedList());
        model.addAttribute("failedList", examResultService.getFailedList());

        return "teacher/summary";
    }
}
