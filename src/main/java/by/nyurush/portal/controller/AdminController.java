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
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Base64;

import static by.nyurush.portal.entity.UserRole.ROLE_STUDENT;
import static by.nyurush.portal.entity.UserRole.ROLE_TEACHER;

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

    @GetMapping("/index")
    public String getIndex(Model model) {
        model.addAttribute("examNameList", examResultService.getExamNameList());
        model.addAttribute("passedList", examResultService.getPassedList());
        model.addAttribute("failedList", examResultService.getFailedList());

        model.addAttribute("numberOfStudents", userRepository.countUserByRole(ROLE_STUDENT));
        model.addAttribute("numberOfTeachers", userRepository.countUserByRole(ROLE_TEACHER));
        model.addAttribute("numberOfCourses", courseRepository.count());

        return "admin/index";
    }

    @GetMapping(value = "/students", produces = MediaType.IMAGE_JPEG_VALUE)
    public String addStudent(Model model) {
        model.addAttribute("user", new UserDto());
        model.addAttribute("students", userService.findAllStudents());
        model.addAttribute("imgUtil", new ImageUtil());
        return "admin/student";
    }

    @GetMapping("/teachers")
    public String addTeacher(Model model) {
        model.addAttribute("user", new UserDto());
        model.addAttribute("teachers", userService.findAllTeachers());
        return "admin/teacher";
    }

    @PostMapping("/student")
    public String addStudent(@ModelAttribute("user") UserDto userDto) {
        User user = conversionService.convert(userDto, User.class);
        userDataValidator.validate(user);
        user.setRole(ROLE_STUDENT);
        userService.register(user);

        return "redirect:students";
    }

    @PostMapping("/teacher")
    public String addTeacher(@ModelAttribute("user") UserDto userDto) {
        User user = conversionService.convert(userDto, User.class);
        userDataValidator.validate(user);
        user.setRole(UserRole.ROLE_TEACHER);
        userService.register(user);

        return "redirect:teachers";
    }

    @GetMapping("/courses")
    public String courses(Model model) {
        model.addAttribute("course", new CourseDto());
        model.addAttribute("courses", courseRepository.findAll());

        return "admin/course";
    }

    @PostMapping("/course")
    public String addCourse(@ModelAttribute("course") CourseDto courseDto) {
        Course course = conversionService.convert(courseDto, Course.class);
        courseRepository.save(course);

        return "redirect:courses";
    }

    @GetMapping("/assign-teacher")
    public String assignTeacher(Model model) {
        model.addAttribute("assignTeacher", new AssignTeacherDto());
        model.addAttribute("courses", courseRepository.findAll());
        return "admin/assign-teacher";
    }

    @PostMapping("/assign-teacher")
    public String assignTeacher(@ModelAttribute("assignTeacher") AssignTeacherDto assignTeacherDto) {
        userService.assignTeacher(assignTeacherDto.getUserId(), assignTeacherDto.getCourseId());
        return "redirect:assign-teacher";
    }

    @GetMapping("/user/image/{id}")
    public void showProductImage(@PathVariable Long id, Model model) {
        User user = userService.findById(id);
        String base64 = Base64.getMimeEncoder().encodeToString(user.getPhoto());
        model.addAttribute("avatar" + id, base64);
    }

    @GetMapping(value = "/image/display/{id}", produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody
    void showImage(@PathVariable("id") String id, HttpServletResponse response) throws IOException {
        User user = userService.findById(Long.getLong(id));
        response.setContentType("image/jpeg");
        response.getOutputStream().write(user.getPhoto());
        response.getOutputStream().close();
    }

    @PostMapping("/user/delete/{id}")
    public String deleteUser(@PathVariable("id") Long id) {
        userRepository.deleteById(id);
        return "redirect:students";
    }

    @PostMapping("/course/delete/{id}")
    public String deleteCourse(@PathVariable("id") Long id) {
        courseRepository.deleteById(id);
        return "redirect:/admin/courses";
    }

    @PostMapping("/unassign/{userId}/{courseId}")
    public String unassignTeacher(@PathVariable("userId") Long userId,
                                  @PathVariable("courseId") Long courseId) {
        userService.unassignTeacher(userId, courseId);
        return "redirect:/admin/assign-teacher";
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


