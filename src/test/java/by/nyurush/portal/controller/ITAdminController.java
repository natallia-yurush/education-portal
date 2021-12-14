package by.nyurush.portal.controller;

import by.nyurush.portal.dto.CourseDto;
import by.nyurush.portal.dto.UserDto;
import by.nyurush.portal.entity.User;
import by.nyurush.portal.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.ResultActions;

import static by.nyurush.portal.builder.UserBuilder.buildUserDto;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.TEXT_HTML_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ITAdminController extends ITCommonAbstract {

    @Autowired
    private UserRepository userRepository;

    @Test
    void getIndex_shouldReturnView() throws Exception {
        // given
        String path = "/admin/index";

        // when
        ResultActions result = mockMvc.perform(get(path));

        // then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(TEXT_HTML_VALUE));
    }

    @Test
    void addStudent_shouldReturnView() throws Exception {
        // given
        String path = "/admin/students";

        // when
        ResultActions result = mockMvc.perform(get(path));

        // then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(TEXT_HTML_VALUE));
    }

    @Test
    void addTeacher_shouldReturnView() throws Exception {
        // given
        String path = "/admin/teachers";

        // when
        ResultActions result = mockMvc.perform(get(path));

        // then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(TEXT_HTML_VALUE));
    }

    @Test
    void addStudent_givenNotRegisteredStudent_shouldAdd() throws Exception {
        // given
        String path = "/admin/student";
        UserDto userDto = buildUserDto();
        when(userService.register(any(), any())).thenReturn(new User());
        // when
        ResultActions result = mockMvc.perform(post(path)
                .requestAttr("Uimage", buildMultipartFile())
                .flashAttr("user", userDto));

        // then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(TEXT_HTML_VALUE));
    }

    @Test
    void addStudent_givenRegisteredStudent_shouldAdd() throws Exception {
        // given
        String path = "/admin/student";
        UserDto userDto = buildUserDto();
        userDto.setId(1L);
        when(userService.findById(any())).thenReturn(new User());
        when(userService.save(any())).thenReturn(new User());
        // when
        ResultActions result = mockMvc.perform(post(path)
                .requestAttr("Uimage", buildMultipartFile())
                .flashAttr("user", userDto));

        // then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(TEXT_HTML_VALUE));
    }

    @Test
    void addCourse_shouldReturnView() throws Exception {
        // given
        String path = "/admin/courses";

        // when
        ResultActions result = mockMvc.perform(get(path));

        // then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(TEXT_HTML_VALUE));
    }

    @Test
    void addCourse_shouldAdd() throws Exception {
        // given
        String path = "/admin/course";

        // when
        ResultActions result = mockMvc.perform(post(path)
                .flashAttr("course", new CourseDto()));

        // then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(TEXT_HTML_VALUE));
    }

    public static MockMultipartFile buildMultipartFile() {
        return new MockMultipartFile(
                "file",
                "hello.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "Hello, World!".getBytes()
        );
    }
}
