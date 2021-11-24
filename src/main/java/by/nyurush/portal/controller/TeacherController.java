package by.nyurush.portal.controller;

import by.nyurush.portal.dto.AnswerDto;
import by.nyurush.portal.dto.ExamDto;
import by.nyurush.portal.dto.TestItemDto;
import by.nyurush.portal.entity.Exam;
import by.nyurush.portal.entity.Question;
import by.nyurush.portal.repository.ExamRepository;
import by.nyurush.portal.repository.QuestionRepository;
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


}
