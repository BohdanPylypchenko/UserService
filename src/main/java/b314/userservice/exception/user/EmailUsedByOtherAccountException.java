package b314.userservice.exception.user;

import java.sql.SQLIntegrityConstraintViolationException;

public class EmailUsedByOtherAccountException extends SQLIntegrityConstraintViolationException {

    public EmailUsedByOtherAccountException(String reason) {
        super(reason);
    }

}
