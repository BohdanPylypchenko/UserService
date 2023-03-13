package b314.userservice.dto.registration;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * Data transfer object for registration
 */
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@PasswordConfirmed
public class UserRegistrationDTO {

    @NotNull
    @Email
    private String email;

    @NotNull
    private String password;

    @NotNull
    private String repeatPassword;

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;

    @NotNull
    private int age;

    @NotNull
    private String driverLicense;

    @NotEmpty
    private String[] roles;

}
