package b314.userservice.repository;

import b314.userservice.entity.user.User;
import b314.userservice.entity.userregistrationconfirmation.UserRegistrationConfirmationToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Repository for UserRegistrationConfirmationToken entity
 */
@Repository
@Transactional
public interface UserRegistrationConfirmationTokenRepository extends
        CrudRepository<UserRegistrationConfirmationToken, Integer> {

    /**
     * Returns User, which corresponds to given raw token
     * @param rawToken token to search by
     * @return Optional: User if found, else null
     */
    Optional<UserRegistrationConfirmationToken> getUserRegistrationConfirmationTokenByRawToken(String rawToken);

    int countByUser(User user);

    void deleteByUser(User user);

}
