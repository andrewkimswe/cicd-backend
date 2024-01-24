package board.demo.controller;

import board.demo.model.user.UserJpaEntity;
import board.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    // UserService 주입
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // OAuth2 로그인 성공 시 호출되는 엔드포인트
    @GetMapping("/loginSuccess")
    public ResponseEntity<?> loginSuccess(OAuth2AuthenticationToken token) {
        String provider = token.getAuthorizedClientRegistrationId();
        String email = token.getPrincipal().getAttribute("email");
        String name = token.getPrincipal().getAttribute("name");

        // Convert provider to String and role to Set<String>
        UserJpaEntity userJpa = userService.createOrUpdateUser(email, provider, Set.of("USER"));

        return new ResponseEntity<>(userJpa, HttpStatus.OK);
    }


    // 특정 ID를 가진 사용자 조회
    @GetMapping("/{id}")
    public ResponseEntity<UserJpaEntity> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 모든 사용자 조회
    @GetMapping
    public ResponseEntity<List<UserJpaEntity>> getAllUsers() {
        List<UserJpaEntity> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    // 사용자 정보 업데이트
    @PutMapping("/{id}")
    public ResponseEntity<UserJpaEntity> updateUser(@PathVariable Long id, @RequestBody UserJpaEntity updatedUser) {
        UserJpaEntity user = userService.getUserById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        UserJpaEntity savedUser = userService.createOrUpdateUser(
                updatedUser.getEmail(),  // Use updatedUser's email
                user.getProvider().name(),  // Keep the original provider
                updatedUser.getRole().stream().map(Enum::name).collect(Collectors.toSet()) // Use updatedUser's roles
        );
        return ResponseEntity.ok(savedUser);
    }


    // 특정 사용자 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/profile")
    public ResponseEntity<UserJpaEntity> updateUserProfile(@PathVariable Long id, @RequestBody UserJpaEntity userRequest) {
        UserJpaEntity updatedUser = userService.updateUserProfile(id, userRequest.getEmail());
        return ResponseEntity.ok(updatedUser);
    }
}
