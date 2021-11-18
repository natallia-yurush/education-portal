package by.nyurush.portal.service;

import by.nyurush.portal.entity.User;
import by.nyurush.portal.entity.UserRole;
import by.nyurush.portal.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static java.util.Objects.isNull;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User saveUser(User user) {
        if(isNull(user.getId())) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(user);
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(); //todo add exception
    }

    public List<User> findAllStudents() {
        return userRepository.findAllByRole(UserRole.ROLE_STUDENT);
    }

    public List<User> findAllTeachers() {
        return userRepository.findAllByRole(UserRole.ROLE_TEACHER);
    }

    public User findByUsername(String username) {
        //todo add custom exception
        return userRepository.findByUsername(username).orElseThrow();
    }

    public User findByUsernameAndPassword(String username, String password) {
        User user = findByUsername(username);
        if (user != null) {
            if (passwordEncoder.matches(password, user.getPassword())) {
                return user;
            }
        }
        return null;
    }

    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }
}

