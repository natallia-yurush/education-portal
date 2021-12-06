package by.nyurush.portal.service.impl;

import by.nyurush.portal.entity.Course;
import by.nyurush.portal.entity.Exam;
import by.nyurush.portal.entity.User;
import by.nyurush.portal.entity.UserRole;
import by.nyurush.portal.exception.EntityAlreadyExistException;
import by.nyurush.portal.exception.PortalException;
import by.nyurush.portal.exception.RedisCodeNotFoundException;
import by.nyurush.portal.exception.user.UserAlreadyExistException;
import by.nyurush.portal.exception.user.UserAlreadyIsActiveException;
import by.nyurush.portal.exception.user.UserNotFoundException;
import by.nyurush.portal.repository.CourseRepository;
import by.nyurush.portal.repository.ExamRepository;
import by.nyurush.portal.repository.UserRepository;
import by.nyurush.portal.service.MailService;
import by.nyurush.portal.service.RedisService;
import by.nyurush.portal.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import java.security.SecureRandom;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private static final int PASSWORD_LENGTH = 15;

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final ExamRepository examRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisService redisService;
    private final MailService mailService;

    @Override
    @Transactional
    public User register(User user, HttpServletRequest request) {
        if (userRepository.existsByEmail(user.getEmail())) {
            log.warn("IN register - user with email: {} already exist", user.getEmail());
            throw new UserAlreadyExistException("User with email " + user.getEmail() + " is already exist");
        }
        if (userRepository.existsByUsername(user.getUsername())) {
            log.warn("IN register - user with username: {} already exist", user.getEmail());
            throw new UserAlreadyExistException("User with username " + user.getEmail() + " is already exist");
        }
        String password = generateRandomPassword(PASSWORD_LENGTH);
        user.setPassword(passwordEncoder.encode(password));
        user.setActive(false);
        User registeredUser = userRepository.save(user);

        String activationCode = generateCode();
        redisService.addCode(activationCode, user.getEmail());
        mailService.sendConfirmationEmail(user.getEmail(), activationCode, user.getUsername(), password, request);

        log.info("IN register - user: {} successfully registered", registeredUser);
        return registeredUser;
    }

    @Override
    @Transactional
    public void confirmUser(String hashCode) {
        if (redisService.isCodeExist(hashCode)) {
            String email = redisService.getValue(hashCode);
            User user = userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);

            if (!user.isActive()) {
                user.setActive(true);
                userRepository.save(user);
            } else {
                log.warn("IN confirmUser - user with email {} is already active", email);
                throw new UserAlreadyIsActiveException();
            }
        } else {
            log.warn("IN confirmUser - failed to confirm user: hash code {} not found.", hashCode);
            throw new RedisCodeNotFoundException("Failed to confirm user: hash code not found.");
        }
    }

    @Override
    @Transactional
    public void resetPassword(String email, HttpServletRequest request) {
        userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);

        String codeToReset = generateCode();
        mailService.sendResetPasswordEmail(email, codeToReset, request);
        redisService.addCode(codeToReset, email);

        log.info("IN resetPassword - mail sent to user {}", email);
    }

    @Override
    @Transactional
    public void updatePassword(String code, String newPassword) {
        String email = redisService.getValue(code);
        if (email == null || email.isBlank()) {
            log.warn("IN updatePassword - there is no such code in redis: {}", code);
            throw new RuntimeException("Code " + code + " in redis not found");
        }
        if (redisService.isValidCode(code, email)) {
            User user = userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
            user.setPassword(passwordEncoder.encode(newPassword));

        } else {
            log.warn("IN updatePassword - there is no code in redis {}", code);
            throw new RedisCodeNotFoundException();
        }
    }

    @Override
    public void assignTeacher(Long userId, Long courseId) {
        Course course = courseRepository.findById(courseId).orElseThrow(EntityNotFoundException::new); //todo
        User user = userRepository.findById(userId).orElseThrow(EntityNotFoundException::new);
        if(!user.getRole().equals(UserRole.ROLE_TEACHER)) {
            throw new PortalException("error.is.not.teacher");
        }

        if (!user.getCourseList().add(course)) {
            throw new EntityAlreadyExistException("The object already exists in the collection.");
        }
        userRepository.save(user);
    }

    @Override
    public void unassignTeacher(Long userId, Long courseId) {
        Course course = courseRepository.findById(courseId).orElseThrow(EntityNotFoundException::new); //todo
        User user = userRepository.findById(userId).orElseThrow(EntityNotFoundException::new);

        user.getCourseList().remove(course);
        userRepository.save(user);
    }

    @Override
    public void unassignExam(Long userId, Long examId) {
        Exam exam = examRepository.findById(examId).orElseThrow(EntityNotFoundException::new);
        User user = userRepository.findById(userId).orElseThrow(EntityNotFoundException::new);

        user.getExamList().remove(exam);
        userRepository.save(user);
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(EntityNotFoundException::new); //todo add exception
    }

    @Override
    public List<User> findAllStudents() {
        return userRepository.findAllByRole(UserRole.ROLE_STUDENT);
    }

    @Override
    public List<User> findAllTeachers() {
        return userRepository.findAllByRole(UserRole.ROLE_TEACHER);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(UserNotFoundException::new);
    }

    @Override
    public User findByEmailOrUsername(String credentials) {
        return userRepository.findByEmailOrUsername(credentials, credentials)
                .orElseThrow(UserNotFoundException::new);
    }

    private String generateCode() {
        return UUID.randomUUID().toString();
    }

    private static String generateRandomPassword(int len) {
        final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789~`!@#$%^&*()-_=+[{]}\\|;:\'\",<.>/?";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            int randomIndex = random.nextInt(chars.length());
            sb.append(chars.charAt(randomIndex));
        }
        return sb.toString();
    }
}

