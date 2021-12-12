package by.nyurush.portal.dto;

import lombok.Data;

import java.util.List;

@Data
public class QuestionDto {

    private String text;

    private List<UserAnswerDto> answerDtos;
}
