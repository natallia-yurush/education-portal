package by.nyurush.portal.repository;

import by.nyurush.portal.entity.Exam;
import by.nyurush.portal.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface ExamRepository extends JpaRepository<Exam, Long> {
    Set<Exam> findByUserListIsNotNull();
    List<Exam> findByUserListContains(User user);
}
