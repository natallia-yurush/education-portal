package by.nyurush.portal.repository;

import by.nyurush.portal.entity.UserAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserAnswerRepository extends JpaRepository<UserAnswer, Long> {

    List<UserAnswer> findAllByExam_IdAndQuestion_Id(Long examId, Long questionId);
    List<UserAnswer> findAllByExam_IdAndUser_Id(Long examId, Long userId);
}
