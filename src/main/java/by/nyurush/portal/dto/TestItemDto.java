package by.nyurush.portal.dto;

import lombok.Data;

import java.util.List;

@Data
public class TestItemDto {

    private Long examId;

    private String questionText;

    private List<AnswerDto> answers;

}
