package b314.userservice.service.user;

import b314.userservice.dto.registration.UserRegistrationConfirmationDTO;
import b314.userservice.dto.registration.UserRegistrationDTO;
import b314.userservice.dto.user.UserProfileDto;
import b314.userservice.entity.role.Role;
import b314.userservice.entity.role.RoleType;
import b314.userservice.entity.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Implements User <-> dto conversion
 */
@Component
public class UserDtoConverter {

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserDtoConverter(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Converts given user to user registration confirmation dto
     * @param user to convert
     * @return DTO from given user
     */
    public UserRegistrationConfirmationDTO user2UserRegistrationConfirmationDto(User user) {
        return UserRegistrationConfirmationDTO.builder()
                                              .email(user.getEmail())
                                              .firstName(user.getFirstName())
                                              .lastName(user.getLastName())
                                              .roles(user.getRoles())
                                              .build();
    }

    /**
     * Converts given user registration dto to User instance
     * @param userRegistrationDTO Dto to take data from
     * @return User based on provided dto
     */
    public User userRegistrationDto2User(UserRegistrationDTO userRegistrationDTO) {
        // Creating User instance
        return User.builder()
                   .email(userRegistrationDTO.getEmail())
                   .password(passwordEncoder.encode(userRegistrationDTO.getPassword()))
                   .firstName(userRegistrationDTO.getFirstName())
                   .lastName(userRegistrationDTO.getLastName())
                   .age(userRegistrationDTO.getAge())
                   .driverLicense(userRegistrationDTO.getDriverLicense())
                   .roles(Arrays.stream(userRegistrationDTO.getRoles())
                           .map(roleType -> {
                               Role role = new Role();
                               role.setRoleType(RoleType.valueOf(roleType));
                               return role;
                           })
                           .toList())
                   .enabled(false)
                   .build();
    }

    /**
     * Converts user to user profile dto
     * @param user to convert
     * @return user profile dto based on given user
     */
    public UserProfileDto user2UserProfileDto(User user) {
        return UserProfileDto.builder()
                             .id(user.getId())
                             .email(user.getEmail())
                             .firstName(user.getFirstName())
                             .lastName(user.getLastName())
                             .age(user.getAge())
                             .driverLicense(user.getDriverLicense())
                             .roles(user.getRoles())
                             .enabled(user.isEnabled())
                             .balance(user.getBalance())
                             .build();
    }

}
