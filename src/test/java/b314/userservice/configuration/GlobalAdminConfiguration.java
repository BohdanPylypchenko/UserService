package b314.userservice.configuration;

import b314.userservice.entity.user.User;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuration class for creating global admin
 * ========================================================
 * Global admin is the only user which can have role ADMIN
 * Global admin is used every time ADMIN access is required
 */
@Configuration
public class GlobalAdminConfiguration {

    /**
     * Global admin bean creator
     * Creates User instance to use as global admin, saves it to db
     * Default bean name - globalAdmin
     * @param passwordEncoder to encrypt admins password
     * @return global admin user
     */
    @Bean
    @Lookup
    User globalAdmin(GlobalAdminProperties properties, PasswordEncoder passwordEncoder) {
        // Returning
        return User.builder()
                   .enabled(properties.isEnabled())
                   .email(properties.getEmail())
                   .firstName(properties.getFirstName())
                   .lastName(properties.getLastName())
                   .password(passwordEncoder.encode(properties.getPassword()))
                   .roles(properties.getRoles())
                   .age(properties.getAge())
                   .balance(properties.getBalance())
                   .driverLicense(properties.getDriverLicense())
                   .build();
    }

}
