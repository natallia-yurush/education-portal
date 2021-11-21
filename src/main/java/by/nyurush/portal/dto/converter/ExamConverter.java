package by.nyurush.portal.dto.converter;

import by.nyurush.portal.dto.ExamDto;
import by.nyurush.portal.entity.Exam;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class ExamConverter implements Converter<ExamDto, Exam> {

    @Override
    public Exam convert(ExamDto examDto) {
        Exam exam = new Exam();
        exam.setId(examDto.getId());
        exam.setName(examDto.getName());
        exam.setInstruction(examDto.getInstruction());
        exam.setStartDate(parseDate(examDto.getStartDate()));
        exam.setEndDate(parseDate(examDto.getEndDate()));
        exam.setTimerInMinutes(examDto.getTimerInMinutes());
        return exam;
    }

    private Date parseDate(String dateValue) {
        final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return formatter.parse(dateValue);
        } catch (ParseException e) {
            final String message = "Exception in date parsing.";
            throw new RuntimeException(message); //todo add custom
        }
    }
}
