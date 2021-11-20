package by.nyurush.portal.dto.converter;

import by.nyurush.portal.dto.CourseDto;
import by.nyurush.portal.entity.Course;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CourseConverter implements Converter<CourseDto, Course> {


    @Override
    public Course convert(CourseDto courseDto) {
        Course course = new Course();
        course.setName(courseDto.getName());
        course.setDescription(courseDto.getDescription());
        return course;
    }
}
