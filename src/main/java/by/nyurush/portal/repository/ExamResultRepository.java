package by.nyurush.portal.repository;

import by.nyurush.portal.dto.SummaryDto;
import by.nyurush.portal.entity.ExamResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ExamResultRepository extends JpaRepository<ExamResult, Long> {
    List<ExamResult> findAllByUser_Id(Long userId);

    Optional<ExamResult> findByUser_IdAndExam_Id(Long userId, Long examId);

    @Query(value = "SELECT exam.name as name, " +
            "count(nullif(passed, false)) as passed, " +
            "count(nullif(passed, true)) as failed " +
            "FROM public.exam_result " +
            "JOIN public.exam ON exam.id = exam_result.exam_id " +
            "GROUP BY exam.name",
            nativeQuery = true)
    List<SummaryDto> getExamResultSummary();
}
