package b314.userservice.exception.user;

import b314.userservice.entity.user.User;

import java.util.function.Supplier;

/**
 * Exception to throw if negative user balance appears during balance operation processing
 */
public class NegativeBalanceException extends Exception {

    /**
     * Default constructor
     * @param user - target of negative balance case
     * @param balanceDecrement to produce negative balance case
     */
    public NegativeBalanceException(User user, float balanceDecrement) {
        super(((Supplier<String>) () -> {
            // Building message
            String messageBuilder = "Can't decrease user balance (id = " +
                                    user.getId() +
                                    "): user balance = " +
                                    user.getBalance() +
                                    " < balance decrement = " +
                                    balanceDecrement;

            // Returning
            return messageBuilder;
        }).get());
    }

}
