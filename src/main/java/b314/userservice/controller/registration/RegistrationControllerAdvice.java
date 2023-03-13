package b314.userservice.controller.registration;

import b314.userservice.controller.Message;
import b314.userservice.exception.registration.MaxUserRegistrationConfirmationTokensCountReached;
import b314.userservice.exception.registration.NoSuchUserRegistrationConfirmationTokenException;
import b314.userservice.exception.registration.UserRegistrationConfirmationTokenExpiredException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * Global controller advice for all controllers of microservice
 */
@ControllerAdvice(assignableTypes = {RegistrationController.class})
public class RegistrationControllerAdvice {

    @ExceptionHandler(NoSuchUserRegistrationConfirmationTokenException.class)
    public ResponseEntity<?> noSuchUserRegistrationConfirmationTokenExceptionHandler() {
        return ResponseEntity.status(400)
                             .body(new Message("Corresponding token does not exist!"));
    }

    @ExceptionHandler(MaxUserRegistrationConfirmationTokensCountReached.class)
    public ResponseEntity<?> expiredUserRegistrationConfirmationTokenExceptionHandler() {
        return ResponseEntity.status(400)
                             .body(new Message("Sorry, max failed confirmation attempts count was reached."));
    }

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<?> sqlIntegrityConstraintViolationExceptionHandler() {
        return ResponseEntity.status(400)
                             .body(new Message("Account with given email already exists!"));
    }

    @ExceptionHandler(UserRegistrationConfirmationTokenExpiredException.class)
    public ResponseEntity<?> tokenExpiredExceptionHandler() {
        return ResponseEntity.status(400)
                             .body(new Message("Sorry, your confirmation token expired. Another confirmation email was sent to you."));
    }

}
