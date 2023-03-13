package b314.userservice.controller.login;

import b314.userservice.controller.Message;
import b314.userservice.dto.login.LoginDTO;
import b314.userservice.service.login.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * Controller to handle authentication
 */
@RestController
public class LoginController {

    private final LoginService loginService;

    @Autowired
    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    /**
     * POST request handler
     * Logins user with given credentials
     * @param login LoginDTO instance with username/password
     * @return JWT token for given credentials
     */
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginDTO login,
                                                                  Errors errors) {
        // Checking for validation errors
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest()
                                 .body(new Message("Invalid login data"));
        }

        // Responding
        return ResponseEntity.ok(loginService.authenticateUser(login));
    }

}
