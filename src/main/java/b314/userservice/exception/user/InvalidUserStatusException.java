package b314.userservice.exception.user;

/**
 * Exception to throw when invalid status comes from client
 */
public class InvalidUserStatusException extends Exception {

    public InvalidUserStatusException(String message) {
        super(message);
    }

}
