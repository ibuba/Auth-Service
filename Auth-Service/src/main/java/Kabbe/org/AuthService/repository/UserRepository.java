package Kabbe.org.AuthService.repository;

import Kabbe.org.AuthService.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.Optional;


@EnableJpaRepositories
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findUserByEmail(String username);
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
}
