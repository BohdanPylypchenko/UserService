package b314.userservice.service.registration.userregistrationconfirmation;

import b314.userservice.entity.user.User;
import b314.userservice.entity.userregistrationconfirmation.UserRegistrationConfirmationToken;
import b314.userservice.exception.registration.MaxUserRegistrationConfirmationTokensCountReached;
import b314.userservice.exception.registration.NoSuchUserRegistrationConfirmationTokenException;
import b314.userservice.exception.registration.UserRegistrationConfirmationTokenExpiredException;
import b314.userservice.repository.UserRegistrationConfirmationTokenRepository;
import b314.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Handles confirmation email send and
 * user registration confirmation
 */
@Service
public class UserRegistrationConfirmationService {

    private final UserRepository userRepository;

    private final UserRegistrationConfirmationTokenRepository tokenRepository;

    private final JavaMailSender mailSender;

    private final EmailCreatorService emailCreatorService;

    @Value("${userservice.user-registration-confirmation-token.maxFailedCount}")
    private int maxFailedCount;

    @Value("${userservice.user-registration-confirmation-token.hsLifetime}")
    private int hsLifetime;

    @Autowired
    public UserRegistrationConfirmationService(UserRegistrationConfirmationTokenRepository tokenRepository,
                                               UserRepository userRepository,
                                               JavaMailSender mailSender,
                                               EmailCreatorService emailCreatorService) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.mailSender = mailSender;
        this.emailCreatorService = emailCreatorService;
    }

    /**
     * Sends initial user registration confirmation email to given user
     * @param user to send email to
     */
    public void sendInitialUserRegistrationConfirmationRequestEmail(User user) {
        sendUserRegistrationConfirmationRequestEmail(user, "");
    }

    /**
     * Validates user registration confirmation token, represented by given raw token
     * @param rawToken raw token to find actual by
     * @return If token valid -> User in Optional, else -> empty optional
     */
    public User validateUserRegistrationConfirmationToken(String rawToken) throws NoSuchUserRegistrationConfirmationTokenException,
                                                                                  MaxUserRegistrationConfirmationTokensCountReached,
                                                                                  UserRegistrationConfirmationTokenExpiredException {
        // Getting actual token
        UserRegistrationConfirmationToken token = tokenRepository.getUserRegistrationConfirmationTokenByRawToken(rawToken)
                                                                 .orElseThrow(() -> new NoSuchUserRegistrationConfirmationTokenException("Invalid raw token"));

        // Check if token is expired
        if (token.isExpired()) {
            // Get count of existing tokens for the user
            int count = tokenRepository.countByUser(token.getUser());
            // Check if count less than max possible
            if (count + 1 >= maxFailedCount) {
                // true -> delete user, tokens, send confirmation fail email, throw exception
                userRepository.delete(token.getUser());
                mailSender.send(emailCreatorService.createUserRegistrationConfirmationFailEmail(token.getUser()));
                throw new MaxUserRegistrationConfirmationTokensCountReached("Sorry, max failed confirmation attempts count was reached. Email was blocked.");
            } else {
                // false -> send another confirmation email, throw exception
                String answer = "You did not confirm your account with last confirmation mail.";
                int left = maxFailedCount - count - 1;
                answer += " You have " + left + " confirmation attempts left.";
                sendUserRegistrationConfirmationRequestEmail(token.getUser(), answer);
                throw new UserRegistrationConfirmationTokenExpiredException("");
            }
        }

        // Deleting all confirmation tokens
        tokenRepository.deleteByUser(token.getUser());

        // Returning user
        return token.getUser();
    }

    /**
     * Sends user registration confirmation success email to
     * @param user to send success email for
     */
    public void sendUserRegistrationConfirmationSuccessEmail(User user) {
        // Creating success email
        SimpleMailMessage email = emailCreatorService.createUserRegistrationConfirmationSuccessEmail(user);

        // Sending
        mailSender.send(email);
    }

    /**
     * Creates token for given user and sends confirmation email to user
     * @param user to work with
     * @param additionalMessage Additional message to include in mail
     */
    private void sendUserRegistrationConfirmationRequestEmail(User user, String additionalMessage) {
        // Creating verification token
        UserRegistrationConfirmationToken token = new UserRegistrationConfirmationToken(UUID.randomUUID().toString(),
                                                                                        user,
                                                                                        hsLifetime);

        // Saving token
        tokenRepository.save(token);

        // Sending
        mailSender.send(emailCreatorService.createUserRegistrationConfirmationMail(user, token, additionalMessage));
    }

}
