package by.nyurush.portal.util;

import lombok.AllArgsConstructor;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;
import java.util.ResourceBundle;

@AllArgsConstructor
public class MessageUtil {

    public static String getMessage(LocaleResolver resolver, HttpServletRequest request, String messageKey) {
        Locale locale = resolver.resolveLocale(request);
        return ResourceBundle.getBundle("messages", locale)
                .getString(messageKey);
    }
}
