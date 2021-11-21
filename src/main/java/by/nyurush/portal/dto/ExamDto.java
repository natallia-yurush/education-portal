package by.nyurush.portal.dto;

import lombok.Data;

@Data
public class ExamDto {

    private Long id;

    private String name;

    private String instruction;

    private String startDate;

    private String endDate;

    private Long timerInMinutes;
}
