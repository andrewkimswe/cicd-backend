package board.demo.service;

import com.github.andrewkimswe.chat.dto.RegisterRequest;
import com.github.andrewkimswe.chat.model.user.Role;
import com.github.andrewkimswe.chat.model.user.UserJpaEntity;
import com.github.andrewkimswe.chat.repository.UserJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserJpaRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserJpaEntity registerUser(RegisterRequest registerRequest) {
        UserJpaEntity newUser = new UserJpaEntity();
        newUser.setEmail(registerRequest.getEmail());
        newUser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        newUser.setCreatedAt(LocalDateTime.now());
        newUser.setRole(Collections.singleton(Role.USER)); // Default role
        // You can add more fields to be set from RegisterRequest as needed

        return userRepository.save(newUser);
    }

    public Optional<UserJpaEntity> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public List<UserJpaEntity> getAllUsers() {
        return userRepository.findAll();
    }

    public UserJpaEntity createOrUpdateUser(String email, String provider, Set<String> roles) {
        Optional<UserJpaEntity> userOpt = userRepository.findByEmail(email);
        UserJpaEntity user = userOpt.orElseGet(UserJpaEntity::new);

        user.setEmail(email);

        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public UserJpaEntity updateUserProfile(Long id, String email) {
        UserJpaEntity user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setEmail(email);
        // Add more fields to update as needed

        return userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserJpaEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // 사용자 정보를 UserDetails로 변환하여 반환
        return org.springframework.security.core.userdetails.User
                .withUsername(email)
                .password(user.getPassword())
                .authorities(user.getRole().stream().map(Role::toString).toArray(String[]::new))
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }
}
