package b314.userservice.dto.registration;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Annotation to check password = repeatPassword
 * on UserRegistrationConfirmationDTO when Valid is called
 */
@Documented
@Constraint(validatedBy = PasswordConfirmationValidator.class)
@Target({ ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@interface PasswordConfirmed {

    String message() default "{PasswordConfirmed}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
