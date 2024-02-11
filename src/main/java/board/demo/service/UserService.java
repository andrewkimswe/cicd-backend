package board.demo.service;

import board.demo.dto.RegisterRequest;
import board.demo.model.user.Role;
import board.demo.model.user.SocialType;
import board.demo.model.user.UserJpaEntity;
import board.demo.repository.UserJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserJpaRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);


    public boolean verifyEmail(String token) {
        Optional<UserJpaEntity> user = userRepository.findByEmailVerificationToken(token);
        if (user.isPresent()) {
            user.get().setEmailVerified(true);
            userRepository.save(user.get());
            return true;
        } else {
            return false;
        }
    }


    public UserJpaEntity registerUser(RegisterRequest registerRequest) {
        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 사용중인 메일입니다");
        }
        String verificationToken = UUID.randomUUID().toString();
        UserJpaEntity newUser = new UserJpaEntity();
        newUser.setEmail(registerRequest.getEmail());
        newUser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        newUser.setCreatedAt(LocalDateTime.now());
        newUser.setEmailVerified(false); // 이메일 미확인 상태로 설정

        Set<Role> roles = new HashSet<>();
        roles.add(Role.USER);
        newUser.setRole(roles);

        UserJpaEntity savedUser = userRepository.save(newUser);

        // 이메일 검증을 위한 메소드 호출
        emailService.sendVerificationEmail(newUser.getEmail(), verificationToken);

        // 토큰 저장
        newUser.setEmailVerificationToken(verificationToken);
        userRepository.save(newUser);

        return savedUser;
    }


    public Optional<UserJpaEntity> getUserById(Long id) {
        return userRepository.findById(id);
    }


    public List<UserJpaEntity> getAllUsers() {
        return userRepository.findAll();
    }


    public UserJpaEntity createOrUpdateUser(String email, String provider, Set<String> roles) {
        Optional<UserJpaEntity> userOpt = userRepository.findByEmail(email);
        UserJpaEntity user;

        if (userOpt.isPresent()) {
            user = userOpt.get();
        } else {
            user = new UserJpaEntity();
            user.setEmail(email);
        }

        // provider 값을 SocialType으로 변환하여 설정
        user.setProvider(SocialType.valueOf(provider.toUpperCase()));
        user.setRole(Collections.singleton(Role.USER));

        // 소셜 로그인 사용자의 경우 이메일 인증 처리
        user.setEmailVerified(true);

        return userRepository.save(user);
    }


    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }


    public UserJpaEntity updateUserProfile(Long id, String email) {
        UserJpaEntity user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setEmail(email);

        return userRepository.save(user);
    }


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserJpaEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        if (!user.isEmailVerified()) {
            throw new UsernameNotFoundException("이메일 인증이 완료되지 않았습니다.");
        }

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


    public UserJpaEntity updateRole(Long userId, Role newRole) {
        UserJpaEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setRole(Collections.singleton(newRole));
        return userRepository.save(user);
    }


    public void saveUser(UserJpaEntity user) {
        userRepository.save(user);
    }

    public boolean unlockAccount(String token) {
        Optional<UserJpaEntity> userOpt = userRepository.findByEmailVerificationToken(token);
        if (userOpt.isPresent()) {
            UserJpaEntity user = userOpt.get();
            user.setEmailVerified(true);
            user.setLoginAttempts(0); // 재시도 횟수 초기화
            user.setLockUntil(null); // 잠금 해제
            userRepository.save(user);
            return true;
        }
        return false;
    }

    public void initiatePasswordReset(String email) {
        UserJpaEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + email));
        String resetToken = UUID.randomUUID().toString();
        user.setPasswordResetToken(resetToken);
        user.setPasswordResetTokenExpiry(LocalDateTime.now().plusHours(1)); // 토큰 유효 시간 설정, 예: 1시간
        userRepository.save(user);
        emailService.sendPasswordResetEmail(email, resetToken);
    }

    // UserService 내에 추가
    public boolean resetPassword(String token, String newPassword) {
        Optional<UserJpaEntity> userOpt = userRepository.findByPasswordResetToken(token)
                .filter(user -> user.getPasswordResetTokenExpiry().isAfter(LocalDateTime.now()));

        if (!userOpt.isPresent()) {
            return false;
        }

        UserJpaEntity user = userOpt.get();
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordResetToken(null); // 토큰 사용 후 초기화
        user.setPasswordResetTokenExpiry(null); // 만료 시간 초기화
        userRepository.save(user);
        return true;
    }

    @Transactional
    public void loginAttempt(String email) {
        logger.info("Login attempt for email: " + email); // 로그인 시도 로그 추가
        Optional<UserJpaEntity> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            UserJpaEntity user = userOpt.get();
            logger.info("Current login attempts: " + user.getLoginAttempts()); // 현재 로그인 시도 횟수 로그
            user.setLoginAttempts(user.getLoginAttempts() + 1);
            if (user.getLoginAttempts() >= 5) {
                logger.info("Locking account for 60 minutes"); // 계정 잠금 로그
                user.setLockUntil(LocalDateTime.now().plusMinutes(60)); // 60분 동안 잠금
            }
            userRepository.save(user);
        } else {
            logger.warn("No user found with email: " + email); // 사용자 미발견 로그
        }
    }



}