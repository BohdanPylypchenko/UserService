package b314.userservice.entity.user;

import b314.userservice.entity.role.Role;
import b314.userservice.exception.user.NegativeBalanceException;
import lombok.*;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;

/**
 * Custom User entity class
 */
@Entity
@Table(name = "users")
@Data
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"id", "password"})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private int id;

    @NotNull
    @Column(name = "email", unique = true)
    private String email;

    @NotNull
    @Column(name = "password")
    private String password;

    @NotNull
    @Column(name = "first_name")
    private String firstName;

    @NotNull
    @Column(name = "last_name")
    private String lastName;

    @NotNull
    @Min(18)
    @Column(name = "age")
    private int age;

    @NotNull
    @Column(name = "driver_license")
    private String driverLicense;

    @NotNull
    @Column(name = "enabled")
    private boolean enabled;

    /**
     * 1 user can have many roles
     * 1 role can be added to many users
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    @JoinTable(
            name="users_roles",
            joinColumns= {@JoinColumn(name="user_id")},
            inverseJoinColumns = {@JoinColumn(name="role_id")}
    )
    private List<Role> roles;

    @NotNull
    @Min(0)
    @Setter(AccessLevel.NONE)
    @Column(name = "balance")
    @Builder.Default
    private float balance = 0.0f;

    /**
     * Increases user's balance with given increment
     * @param balanceIncrement to give to user
     * @return updated balance of user
     */
    public float increaseBalance(@Positive float balanceIncrement) {
        balance += balanceIncrement;
        return balance;
    }

    /**
     * Decreases user's balance with given value
     * @param balanceDecrement to change balance
     * @return updated balance of user
     * @throws NegativeBalanceException if negative balance case occurs
     */
    public float decreaseBalance(@Positive float balanceDecrement) throws NegativeBalanceException {
        // Calculating possible balance value
        float possibleUpdatedBalance = balance - balanceDecrement;

        // Checking for negative
        if (possibleUpdatedBalance < 0) {
            // New balance value is negative, throw exception
            throw new NegativeBalanceException(this, balanceDecrement);
        }

        // Assigning new balance
        balance = possibleUpdatedBalance;

        // Returning
        return balance;
    }

}
