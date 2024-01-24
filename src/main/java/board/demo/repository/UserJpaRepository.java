package board.demo.repository;

import board.demo.model.user.SocialType;
import board.demo.model.user.UserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Repository interface for CRUD operations on User entities.
 * Includes custom query methods for social login and traditional login handling.
 */
public interface UserJpaRepository extends JpaRepository<UserJpaEntity, Long> {

    // Find a user by email
    Optional<UserJpaEntity> findByEmail(String email);

    // Find a user by social ID and provider
    Optional<UserJpaEntity> findBySocialIdAndProvider(String socialId, SocialType provider);


    // Check if a user exists with a given email
    boolean existsByEmail(String email);

    Optional<UserJpaEntity> findByEmailVerificationToken(String token);

}
