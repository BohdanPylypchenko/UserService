package b314.userservice.service.userdetails;

import b314.userservice.entity.user.User;
import b314.userservice.entity.user.CustomUserDetails;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

/**
 * Implements conversion User -> UserDetailsImplementation
 */
@Component
class User2CustomUserDetailsConverter {

    /**
     * Converts User to UserDetailsImplementation
     * @param user User to convert
     * @return UserDetailsImplementation instance with properties from given User
     */
    public CustomUserDetails user2UserDetailsImplementation(User user) {
        // Returning
        return CustomUserDetails.builder()
                                .email(user.getEmail())
                                .password(user.getPassword())
                                .enabled(user.isEnabled())
                                .authorities(user.getRoles()
                                                 .stream()
                                                 .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRoleType())).toList())
                                .build();
    }

}
