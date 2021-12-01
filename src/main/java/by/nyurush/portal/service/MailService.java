package by.nyurush.portal.service;

public interface MailService {

    void sendConfirmationEmail(String email, String code, String username, String password);

    void sendResetPasswordEmail(String email, String code);
}
