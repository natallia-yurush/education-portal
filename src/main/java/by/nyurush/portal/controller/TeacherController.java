package by.nyurush.portal.controller;

import by.nyurush.portal.dto.ExamDto;
import by.nyurush.portal.entity.Exam;
import by.nyurush.portal.repository.ExamRepository;
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
@RequestMapping(value = "/teacher")
@AllArgsConstructor
public class TeacherController {

    private ExamRepository examRepository;
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

}
