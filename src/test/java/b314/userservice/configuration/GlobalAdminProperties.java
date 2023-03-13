package b314.userservice.configuration;

import b314.userservice.entity.role.Role;
import b314.userservice.entity.role.RoleType;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Data
@ConfigurationProperties(prefix = "userservice.global-admin")
@PropertySource("classpath:test-admin.properties")
public class GlobalAdminProperties {

    @Value("${userservice.global-admin.email")
    private String email;

    @Value("${userservice.global-admin.password")
    private String password;

    @Value("${userservice.global-admin.firstname")
    private String firstName;

    @Value("${userservice.global-admin.lastname")
    private String lastName;

    @Value("${userservice.global-admin.enabled}")
    private boolean enabled;

    private int age = 19;

    private String driverLicense = "";

    private float balance = 0.0f;

    private List<Role> roles;

    public GlobalAdminProperties() {
        // Initializing role list
        Role role = new Role();
        role.setRoleType(RoleType.ADMIN);
        roles = List.of(role);
    }

}
