package b314.userservice.exception.registration;

/**
 * Exceptions to through when
 * token count for given user reached maximum
 */
public class MaxUserRegistrationConfirmationTokensCountReached extends Exception {

    public MaxUserRegistrationConfirmationTokensCountReached(String msg) {
        super(msg);
    }

}
