package Kabbe.org.AuthService.token;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Integer> {
    @Query("select t from Token t inner join User u on t.user.userId = u.userId \n" +
            "            where u.userId = :userId and (t.expired = false or t.revoked = false)")
    List<Token> findAllValidTokensByUser(Long userId);

    Optional<Token> findByToken(String token);
}
