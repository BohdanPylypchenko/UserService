package b314.userservice.usercontroller.status;

import b314.userservice.dto.user.UserProfileDto;
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
class UpdateUserStatusTestEntityHolder {

    private final User admin;

    private final User user2;

    @Autowired
    public UpdateUserStatusTestEntityHolder(PasswordEncoder passwordEncoder, User admin) {
        // initializing admin
        this.admin = admin;

        // Creating roles for user
        List<Role> roles2 = List.of(new Role());
        roles2.get(0).setRoleType(RoleType.USER);

        // Initializing user 2
        user2 = User.builder()
                    .id(2)
                    .email("user2@outlook.com")
                    .password(passwordEncoder.encode("123"))
                    .firstName("Amanda")
                    .lastName("Resenbaum")
                    .age(34)
                    .driverLicense("MU8294A")
                    .roles(roles2)
                    .build();
    }

    public User getUser1() {
        return admin;
    }

    public User getUser2() {
        return user2;
    }

    public UserProfileDto createUserProfileDtoFor2ndUserWithStatus(boolean status) {
        // Creating user profile dto of 2nd user
        return UserProfileDto.builder()
                             .id(2)
                             .email("user2@outlook.com")
                             .firstName("Amanda")
                             .lastName("Resenbaum")
                             .age(34)
                             .driverLicense("MU8294A")
                             .roles(user2.getRoles())
                             .enabled(status)
                             .build();
    }

}
