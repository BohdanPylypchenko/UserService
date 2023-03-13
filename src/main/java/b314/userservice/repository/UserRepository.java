package b314.userservice.repository;

import b314.userservice.entity.user.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * CrudRepository fo User
 */
@Repository
@Transactional
public interface UserRepository extends CrudRepository<User, Integer> {

    // Returns user by given email
    Optional<User> findByEmail(String username);

}
