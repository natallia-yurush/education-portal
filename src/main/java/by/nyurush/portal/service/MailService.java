package by.nyurush.portal.service;

public interface MailService {

    void sendConfirmationEmail(String email, String code);

    void sendResetPasswordEmail(String email, String code);
}
