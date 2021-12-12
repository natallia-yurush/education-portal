package by.nyurush.portal.service;

import by.nyurush.portal.dto.TestItemDto;
import by.nyurush.portal.entity.Question;

public interface QuestionService {

    Question saveQuestion(TestItemDto testItemDto);
}
