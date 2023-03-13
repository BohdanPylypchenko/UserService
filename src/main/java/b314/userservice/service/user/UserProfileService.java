package b314.userservice.service.user;

import b314.userservice.dto.user.UpdateUserDto;
import b314.userservice.dto.user.UserProfileDto;
import b314.userservice.entity.user.User;
import b314.userservice.exception.user.EmailUsedByOtherAccountException;
import b314.userservice.exception.user.NoUserWithSuchIdException;
import b314.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

/**
 * Service to provide user profile operations
 */
@Service
public class UserProfileService {

    private final UserRepository userRepository;

    private final UserDtoConverter converter;

    @Autowired
    public UserProfileService(UserRepository userRepository, UserDtoConverter converter) {
        this.userRepository = userRepository;
        this.converter = converter;
    }

    /**
     * Returns user profile of user, represented by given id
     * @param id Id of user to get profile for
     * @return UserProfileDto of user
     * @throws NoUserWithSuchIdException If no user with specified id is present in db
     */
    public UserProfileDto getUserProfileById(int id) throws NoUserWithSuchIdException {
        User result = userRepository.findById(id)
                                    .orElseThrow(() -> new NoUserWithSuchIdException("No user with given id found"));
        return converter.user2UserProfileDto(result);
    }

    /**
     * Updates user with given id by info from provided profile
     * @param id of user to update
     * @param updatedUserInfo to get new info from
     * @return user profile dto of updated user
     */
    public UserProfileDto updateUserByIdWithInfo(int id, UpdateUserDto updatedUserInfo)
            throws NoUserWithSuchIdException, EmailUsedByOtherAccountException {
        // Getting user from db
        User user = userRepository.findById(id)
                                  .orElseThrow(() -> new NoUserWithSuchIdException("No user with given id found"));

        // Setting new values for user properties
        user.setEmail(updatedUserInfo.getEmail());
        user.setFirstName(updatedUserInfo.getFirstName());
        user.setLastName(updatedUserInfo.getLastName());
        user.setAge(updatedUserInfo.getAge());
        user.setDriverLicense(updatedUserInfo.getDriverLicense());

        // Trying to update user in db
        try {
            // Saving updated user
            userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            // There is another account with given email, though exception
            throw new EmailUsedByOtherAccountException("Provided email is already used by another account.");
        }

        // Returning user profile of updated user
        return converter.user2UserProfileDto(user);
    }

}
