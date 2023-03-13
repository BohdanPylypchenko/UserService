package b314.userservice.service.login;

import b314.userservice.dto.login.LoginDTO;
import b314.userservice.dto.login.LoginResponseDto;
import b314.userservice.security.JwtProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Implements login functionality
 */
@Component
public class LoginService {

    private final AuthenticationManager authenticationManager;

    private final JwtProvider tokenProvider;

    public LoginService(AuthenticationManager authenticationManager, JwtProvider tokenProvider) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

    /**
     * Creates login response dto
     * (email + token) for given login
     */
    public LoginResponseDto authenticateUser(LoginDTO login) {
        // Authenticating user (check if there is user with username / password is correct)
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(login.getEmail(), login.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Returning
        return LoginResponseDto.builder()
                               .email(login.getEmail())
                               .token(tokenProvider.generateToken(authentication))
                               .build();
    }

}
