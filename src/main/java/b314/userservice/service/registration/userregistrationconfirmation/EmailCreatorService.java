package b314.userservice.service.registration.userregistrationconfirmation;

import b314.userservice.entity.user.User;
import b314.userservice.entity.userregistrationconfirmation.UserRegistrationConfirmationToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * Handles creation of different emails for user registration confirmation service
 */
@Component
class EmailCreatorService {

    @Value("${userservice.service-mail.sentFrom}")
    private String sentFrom;

    private final String companyName = "DriveDynamics";

    /**
     * Creates initial user registration confirmation email
     * @param user User to send email to
     * @param token Token to include with email
     * @return SimpleMailMessage instance
     */
    public SimpleMailMessage createUserRegistrationConfirmationMail(User user,
                                                                    UserRegistrationConfirmationToken token,
                                                                    String additionalMessage) {
        // Creating email
        SimpleMailMessage email = new SimpleMailMessage();

        // Configuring
        email.setFrom(sentFrom);
        email.setTo(user.getEmail());
        email.setSubject("Registration confirmation request on " + companyName);
        email.setText(additionalMessage
                + "\r\n"
                + "Please follow the link to confirm registration at " + companyName + ":"
                + "\r\n"
                + ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString()
                + "/registration-confirm?token=" + token.getRawToken());

        // Returning
        return email;
    }

    /**
     * Creates user registration confirmation success email
     * @param user to create email for
     * @return SimpleMailMessage instance
     */
    public SimpleMailMessage createUserRegistrationConfirmationSuccessEmail(User user) {
        // Creating email
        SimpleMailMessage email = new SimpleMailMessage();

        // Configuring
        email.setFrom(sentFrom);
        email.setTo(user.getEmail());
        email.setSubject("Registration confirmation success on " + companyName);
        email.setText("Your account on " + companyName + " has been confirmed!");

        // Returning
        return email;
    }

    public SimpleMailMessage createUserRegistrationConfirmationFailEmail(User user) {
        // Creating email
        SimpleMailMessage email = new SimpleMailMessage();

        // Configuring
        email.setFrom(sentFrom);
        email.setTo(user.getEmail());
        email.setSubject("Registration confirmation fail on " + companyName);
        email.setText("Sorry, you last token has expired, you did not confirm your account so it was deleted. Try to register again.");

        // Returning
        return email;
    }

}
