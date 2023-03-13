package b314.userservice.exception.user;

import java.sql.SQLException;

/**
 * Exception to throw when no user with given is found by findById repository method
 */
public class NoUserWithSuchIdException extends SQLException {

    public NoUserWithSuchIdException(String reason) {
        super(reason);
    }

}
