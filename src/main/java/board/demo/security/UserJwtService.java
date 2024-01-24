package board.demo.security;

import com.github.andrewkimswe.chat.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class UserJwtService {

    private final JwtUtil jwtUtil;

    @Autowired
    public UserJwtService(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    public String generateToken(UserDetails userDetails) {
        return jwtUtil.generateToken(userDetails.getUsername());
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = jwtUtil.extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !jwtUtil.isTokenExpired(token));
    }
}
