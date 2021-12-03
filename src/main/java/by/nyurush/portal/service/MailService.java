package by.nyurush.portal.service;

import javax.servlet.http.HttpServletRequest;

public interface MailService {

    void sendConfirmationEmail(String email, String code, String username, String password, HttpServletRequest request);

    void sendResetPasswordEmail(String email, String code, HttpServletRequest request);
}
