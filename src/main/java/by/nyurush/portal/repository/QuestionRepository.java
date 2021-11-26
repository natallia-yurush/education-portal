package by.nyurush.portal.repository;

import by.nyurush.portal.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    List<Question> findByExam_Id(Long id);
    Optional<Question> findByText(String text);
}
