package b314.userservice.service.userdetails;

import b314.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Custom UserDetailsService implementation
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    private final User2CustomUserDetailsConverter converter;

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository,
                                    User2CustomUserDetailsConverter converter) {
        this.userRepository = userRepository;
        this.converter = converter;
    }

    /**
     * UserDetailsService's loadUserByUsername implementation
     * @param email Identifier to search user by
     * @return UserDetails of user with given name
     * @throws UsernameNotFoundException if no user with given email in db
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Getting user
        var user =  userRepository.findByEmail(email)
                                  .orElseThrow(() -> new UsernameNotFoundException("User NOT found"));

        // Returning UserDetails implementation for user
        return converter.user2UserDetailsImplementation(user);
    }

}
