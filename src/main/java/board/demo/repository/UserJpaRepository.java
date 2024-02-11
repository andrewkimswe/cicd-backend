package board.demo.repository;

import board.demo.model.user.UserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<UserJpaEntity, Long> {
    Optional<UserJpaEntity> findByEmail(String email);
    Optional<UserJpaEntity> findByEmailVerificationToken(String token);
    Optional<UserJpaEntity> findByPasswordResetToken(String token);

}
