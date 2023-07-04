package Kabbe.org.AuthService.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NaturalId;

import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name="user_table")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    @NaturalId(mutable = true)
    private String email;
    private String password;
    private boolean isEnabled = false;

    @OneToMany
    @JoinColumn(name = "userId")
    private Set<Role> roles;

}
