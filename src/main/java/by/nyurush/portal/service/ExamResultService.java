package by.nyurush.portal.service;

import by.nyurush.portal.dto.ExamResultDetailsDto;
import by.nyurush.portal.dto.ExamResultSummaryDto;
import by.nyurush.portal.entity.ExamResult;

import java.util.List;

public interface ExamResultService {

    ExamResult calculateTestResult(Long ExamId);

    List<ExamResultDetailsDto> getExamResultDetails(Long examResultId);

    List<ExamResultSummaryDto> getExamResultSummaryDtoList();

    List<String> getExamNameList();

    List<Integer> getPassedList();

    List<Integer> getFailedList();
}
