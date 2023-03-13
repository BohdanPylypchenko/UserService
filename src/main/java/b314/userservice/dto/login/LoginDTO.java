package b314.userservice.dto.login;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

/**
 * Data transfer object for login
 */
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class LoginDTO {

    @NotNull
    @Email
    private String email;

    @NotNull
    private String password;

}
