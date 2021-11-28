package by.nyurush.portal.repository;

import by.nyurush.portal.entity.User;
import by.nyurush.portal.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByEmailOrUsername(String email, String username);

    List<User> findAllByRole(UserRole userRole);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    long countUserByRole(UserRole userRole);
}
