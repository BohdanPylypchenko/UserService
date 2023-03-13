package b314.userservice.controller.login;

import b314.userservice.controller.Message;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice(assignableTypes = {LoginController.class})
public class LoginControllerAdvice {

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> badCredentialsExceptionHandler() {
        return ResponseEntity.status(401)
                             .body(new Message("Wrong email or password"));
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<?> disabledExceptionHandler() {
        return ResponseEntity.status(401)
                             .body(new Message("Your account has not been confirmed. Check your email."));
    }

}
