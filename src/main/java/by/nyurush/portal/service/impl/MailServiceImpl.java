package by.nyurush.portal.service.impl;

import by.nyurush.portal.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@PropertySource("classpath:constants.properties")
public class MailServiceImpl implements MailService {
    private final JavaMailSender emailSender;

    @Value("${mail.confirm}")
    private String confirmType;
    @Value("${mail.reset.password}")
    private String resetPasswordType;
    @Value("${mail.confirm.message}")
    private String confirmMessage;
    @Value("${mail.reset.password.message}")
    private String resetPasswordMessage;
    @Value("${mail.username}")
    private String emailFrom;
    @Value("${mail.link.confirm}")
    private String linkToConfirm;
    @Value("${mail.link.reset}")
    private String linkToReset;


    private void sendEmail(String email, String messageText, String messageType) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject(messageType);
        message.setFrom(emailFrom);
        message.setTo(email);
        message.setText(messageText);
        emailSender.send(message);
    }

    @Override
    public void sendConfirmationEmail(String email, String code) {
        String messageText = confirmMessage +
                linkToConfirm + code;
        sendEmail(email, messageText, confirmType);
    }

    @Override
    public void sendResetPasswordEmail(String email, String code) {
        String messageText = resetPasswordMessage + linkToReset + code;
        sendEmail(email, messageText, resetPasswordType);
    }
}
