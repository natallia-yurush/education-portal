package by.nyurush.portal.repository;

import by.nyurush.portal.entity.UserAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserAnswerRepository extends JpaRepository<UserAnswer, Long> {

    Optional<UserAnswer> findByExam_IdAndUser_IdAndQuestion_Id(Long examId, Long userId, Long questionId);
    List<UserAnswer> findAllByExam_IdAndUser_Id(Long examId, Long userId);
}
