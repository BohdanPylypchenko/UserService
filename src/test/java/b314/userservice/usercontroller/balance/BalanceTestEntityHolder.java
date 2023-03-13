package b314.userservice.usercontroller.balance;

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
public class BalanceTestEntityHolder {

    private final User admin;

    private final PasswordEncoder passwordEncoder;

    private User regular;

    @Autowired
    public BalanceTestEntityHolder(User admin, PasswordEncoder passwordEncoder) {
        this.admin = admin;
        this.passwordEncoder = passwordEncoder;
        initializeRegularWithBalance(0);
    }

    public User getAdmin() {
        return admin;
    }

    public User getRegular() {
        return regular;
    }

    public void initializeRegularWithBalance(float balance) {
        // Initializing regular
        Role role = new Role();
        role.setRoleType(RoleType.USER);
        regular =  User.builder()
                       .id(2)
                       .email("user1@gmail.com")
                       .password(passwordEncoder.encode("123"))
                       .firstName("John")
                       .lastName("Smith")
                       .age(23)
                       .driverLicense("license1234")
                       .roles(List.of(role))
                       .enabled(true)
                       .balance(balance)
                       .build();
    }

}
