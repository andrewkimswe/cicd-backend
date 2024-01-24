package board.demo.service;

import board.demo.dto.RegisterRequest;
import board.demo.model.user.Role;
import board.demo.model.user.UserJpaEntity;
import board.demo.repository.UserJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserJpaRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MailService mailService; // MailService를 주입합니다.

    public UserJpaEntity registerUser(RegisterRequest registerRequest) {
        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 사용중인 메일입니다");
        }
        String verificationToken = UUID.randomUUID().toString(); // 인증 토큰 생성
        UserJpaEntity newUser = new UserJpaEntity();
        newUser.setEmail(registerRequest.getEmail());
        newUser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        newUser.setCreatedAt(LocalDateTime.now());
        newUser.setRole(Collections.singleton(Role.USER)); // Default role
        // You can add more fields to be set from RegisterRequest as needed

        UserJpaEntity savedUser = userRepository.save(newUser);

        sendVerificationEmail(newUser.getEmail(), verificationToken); // 인증 이메일 발송
        return savedUser;
    }

    private void sendVerificationEmail(String email, String verificationToken) {
        String verificationUrl = "http://yourdomain.com/verify?token=" + verificationToken;
        String subject = "이메일 인증";
        String text = "이메일 인증을 위해 다음 링크를 클릭해주세요: " + verificationUrl;

        mailService.sendEmail(email, subject, text); // MailService의 메서드를 사용하여 이메일을 발송합니다.
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
