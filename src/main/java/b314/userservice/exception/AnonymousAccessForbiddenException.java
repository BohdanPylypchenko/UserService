package b314.userservice.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * Exception to through when anonymous authentication
 * is not allowed
 */
public class AnonymousAccessForbiddenException extends AuthenticationException {

    public AnonymousAccessForbiddenException(String msg) {
        super(msg);
    }

}
