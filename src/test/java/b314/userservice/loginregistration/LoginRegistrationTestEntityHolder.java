package b314.userservice.loginregistration;

import b314.userservice.dto.registration.UserRegistrationConfirmationDTO;
import b314.userservice.entity.role.Role;
import b314.userservice.entity.role.RoleType;
import b314.userservice.entity.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Scope("prototype")
class LoginRegistrationTestEntityHolder {

    private final User user;

    private final UserRegistrationConfirmationDTO confirmedUserRegistrationConfirmationDTO;

    @Autowired
    public LoginRegistrationTestEntityHolder(PasswordEncoder passwordEncoder) {
        // Creating roles for user 1
        List<Role> roles = List.of(new Role(), new Role());
        roles.get(0).setRoleType(RoleType.USER);
        roles.get(1).setRoleType(RoleType.CAR_OWNER);

        // Initializing expected user 1
        user = User.builder()
                   .id(1)
                   .email("user1@gmail.com")
                   .password(passwordEncoder.encode("123"))
                   .firstName("John")
                   .lastName("Smith")
                   .age(23)
                   .driverLicense("license1234")
                   .roles(roles)
                   .enabled(false)
                   .build();

        // Initializing confirmed user dto
        confirmedUserRegistrationConfirmationDTO = UserRegistrationConfirmationDTO.builder()
                                                                                  .email("user1@gmail.com")
                                                                                  .firstName("John")
                                                                                  .lastName("Smith")
                                                                                  .roles(roles)
                                                                                  .build();
    }

    public User getUser() {
        return user;
    }

    public UserRegistrationConfirmationDTO getConfirmedUserRegistrationConfirmationDTO() {
        return confirmedUserRegistrationConfirmationDTO;
    }

}
