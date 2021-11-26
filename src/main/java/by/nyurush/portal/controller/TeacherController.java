package by.nyurush.portal.controller;

import by.nyurush.portal.dto.AnswerDto;
import by.nyurush.portal.dto.AssignExamDto;
import by.nyurush.portal.dto.ExamDto;
import by.nyurush.portal.dto.ExamResultSummaryDto;
import by.nyurush.portal.dto.SummaryDto;
import by.nyurush.portal.dto.TestItemDto;
import by.nyurush.portal.entity.Exam;
import by.nyurush.portal.entity.ExamResult;
import by.nyurush.portal.entity.Question;
import by.nyurush.portal.entity.User;
import by.nyurush.portal.repository.ExamRepository;
import by.nyurush.portal.repository.ExamResultRepository;
import by.nyurush.portal.repository.QuestionRepository;
import by.nyurush.portal.repository.UserRepository;
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

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
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

    @GetMapping("/index")
    public String getIndex(Model model) {
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
        Question question = questionRepository.findById(id).orElseThrow();
        question.getAnswerList().forEach(answer -> testItemDto.getAnswers().add(new AnswerDto()));
        model.addAttribute("itemToEdit", testItemDto);

        return "redirect:question";
    }

    @GetMapping("/assign-exam")
    public String assignExam(Model model) {
        List<Exam> examList = examRepository.findByUserListIsNotNull();
        model.addAttribute("exams", examList);
        model.addAttribute("assignExam", new AssignExamDto());
        return "teacher/assign-exam";
    }

    @PostMapping("/assign-exam")
    public String assignExam(@ModelAttribute("exam") AssignExamDto assignExamDto) {
        Exam exam = examRepository.findById(assignExamDto.getExamId()).orElseThrow();
        User user = userRepository.findById(assignExamDto.getUserId()).orElseThrow();
        user.getExamList().add(exam);
        userRepository.save(user);
        return "redirect:assign-exam";
    }

    @PostMapping("/unassign/{userId}/{courseId}")
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

    @GetMapping("/score-card/{userId}/{examId}")
    public String viewExamResults(@PathVariable Long userId,
                                  @PathVariable Long examId,
                                  Model model) {
        ExamResult examResult = examResultRepository.findByUser_IdAndExam_Id(userId, examId).orElseThrow();
        model.addAttribute("examResult", examResult);
        return "teacher/view-score";
    }

    @PostMapping("/exam-result/delete/{resultId}")
    public String viewExamResults(@PathVariable Long resultId) {
        examResultRepository.deleteById(resultId);
        return "redirect:exam-results";
    }

    @GetMapping("summary")
    public String viewExamResultSummary(Model model) {
        List<SummaryDto> summaryDtos = examResultRepository.getExamResultSummary();
        List<ExamResultSummaryDto> examResultSummaryDtoList = summaryDtos.stream().map(summaryDto -> {
            ExamResultSummaryDto examResultSummaryDto = new ExamResultSummaryDto();
            examResultSummaryDto.setName(summaryDto.getName());
            examResultSummaryDto.setPassed(summaryDto.getPassed());
            examResultSummaryDto.setFailed(summaryDto.getFailed());
            return examResultSummaryDto;
        }).collect(Collectors.toList());
        model.addAttribute("summaryList", examResultSummaryDtoList);
        return "teacher/summary";
    }

}
