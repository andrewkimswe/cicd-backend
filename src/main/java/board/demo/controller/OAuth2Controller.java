package board.demo.controller;

import com.github.andrewkimswe.chat.dto.AuthResponse;
import com.github.andrewkimswe.chat.dto.LoginRequest;
import com.github.andrewkimswe.chat.dto.RegisterRequest;
import com.github.andrewkimswe.chat.model.user.UserJpaEntity;
import com.github.andrewkimswe.chat.service.UserService;
import com.github.andrewkimswe.chat.util.JwtUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class OAuth2Controller {
    private HttpServletRequest request;
    private AuthenticationManager authenticationManager;
    private JwtUtil jwtUtil;
    private UserService userService;

    @Autowired
    public OAuth2Controller(HttpServletRequest request,
                            AuthenticationManager authenticationManager,
                            JwtUtil jwtUtil,
                            UserService userService) {
        this.request = request;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    // Endpoint for standard login
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );
        String jwt = jwtUtil.generateToken(authentication.getName());
        return ResponseEntity.ok(new AuthResponse(jwt));
    }

    // Endpoint for user registration
    @PostMapping("/register")
    public ResponseEntity<UserJpaEntity> registerUser(@RequestBody RegisterRequest registerRequest) {
        UserJpaEntity user = userService.registerUser(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    // Endpoint for OAuth2 login
    @PostMapping("/oauth2/login")
    public ResponseEntity<AuthResponse> authenticateOAuth2User(OAuth2AuthenticationToken auth) {
        // Assuming the email is the unique identifier in your JWT claims
        String email = auth.getPrincipal().getAttribute("email");

        // Generate the JWT token
        String jwt = jwtUtil.generateToken(email);

        // Return the token in the response
        return ResponseEntity.ok(new AuthResponse(jwt));
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