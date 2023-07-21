package Kabbe.org.AuthService.repository;

import Kabbe.org.AuthService.entity.Role;
import Kabbe.org.AuthService.entity.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
@EnableJpaRepositories

public interface RoleRepository extends JpaRepository<Role, Long>                                  {

    Optional<Role> findRoleByRoleName(RoleType roleName);
}
