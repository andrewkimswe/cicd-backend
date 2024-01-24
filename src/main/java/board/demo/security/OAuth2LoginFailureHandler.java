package board.demo.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import java.io.IOException;

public class OAuth2LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        // Log OAuth2 failure
        System.out.println("OAuth2 Authentication Failed: " + exception.getMessage());

        // Set custom error response
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "OAuth2 Authentication Failed");
    }
}
