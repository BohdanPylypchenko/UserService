package b314.userservice.exception.registration;

/**
 * Exception to through when
 * user registration confirmation token expired (and nothing more)
 */
public class UserRegistrationConfirmationTokenExpiredException extends Exception {

    public UserRegistrationConfirmationTokenExpiredException(String message) {
        super(message);
    }

}
