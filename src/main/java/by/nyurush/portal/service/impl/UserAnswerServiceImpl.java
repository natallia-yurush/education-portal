package by.nyurush.portal.service.impl;

import by.nyurush.portal.dto.QuestionDto;
import by.nyurush.portal.dto.converter.UserAnswerConverter;
import by.nyurush.portal.entity.Exam;
import by.nyurush.portal.entity.UserAnswer;
import by.nyurush.portal.repository.ExamRepository;
import by.nyurush.portal.repository.UserAnswerRepository;
import by.nyurush.portal.repository.UserRepository;
import by.nyurush.portal.service.UserAnswerService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class UserAnswerServiceImpl implements UserAnswerService {

    private final ConversionService conversionService;
    private final ExamRepository examRepository;
    private final UserAnswerRepository userAnswerRepository;

    @Override
    public UserAnswer save(QuestionDto questionDto) {
        return null;
    }
}
