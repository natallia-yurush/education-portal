package by.nyurush.portal.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.List;

@NoArgsConstructor
@Getter
public class PortalException extends RuntimeException {

    private String messageKey;

    private List<Object> args;

    private HttpStatus httpStatus;

    public PortalException(final String messageKey, final HttpStatus httpStatus, final List<Object> args) {
        this.messageKey = messageKey;
        this.args = args;
        this.httpStatus = httpStatus;
    }

    public PortalException(final String messageKey, final HttpStatus httpStatus) {
        this.messageKey = messageKey;
        this.httpStatus = httpStatus;
    }

    public PortalException(final String messageKey) {
        this.messageKey = messageKey;
    }
}
