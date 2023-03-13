package b314.userservice.entity.userregistrationconfirmation;

import b314.userservice.entity.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Represents user registration confirmation token
 * No args constructor should not be used manually!!!
 */
@Entity
@Table(name = "confirmation_tokens")
@Getter
@NoArgsConstructor
public class UserRegistrationConfirmationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_id")
    private int id;

    @NotNull
    @Column(name = "rawtoken")
    private String rawToken;

    @NotNull
    @Column(name = "expire_date_time")
    private LocalDateTime expireDateTime;

    @ManyToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    /**
     * Default manual constructor
     * @param rawToken Random string to use as token value
     * @param user User instance, which token is created for
     */
    public UserRegistrationConfirmationToken(String rawToken, User user, int hours) {
        // Initializing fields
        this.rawToken = rawToken;
        this.user = user;

        // Initializing expiration date
        initializeExpireDate(hours);
    }

    /**
     * Checks, if token is expired
     */
    public boolean isExpired() {
        // Calculating difference
        Duration difference = Duration.between(LocalDateTime.now(), expireDateTime);

        // Returning
        return difference.isNegative();
    }

    /**
     * Initializes expiration date
     */
    private void initializeExpireDate(int hours) {
        expireDateTime = LocalDateTime.now().plusHours(hours);
    }

}
