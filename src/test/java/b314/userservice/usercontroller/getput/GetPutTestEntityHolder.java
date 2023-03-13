package b314.userservice.usercontroller.getput;

import b314.userservice.dto.user.UserProfileDto;
import b314.userservice.entity.role.Role;
import b314.userservice.entity.role.RoleType;
import b314.userservice.entity.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Test entity holder class
 * for get and put user controller tests
 */
@Component
@Scope("prototype")
class GetPutTestEntityHolder {

    private final User expectedUser1;

    private final User expectedUser2;

    private final User expectedChangedUser;

    private final UserProfileDto userProfileDto;

    private final UserProfileDto expectedChangedUserProfileDto;

    @Autowired
    public GetPutTestEntityHolder(PasswordEncoder passwordEncoder) {
        // Creating roles for user 1
        List<Role> roles1 = List.of(new Role(), new Role());
        roles1.get(0).setRoleType(RoleType.USER);
        roles1.get(1).setRoleType(RoleType.CAR_OWNER);

        // Initializing expected user 1
        expectedUser1 = User.builder()
                            .id(1)
                            .email("user1@gmail.com")
                            .password(passwordEncoder.encode("123"))
                            .firstName("John")
                            .lastName("Smith")
                            .age(23)
                            .driverLicense("license1234")
                            .roles(roles1)
                            .enabled(false)
                            .build();

        // Initializing expected user 2
        List<Role> roles2 = List.of(new Role());
        roles2.get(0).setRoleType(RoleType.USER);
        expectedUser2 = User.builder()
                            .id(2)
                            .email("user2@outlook.com")
                            .password(passwordEncoder.encode("123"))
                            .firstName("Amanda")
                            .lastName("Resenbaum")
                            .age(34)
                            .driverLicense("MU8294A")
                            .roles(roles2)
                            .enabled(false)
                            .build();

        // Initializing expected user
        expectedChangedUser = User.builder()
                                  .id(1)
                                  .email("SteveJ0bs@apple.com")
                                  .password(passwordEncoder.encode("123"))
                                  .firstName("Steve")
                                  .lastName("Jobs")
                                  .age(63)
                                  .driverLicense("IphoneGuy217")
                                  .roles(roles1)
                                  .enabled(true)
                                  .build();

        // Initializing user profile dto
        userProfileDto = UserProfileDto.builder()
                                       .id(1)
                                       .email(expectedUser1.getEmail())
                                       .firstName(expectedUser1.getFirstName())
                                       .lastName(expectedUser1.getLastName())
                                       .age(expectedUser1.getAge())
                                       .driverLicense(expectedUser1.getDriverLicense())
                                       .roles(expectedUser1.getRoles())
                                       .enabled(true)
                                       .build();

        // Initializing changed user profile dto
        expectedChangedUserProfileDto = UserProfileDto.builder()
                                                      .id(1)
                                                      .email(expectedChangedUser.getEmail())
                                                      .firstName(expectedChangedUser.getFirstName())
                                                      .lastName(expectedChangedUser.getLastName())
                                                      .age(expectedChangedUser.getAge())
                                                      .driverLicense(expectedChangedUser.getDriverLicense())
                                                      .roles(expectedChangedUser.getRoles())
                                                      .enabled(true)
                                                      .build();
    }

    public User getExpectedUser1() {
        return expectedUser1;
    }

    public User getExpectedUser2() {
        return expectedUser2;
    }

    public User getExpectedChangedUser() {
        return expectedChangedUser;
    }

    public UserProfileDto getUserProfileDto() {
        return userProfileDto;
    }

    public UserProfileDto getExpectedChangedUserProfileDto() {
        return expectedChangedUserProfileDto;
    }
}
