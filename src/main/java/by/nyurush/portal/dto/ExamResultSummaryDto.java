package by.nyurush.portal.dto;

import lombok.Data;

@Data
public class ExamResultSummaryDto {
    private String name;
    private int passed;
    private int failed;
}
