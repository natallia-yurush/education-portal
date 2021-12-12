package by.nyurush.portal.dto;

import by.nyurush.portal.entity.Answer;
import by.nyurush.portal.entity.Question;
import lombok.Data;

import java.util.Set;

@Data
public class ExamResultDetailsDto {

    private Question question;

    private Set<Answer> selectedAnswers;
}
