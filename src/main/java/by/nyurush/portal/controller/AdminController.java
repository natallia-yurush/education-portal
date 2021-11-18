package by.nyurush.portal.controller;

import by.nyurush.portal.dto.UserDto;
import by.nyurush.portal.entity.User;
import by.nyurush.portal.entity.UserRole;
import by.nyurush.portal.service.UserService;
import lombok.AllArgsConstructor;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Controller()
@RequestMapping(value = "/admin")
@AllArgsConstructor
public class AdminController {

    private final UserService userService;
    private final ConversionService conversionService;

    @GetMapping("/students")
    public String addStudent(Model model) {
        model.addAttribute("user", new UserDto());
        model.addAttribute("students", userService.findAllStudents());
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
        // todo check if user exist
        User user = conversionService.convert(userDto, User.class);
        user.setRole(UserRole.ROLE_STUDENT);
        userService.saveUser(user);

        return "redirect:students";
    }

    @PostMapping("/teacher")
    public String addTeacher(@ModelAttribute("user") UserDto userDto) {
        // todo check if user exist
        User user = conversionService.convert(userDto, User.class);
        user.setRole(UserRole.ROLE_TEACHER);
        userService.saveUser(user);

        return "redirect:teachers";
    }

    @GetMapping("/user/image/{id}")
    public void showProductImage(@PathVariable String id,
                                 HttpServletResponse response) throws IOException {
        response.setContentType("image/jpeg"); // Or whatever format you wanna use

        User user = userService.findById(Long.getLong(id));

        InputStream is = new ByteArrayInputStream(user.getPhoto());
        IOUtils.copy(is, response.getOutputStream());
    }

    @GetMapping("/image/display/{id}")
    @ResponseBody
    void showImage(@PathVariable("id") String id, HttpServletResponse response) throws IOException {
        User user = userService.findById(Long.getLong(id));
        response.setContentType("image/jpeg");
        response.getOutputStream().write(user.getPhoto());
        response.getOutputStream().close();
    }

    @PostMapping("/user/delete/{id}")
    public String deleteUser(@PathVariable("id") String id) {
        userService.deleteById(Long.getLong(id));
        return "redirect:admin/students";
    }

}
