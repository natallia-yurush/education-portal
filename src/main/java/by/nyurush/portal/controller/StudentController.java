package by.nyurush.portal.controller;

import by.nyurush.portal.entity.Answer;
import by.nyurush.portal.entity.Question;
import by.nyurush.portal.entity.UserAnswer;
import by.nyurush.portal.repository.QuestionRepository;
import by.nyurush.portal.repository.UserAnswerRepository;
import lombok.AllArgsConstructor;
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
    private final UserAnswerRepository userAnswerRepository;

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

        if(!questionList.isEmpty()) {
            Question question = questionList.get(0);

            QuestionDto questionDto = new QuestionDto();
            questionDto.setText(question.getText());
            questionDto.setAnswerDtos(question.getAnswerList().stream()
                    .map(answer -> {
                        AnswerDto answerDto = new AnswerDto();
                        answerDto.setText(answer.getText());
                        return answerDto;
                    })
                    .collect(Collectors.toList()));
            model.addAttribute("question", questionDto);
            model.addAttribute("examId", examId);
            return "student/test";
        } else {
            return ""; //todo result
        }
    }

    @PostMapping("/test/{examId}")
    public String answerQuestion(@PathVariable Long examId, @ModelAttribute() QuestionDto question) {


        return "redirect:"+examId;
    }
}
