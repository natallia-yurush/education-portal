package by.nyurush.portal.controller;

import by.nyurush.portal.dto.AssignTeacherDto;
import by.nyurush.portal.dto.CourseDto;
import by.nyurush.portal.dto.UserDto;
import by.nyurush.portal.entity.Course;
import by.nyurush.portal.entity.User;
import by.nyurush.portal.entity.UserRole;
import by.nyurush.portal.repository.CourseRepository;
import by.nyurush.portal.repository.UserRepository;
import by.nyurush.portal.service.ExamResultService;
import by.nyurush.portal.service.UserService;
import by.nyurush.portal.validator.UserDataValidator;
import lombok.AllArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Base64;

import static by.nyurush.portal.entity.UserRole.ROLE_STUDENT;
import static by.nyurush.portal.entity.UserRole.ROLE_TEACHER;
import static by.nyurush.portal.util.Constants.COURSE;
import static by.nyurush.portal.util.Constants.COURSES;
import static by.nyurush.portal.util.Constants.ID;
import static by.nyurush.portal.util.Constants.IMG_UTIL;
import static by.nyurush.portal.util.Constants.INDEX;
import static by.nyurush.portal.util.Constants.REDIRECT;
import static by.nyurush.portal.util.Constants.STUDENT;
import static by.nyurush.portal.util.Constants.STUDENTS;
import static by.nyurush.portal.util.Constants.TEACHER;
import static by.nyurush.portal.util.Constants.TEACHERS;
import static by.nyurush.portal.util.Constants.USER;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.springframework.http.HttpHeaders.REFERER;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;

@Controller
@RequestMapping(value = "/admin")
@AllArgsConstructor
public class AdminController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final ConversionService conversionService;
    private final CourseRepository courseRepository;
    private final ExamResultService examResultService;
    private final UserDataValidator userDataValidator;

    @GetMapping(INDEX)
    public String getIndex(Model model) {
        model.addAttribute("examNameList", examResultService.getExamNameList());
        model.addAttribute("passedList", examResultService.getPassedList());
        model.addAttribute("failedList", examResultService.getFailedList());

        model.addAttribute("numberOfStudents", userRepository.countUserByRole(ROLE_STUDENT));
        model.addAttribute("numberOfTeachers", userRepository.countUserByRole(ROLE_TEACHER));
        model.addAttribute("numberOfCourses", courseRepository.count());

        return "admin/index";
    }

    @GetMapping(value = STUDENTS, produces = IMAGE_JPEG_VALUE)
    public String addStudent(Model model) {
        model.addAttribute(USER, new UserDto());
        model.addAttribute(STUDENTS, userService.findAllStudents());
        model.addAttribute(IMG_UTIL, new ImageUtil());
        return "admin/student";
    }

    @GetMapping(value = TEACHERS, produces = IMAGE_JPEG_VALUE)
    public String addTeacher(Model model) {
        model.addAttribute(USER, new UserDto());
        model.addAttribute(TEACHERS, userService.findAllTeachers());
        model.addAttribute(IMG_UTIL, new ImageUtil());
        return "admin/teacher";
    }

    @PostMapping(path = {TEACHER, STUDENT})
    public String addUser(@ModelAttribute(USER) UserDto userDto,
                          @RequestParam(value = "Uimage", required = false) MultipartFile file,
                          HttpServletRequest request) throws IOException {
        User user = conversionService.convert(userDto, User.class);
        if (nonNull(file)) {
            user.setPhoto(file.getBytes());
        }
        userDataValidator.validate(user);
        if (isNull(user.getId())) {
            if (request.getRequestURI().contains(TEACHER)) {
                user.setRole(UserRole.ROLE_TEACHER);
            } else {
                user.setRole(ROLE_STUDENT);
            }
            userService.register(user, request);
        } else {
            User existsUser = userService.findById(user.getId());
            user.setRole(existsUser.getRole());
            user.setActive(existsUser.isActive());
            user.setPhoto(existsUser.getPhoto());
            userService.save(user);
        }

        return REDIRECT + request.getHeader(REFERER);
    }

    @GetMapping(COURSES)
    public String courses(Model model) {
        model.addAttribute(COURSE, new CourseDto());
        model.addAttribute(COURSES, courseRepository.findAll());

        return "admin/course";
    }

    @PostMapping(COURSE)
    public String addCourse(@ModelAttribute(COURSE) CourseDto courseDto) {
        Course course = conversionService.convert(courseDto, Course.class);
        courseRepository.save(course);

        return REDIRECT + COURSES;
    }

    @GetMapping("/assign-teacher")
    public String assignTeacher(Model model) {
        model.addAttribute("assignTeacher", new AssignTeacherDto());
        model.addAttribute(COURSES, courseRepository.findAll());
        return "admin/assign-teacher";
    }

    @PostMapping("/assign-teacher")
    public String assignTeacher(@ModelAttribute("assignTeacher") AssignTeacherDto assignTeacherDto) {
        userService.assignTeacher(assignTeacherDto.getUserId(), assignTeacherDto.getCourseId());
        return  REDIRECT + "assign-teacher";
    }

    @PostMapping("/user/delete/{id}")
    public String deleteUser(@PathVariable(ID) Long id, HttpServletRequest request) {
        userRepository.deleteById(id);
        return REDIRECT + request.getHeader(REFERER);
    }

    @PostMapping("/course/delete/{id}")
    public String deleteCourse(@PathVariable(ID) Long id) {
        courseRepository.deleteById(id);
        return  REDIRECT + "/admin/courses";
    }

    @PostMapping("/unassign/{userId}/{courseId}")
    public String unassignTeacher(@PathVariable("userId") Long userId,
                                  @PathVariable("courseId") Long courseId) {
        userService.unassignTeacher(userId, courseId);
        return REDIRECT + "/admin/assign-teacher";
    }

    public static class ImageUtil {
        public String getImgData(byte[] byteData) {
            if (byteData != null) {
                return Base64.getMimeEncoder().encodeToString(byteData);
            }
            return null;
        }
    }
}


