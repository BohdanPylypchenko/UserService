package b314.userservice.entity.role;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Represents user's role
 */
@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"id"})
public class Role implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private int id;

    @Enumerated(EnumType.STRING)
    @Column(name = "name")
    private RoleType roleType;

}
