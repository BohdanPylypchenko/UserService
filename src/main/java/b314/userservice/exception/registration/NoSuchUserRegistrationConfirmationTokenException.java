package b314.userservice.exception.registration;

import java.util.NoSuchElementException;

/**
 * Exception to through when
 * token was not loaded from db
 */
public class NoSuchUserRegistrationConfirmationTokenException extends NoSuchElementException {

    public NoSuchUserRegistrationConfirmationTokenException(String s) {
        super(s);
    }

}
