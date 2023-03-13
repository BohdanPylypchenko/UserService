package b314.userservice.service.user;

import b314.userservice.dto.user.UserBalanceDto;
import b314.userservice.entity.user.User;
import b314.userservice.exception.user.NegativeBalanceException;
import b314.userservice.exception.user.NoUserWithSuchIdException;
import b314.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service to provide user balance operations
 */
@Service
public class UserBalanceService {

    private final UserRepository userRepository;

    @Autowired
    public UserBalanceService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Updates user's balance with given increment
     * @param id of user to change
     * @param balanceIncrement to add to balance
     * @return added balance increment
     */
    public UserBalanceDto increaseUserBalanceById(int id, float balanceIncrement) throws NoUserWithSuchIdException {
        // Getting user from db
        User user = userRepository.findById(id)
                                  .orElseThrow(() -> new NoUserWithSuchIdException("No user with given id found"));

        // Increasing balance
        float updatedBalance = user.increaseBalance(balanceIncrement);

        // Saving
        userRepository.save(user);

        // Returning
        return new UserBalanceDto(updatedBalance);
    }

    /**
     * Updates user's balance with given decrement
     * @param id of user to change
     * @param balanceDecrement to subtract from balance
     * @return subtracted balance increment
     */
    public UserBalanceDto decreaseUserBalanceById(int id, float balanceDecrement)
            throws NoUserWithSuchIdException, NegativeBalanceException {
        // Getting user from db
        User user = userRepository.findById(id)
                                  .orElseThrow(() -> new NoUserWithSuchIdException("No user with given id found"));

        // Increasing balance
        user.decreaseBalance(balanceDecrement);

        // Saving
        userRepository.save(user);

        // Returning
        return new UserBalanceDto(user.getBalance());
    }

}
