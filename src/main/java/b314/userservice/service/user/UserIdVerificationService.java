package b314.userservice.service.user;

import b314.userservice.entity.user.User;
import b314.userservice.exception.AnonymousAccessForbiddenException;
import b314.userservice.exception.user.AuthorizedIdNotSameAsRequestedException;
import b314.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Service to provide actual user id = id of token owner functionality
 */
@Service
public class UserIdVerificationService {

    private final UserRepository userRepository;

    @Autowired
    public UserIdVerificationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Checks if authorized user has same id as given
     * @param id to check for
     * @return check result
     */
    public boolean hasSameId(int id) throws AuthorizedIdNotSameAsRequestedException {
        // Getting email of authorized user
        var candidate = SecurityContextHolder.getContext().getAuthentication();
        UsernamePasswordAuthenticationToken authentication;
        if (UsernamePasswordAuthenticationToken.class.equals(candidate.getClass())) {
            authentication = (UsernamePasswordAuthenticationToken) candidate;
        } else {
            throw new AnonymousAccessForbiddenException("Anonymous access is not allowed");
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();

        // Getting actual user from db
        User user = userRepository.findByEmail(email)
                                  .orElseThrow(() -> new UsernameNotFoundException("User NOT found"));

        // Returning
        if (id == user.getId()) {
            return true;
        } else {
            throw new AuthorizedIdNotSameAsRequestedException("Trying to access other user profile");
        }
    }

}
