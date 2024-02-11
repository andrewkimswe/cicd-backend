package board.demo.security;

import board.demo.model.user.UserJpaEntity;
import board.demo.service.UserService;
import board.demo.util.JwtUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;

@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserService userService;

    private final JwtUtil jwtUtil;


    @Autowired
    public OAuth2LoginSuccessHandler(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;

            String provider = token.getAuthorizedClientRegistrationId();
            String email = token.getPrincipal().getAttribute("email");

            UserJpaEntity userJpa = userService.createOrUpdateUser(email, provider, Set.of("USER"));

            if (!userJpa.isEmailVerified()) {
                userJpa.setEmailVerified(true);
                userService.saveUser(userJpa);
            }

            // Generate the JWT token
            String jwt = jwtUtil.generateToken(email);

            System.out.println("OAuth2 Authentication Successful: " + authentication.getName());

            String redirectUrl = "http://localhost:3000/posts/?jwt=" + jwt;
            getRedirectStrategy().sendRedirect(request, response, redirectUrl);

        } else {
            super.onAuthenticationSuccess(request, response, authentication);
        }
    }

}