package Kabbe.org.AuthService.token;

import Kabbe.org.AuthService.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Token {
    @Id
    private Integer id;
    private String token;
    @Enumerated(EnumType.STRING)
    private TokenType tokenType;

    private boolean expired;

    private boolean revoked;
    @ManyToOne()
    @JoinColumn(name="user_id")
    private User user;
}
