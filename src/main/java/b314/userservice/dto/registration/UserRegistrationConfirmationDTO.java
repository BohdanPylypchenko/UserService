package b314.userservice.dto.registration;

import b314.userservice.entity.role.Role;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Data transfer object implementation for User
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@EqualsAndHashCode
public class UserRegistrationConfirmationDTO {

    @NotNull
    @Email
    private String email;

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;

    @NotEmpty
    List<Role> roles;

}
