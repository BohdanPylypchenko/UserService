package b314.userservice.controller.registration;

import b314.userservice.controller.Message;
import b314.userservice.dto.registration.UserRegistrationDTO;
import b314.userservice.entity.user.User;
import b314.userservice.exception.registration.MaxUserRegistrationConfirmationTokensCountReached;
import b314.userservice.exception.registration.UserRegistrationConfirmationTokenExpiredException;
import b314.userservice.service.registration.UserRegistratorService;
import b314.userservice.service.user.UserDtoConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;

/**
 * Controller to handle registration
 */
@RestController
public class RegistrationController {

    private final UserRegistratorService registrator;

    private final UserDtoConverter userDtoConverter;

    @Autowired
    public RegistrationController(UserRegistratorService registrator,
                                  UserDtoConverter userDtoConverter) {
        this.registrator = registrator;
        this.userDtoConverter = userDtoConverter;
    }

    /**
     * POST request handler
     * @param userRegistrationDTO Object, which contains all registration info
     * @return creation link with user
     */
    @PostMapping("/registration")
    public ResponseEntity<?> register(@Valid @RequestBody UserRegistrationDTO userRegistrationDTO,
                                                          Errors errors) {
        // Checking for validation errors
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest()
                                 .body(new Message("Invalid registration data"));
        }

        // Registering user
        var user = registrator.registerUserUnconfirmed(userRegistrationDTO);

        // Building location
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                                                  .path("/{id}")
                                                  .buildAndExpand(user.getId())
                                                  .toUri();

        // Returning
        return ResponseEntity.created(location).body(user);
    }

    /**
     * GET request handler
     * Handles user's confirmation of registration
     * (link from confirmation email)
     * @param rawToken raw user registration confirmation Token, initially sent in email
     * @return confirmed user info
     */
    @GetMapping("/registration-confirm")
    public ResponseEntity<?> confirmRegistration(@RequestParam("token") @NotNull String rawToken)
            throws UserRegistrationConfirmationTokenExpiredException, MaxUserRegistrationConfirmationTokensCountReached {
        // Processing
        User user = registrator.processUserRegistrationConfirmation(rawToken);

        // Responding
        return ResponseEntity.ok().body(userDtoConverter.user2UserRegistrationConfirmationDto(user));
    }

}
