package board.demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import board.demo.dto.AuthResponse;
import board.demo.dto.LoginRequest;
import board.demo.dto.RegisterRequest;
import board.demo.model.user.UserJpaEntity;
import board.demo.service.UserService;
import board.demo.util.JwtUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class OAuth2Controller {

    private static final Logger logger = LoggerFactory.getLogger(OAuth2Controller.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private HttpServletRequest request;

    @PostMapping("/register")
    public ResponseEntity<UserJpaEntity> registerUser(@RequestBody RegisterRequest registerRequest) {
        UserJpaEntity user = userService.registerUser(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

            // 인증 성공 시, JWT 토큰 생성
            String jwt = jwtUtil.generateToken(authentication.getName());

            // 사용자 역할 가져오기
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String role = userService.loadUserByUsername(userDetails.getUsername()).getAuthorities().toString();

            // 응답 헤더에 JWT 토큰 설정
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + jwt);

            // 역할 정보를 포함하여 응답 반환
            return ResponseEntity.ok().headers(headers).body(new AuthResponse(jwt, role));
        } catch (Exception e) {
            logger.error("Login error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login failed");
        }
    }




    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        try {
            request.logout();
            return ResponseEntity.ok().build();
        } catch (ServletException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
