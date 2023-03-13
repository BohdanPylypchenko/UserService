package b314.userservice.dto.registration;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Class to implement PasswordConfirmed validation
 * functionality
 */
class PasswordConfirmationValidator implements
        ConstraintValidator<PasswordConfirmed, UserRegistrationDTO> {

    @Override
    public void initialize(PasswordConfirmed constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(UserRegistrationDTO value, ConstraintValidatorContext context) {
        // Checking if password = repeatPassword
        return value.getPassword().equals(value.getRepeatPassword());
    }

}
