package b314.userservice.dto.user;

import b314.userservice.entity.role.Role;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * User profile data transfer object
 */
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@EqualsAndHashCode
public class UserProfileDto {

    @NotNull
    private int id;

    @NotNull
    @Email
    private String email;

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;

    @NotNull
    @Min(18)
    private int age;

    @NotNull
    private String driverLicense;

    @NotEmpty
    private List<Role> roles;

    @NotNull
    private boolean enabled;

    @NotNull
    @Builder.Default
    private float balance = 0.0f;

}
