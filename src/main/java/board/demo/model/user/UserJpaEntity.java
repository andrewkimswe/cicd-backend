package board.demo.model.user;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor // Added for a full constructor
public class UserJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String password; // Added for storing the hashed password
    private LocalDateTime createdAt;
    private String socialId;
    private boolean emailVerified;
    private String emailVerificationToken;

    @Enumerated(EnumType.STRING)
    private SocialType provider;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<Role> role;

    // Constructor for OAuth users (without password)
    public UserJpaEntity(String email, SocialType provider, Set<Role> role) {
        this.email = email;
        this.createdAt = LocalDateTime.now(); // Set current time as default
        this.provider = provider;
        this.role = role;
    }
}
