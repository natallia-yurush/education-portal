package by.nyurush.portal.repository;

import by.nyurush.portal.entity.User;
import by.nyurush.portal.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
    List<User> findAllByRole(UserRole userRole);
}
