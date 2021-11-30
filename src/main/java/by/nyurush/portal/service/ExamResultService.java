package by.nyurush.portal.service;

import by.nyurush.portal.dto.ExamResultDetailsDto;
import by.nyurush.portal.dto.ExamResultSummaryDto;
import by.nyurush.portal.entity.ExamResult;
import by.nyurush.portal.entity.User;

import java.util.List;

public interface ExamResultService {

    ExamResult calculateTestResult(User user, Long ExamId);

    List<ExamResultDetailsDto> getExamResultDetails(Long examResultId);

    List<ExamResultSummaryDto> getExamResultSummaryDtoList();

    List<String> getExamNameList();

    List<Integer> getPassedList();

    List<Integer> getFailedList();
}
