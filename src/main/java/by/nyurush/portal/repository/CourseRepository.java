package by.nyurush.portal.repository;

import by.nyurush.portal.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {
}
