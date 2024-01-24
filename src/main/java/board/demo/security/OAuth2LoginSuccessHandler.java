package board.demo.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import java.io.IOException;

public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    // Constructor
    public OAuth2LoginSuccessHandler() {
        setDefaultTargetUrl("http://localhost:3000/posts");
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        System.out.println("OAuth2 Authentication Successful: " + authentication.getName());

        super.onAuthenticationSuccess(request, response, authentication);
    }
}
