package b314.userservice.service.user;

import b314.userservice.dto.user.UserProfileDto;
import b314.userservice.entity.user.User;
import b314.userservice.exception.user.InvalidUserStatusException;
import b314.userservice.exception.user.NoUserWithSuchIdException;
import b314.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service to provide user status operations
 */
@Service
public class UserStatusService {

    private final UserRepository userRepository;

    private final UserDtoConverter converter;

    @Autowired
    public UserStatusService(UserRepository userRepository, UserDtoConverter converter) {
        this.userRepository = userRepository;
        this.converter = converter;
    }

    /**
     * Updates status user with given id
     * @param id of user to update
     * @param status new user status
     * @return user profile dto of updated user
     */
    public UserProfileDto updateUserStatusById(int id, String status)
            throws NoUserWithSuchIdException, InvalidUserStatusException {
        // Getting user from db
        User user = userRepository.findById(id)
                                  .orElseThrow(() -> new NoUserWithSuchIdException("No user with given id found"));

        // Update user status
        switch (status.toLowerCase()) {
            case "enable"  -> user.setEnabled(true);
            case "disable" -> user.setEnabled(false);
            default        -> throw new InvalidUserStatusException("Invalid user status. No update occurred.");
        }

        // Updating user in db
        userRepository.save(user);

        // Returning user profile of updated user
        return converter.user2UserProfileDto(user);
    }

}
