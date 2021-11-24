package by.nyurush.portal.repository;

import by.nyurush.portal.entity.Exam;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExamRepository extends JpaRepository<Exam, Long> {
    List<Exam> findByUserListIsNotNull();
}
