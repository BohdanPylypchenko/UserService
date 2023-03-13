package b314.userservice.dto.login;

import lombok.*;

/**
 * Data transfer object for login responce
 */
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class LoginResponseDto {

    private String email;

    private String token;

}
