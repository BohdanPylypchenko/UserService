package b314.userservice.exception.user;

import javax.security.sasl.AuthenticationException;

/**
 * Exception to throw in hasSameId method
 */
public class AuthorizedIdNotSameAsRequestedException extends AuthenticationException {

    public AuthorizedIdNotSameAsRequestedException(String detail) {
        super(detail);
    }

}
