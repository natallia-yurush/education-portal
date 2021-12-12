package by.nyurush.portal.service;

import by.nyurush.portal.dto.QuestionDto;
import by.nyurush.portal.entity.User;
import by.nyurush.portal.entity.UserAnswer;

public interface UserAnswerService {

    UserAnswer save(QuestionDto questionDto, Long examId, User user);
}
