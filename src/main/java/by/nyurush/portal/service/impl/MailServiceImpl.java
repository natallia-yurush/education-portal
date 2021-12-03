package by.nyurush.portal.service.impl;

import by.nyurush.portal.service.MailService;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.jni.Local;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

@Service
@RequiredArgsConstructor
@PropertySource("classpath:messages.properties")
public class MailServiceImpl implements MailService {

    private final JavaMailSender emailSender;

    @Value("${mail.username}")
    private String emailFrom;
    @Value("${mail.link.confirm}")
    private String linkToConfirm;
    @Value("${mail.link.reset}")
    private String linkToReset;

    private final LocaleResolver localeResolver;

    private void sendEmail(String email, String messageText, String messageType) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject(messageType);
        message.setFrom(emailFrom);
        message.setTo(email);
        message.setText(messageText);
        emailSender.send(message);
    }

    @Override
    public void sendConfirmationEmail(String email, String code, String username, String password, HttpServletRequest request) {
        Locale locale = localeResolver.resolveLocale(request);
        String confirmMessage = ResourceBundle.getBundle("messages", locale)
                .getString("mail.confirm.message");
        String messageText = confirmMessage +
                linkToConfirm + code;
        String confirmPasswordMessage = ResourceBundle.getBundle("messages", locale)
                .getString("mail.confirm.password.message");
        String confirmType = ResourceBundle.getBundle("messages", locale)
                .getString("mail.confirm");
        String credentialMessage = MessageFormat.format(confirmPasswordMessage, username, password);
        String message = messageText + credentialMessage + linkToReset + code;
        sendEmail(email, message, confirmType);
    }

    @Override
    public void sendResetPasswordEmail(String email, String code, HttpServletRequest request) {
        Locale locale = localeResolver.resolveLocale(request);
        String resetPasswordMessage = ResourceBundle.getBundle("messages", locale)
                .getString("mail.reset.password.message");
        String resetPasswordType = ResourceBundle.getBundle("messages", locale)
                .getString("mail.reset.password");
        String messageText = resetPasswordMessage + linkToReset + code;
        sendEmail(email, messageText, resetPasswordType);
    }
}
