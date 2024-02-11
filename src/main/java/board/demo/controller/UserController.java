package board.demo.controller;

import board.demo.model.user.Role;
import board.demo.model.user.UserJpaEntity;
import board.demo.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

    @PutMapping("/{id}/role")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<UserJpaEntity> updateUserRole(@PathVariable Long id, @RequestParam("role") Role role) {
        UserJpaEntity updatedUser = userService.updateRole(id, role);
        return ResponseEntity.ok(updatedUser);
    }

    @PostMapping("/unlock-account")
    public ResponseEntity<?> unlockAccount(@RequestParam("token") String token) {
        if (userService.unlockAccount(token)) {
            return ResponseEntity.ok("계정이 잠금 해제되었습니다.");
        }
        return ResponseEntity.badRequest().body("잘못된 요청입니다.");
    }

    @GetMapping("/request-reset-password")
    public ResponseEntity<?> requestResetPassword(@RequestParam("email") String email) {
        userService.initiatePasswordReset(email);
        return ResponseEntity.ok("비밀번호 재설정 이메일을 발송했습니다.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(
            @RequestParam("token") String token,
            @RequestParam("newPassword") String newPassword) {
        boolean isReset = userService.resetPassword(token, newPassword);
        if (isReset) {
            return ResponseEntity.ok("비밀번호가 성공적으로 재설정되었습니다.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("비밀번호 재설정 요청이 유효하지 않습니다.");
        }
    }


}
