package b314.userservice.service.registration;

import b314.userservice.dto.registration.UserRegistrationDTO;
import b314.userservice.entity.user.User;
import b314.userservice.exception.registration.MaxUserRegistrationConfirmationTokensCountReached;
import b314.userservice.exception.registration.NoSuchUserRegistrationConfirmationTokenException;
import b314.userservice.exception.registration.UserRegistrationConfirmationTokenExpiredException;
import b314.userservice.repository.UserRepository;
import b314.userservice.service.registration.userregistrationconfirmation.UserRegistrationConfirmationService;
import b314.userservice.service.user.UserDtoConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implements user registration functionality
 */
@Service
public class UserRegistratorService {

    private final UserRepository userRepository;

    private final UserDtoConverter converter;

    private final UserRegistrationConfirmationService userRegistrationConfirmationService;

    @Autowired
    public UserRegistratorService(UserRepository userRepository,
                                  UserDtoConverter converter,
                                  UserRegistrationConfirmationService userRegistrationConfirmationService) {
        this.userRepository = userRepository;
        this.converter = converter;
        this.userRegistrationConfirmationService = userRegistrationConfirmationService;
    }

    /**
     * Registers user, identified by dto
     * User registration needs to be confirmed by user
     * @param userRegistrationDTO dto for user to register
     * @return registered User instance
     */
    public User registerUserUnconfirmed(UserRegistrationDTO userRegistrationDTO) {
        // Creating user based on dto
        var user = converter.userRegistrationDto2User(userRegistrationDTO);

        // Saving
        userRepository.save(user);

        // Creating user verification token and sending verification email
        userRegistrationConfirmationService.sendInitialUserRegistrationConfirmationRequestEmail(user);

        // Returning
        return user;
    }

    public User processUserRegistrationConfirmation(String rawToken) throws NoSuchUserRegistrationConfirmationTokenException,
                                                                            MaxUserRegistrationConfirmationTokensCountReached,
                                                                            UserRegistrationConfirmationTokenExpiredException {
        // Validating token and getting user
        User user = userRegistrationConfirmationService.validateUserRegistrationConfirmationToken(rawToken);

        // Enabling user
        user.setEnabled(true);

        // Saving status change
        userRepository.save(user);

        // Sending user registration confirmation success email
        userRegistrationConfirmationService.sendUserRegistrationConfirmationSuccessEmail(user);

        // Returning
        return user;
    }

}
