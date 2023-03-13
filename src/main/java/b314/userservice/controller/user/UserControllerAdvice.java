package b314.userservice.controller.user;

import b314.userservice.controller.Message;
import b314.userservice.exception.AnonymousAccessForbiddenException;
import b314.userservice.exception.user.AuthorizedIdNotSameAsRequestedException;
import b314.userservice.exception.user.EmailUsedByOtherAccountException;
import b314.userservice.exception.user.InvalidUserStatusException;
import b314.userservice.exception.user.NegativeBalanceException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice(assignableTypes = {UserController.class})
public class UserControllerAdvice {

    @ExceptionHandler(AnonymousAccessForbiddenException.class)
    public ResponseEntity<?> classCastExceptionHandler() {
        return ResponseEntity.status(401)
                             .body(new Message("Anonymous access is not allowed"));
    }

    @ExceptionHandler(AuthorizedIdNotSameAsRequestedException.class)
    public ResponseEntity<?> authorizedIdNotSameAsRequestedExceptionHandler() {
        return ResponseEntity.status(403)
                             .body(new Message("Trying to access other user profile"));
    }

    @ExceptionHandler(EmailUsedByOtherAccountException.class)
    public ResponseEntity<?> emailUsedByOtherAccountExceptionHandler() {
        return ResponseEntity.status(400)
                             .body(new Message("Provided email is already used by another account."));
    }

    @ExceptionHandler(InvalidUserStatusException.class)
    public ResponseEntity<?> invalidUserStatusException() {
        return ResponseEntity.status(400)
                             .body(new Message("Invalid new user status. Update was canceled."));
    }

    @ExceptionHandler({javax.validation.ConstraintViolationException.class, MethodArgumentNotValidException.class})
    public ResponseEntity<?> constraintViolationExceptionHandler() {
        return ResponseEntity.status(400)
                             .body(new Message("Invalid data"));
    }

    @ExceptionHandler(NegativeBalanceException.class)
    public ResponseEntity<?> negativeBalanceExceptionHandler(NegativeBalanceException ex) {
        return ResponseEntity.status(400)
                             .body(new Message(ex.getMessage()));
    }

}
