package by.nyurush.portal.service;

import by.nyurush.portal.entity.User;

import java.util.List;

public interface UserService {

    User findById(Long id);

    List<User> findAllStudents();

    List<User> findAllTeachers();

    User findByEmail(String email);

    User findByUsername(String username);

    User findByEmailOrUsername(String credentials);

    User register(User user);

    void confirmUser(String hashCode);

    void resetPassword(String email);

    void updatePassword(String code, String newPassword);

    void assignTeacher(Long userId, Long courseId);

    void unassignTeacher(Long userId, Long courseId);

    void unassignExam(Long userId, Long examId);

    User save(User user);

}
