package by.nyurush.portal.dto.converter;

import by.nyurush.portal.dto.ExamDto;
import by.nyurush.portal.entity.Exam;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class ExamDtoConverter implements Converter<Exam, ExamDto> {

    @Override
    public ExamDto convert(Exam exam) {
        ExamDto examDto = new ExamDto();
        examDto.setId(exam.getId());
        examDto.setName(exam.getName());
        examDto.setInstruction(exam.getInstruction());
        examDto.setStartDate(parseDate(exam.getStartDate()));
        examDto.setEndDate(parseDate(exam.getEndDate()));
        examDto.setTimerInMinutes(exam.getTimerInMinutes());
        return examDto;
    }

    private String parseDate(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(date);
    }
}
