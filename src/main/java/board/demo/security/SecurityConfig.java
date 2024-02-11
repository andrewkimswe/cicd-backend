package board.demo.security;

import board.demo.service.UserService;
import board.demo.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

    // SecurityFilterChain을 정의하는 Bean
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers(
                                "/", "/static/**", "/favicon.ico", "/manifest.json",
                                "/login", "/signup", "/posts/**", "/login/oauth2/code/**",
                                "/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html",
                                "/public/**", "/api/**", "/chat-socket/**", "/api/comments/**/replies/**"
                        ).permitAll()
                        .requestMatchers("/user/**").hasRole("USER")
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(oAuth2LoginSuccessHandler) // Use the injected bean here
                        .failureHandler(new OAuth2LoginFailureHandler())
                )
                .logout(logout -> logout
                        .logoutUrl("/logout?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                );

        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));
        http.addFilterBefore(jwtRequestFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public JwtRequestFilter jwtRequestFilter() {
        return new JwtRequestFilter(jwtUtil, userDetailsService);
    }

    //CORS(Cross-Origin Resource Sharing) 구성은 다른 웹 도메인에서 리소스 요청을 보낼 때, 웹 애플리케이션이 그 요청을 허용할지 또는 거부할지를 정의하는 중요한 역할을 합니다. 다른 도메인에서의 요청을 허용하거나 거부함으로써 웹 애플리케이션의 보안을 강화하고, 동시에 다른 웹 애플리케이션과의 상호작용을 제어할 수 있습니다.
    //
    //CORS 구성의 역할은 다음과 같습니다:
    //
    //보안 강화: 웹 애플리케이션은 기본적으로 동일 출처 정책(Same-Origin Policy)을 따릅니다. 이 정책에 따르면, 웹 페이지는 동일한 출처(프로토콜, 호스트, 포트가 모두 일치)에서만 다른 리소스를 요청할 수 있습니다. 그러나 실제 상황에서는 다른 도메인의 서버에서 데이터를 요청하는 경우가 많기 때문에, CORS를 사용하여 보안을 강화합니다.
    //
    //다른 도메인에서의 요청 허용: CORS 구성을 통해 다른 도메인에서의 요청을 허용할 수 있습니다. 이를 통해 웹 애플리케이션은 다른 도메인의 클라이언트로부터 요청을 수락하고 응답을 반환할 수 있습니다.
    //
    //HTTP 헤더 설정: CORS 정책을 구성하면 다양한 HTTP 헤더(예: Access-Control-Allow-Origin, Access-Control-Allow-Methods, Access-Control-Allow-Headers 등)를 설정하여 요청의 허용 범위와 규칙을 정의할 수 있습니다.
    //
    //보안과 상호운용성 균형: CORS는 보안을 강화하면서도 다른 도메인과의 상호운용성을 유지할 수 있도록 합니다. 특히, 웹 애플리케이션이 서로 다른 도메인의 API에 액세스해야 하는 경우에 유용합니다.
    //
    //예를 들어, 웹 애플리케이션이 클라이언트 측 JavaScript 코드에서 다른 도메인의 API로 HTTP 요청을 보내려면, 해당 API 서버에서 CORS를 구성하여 요청을 허용해야 합니다. 그렇지 않으면 브라우저에서 CORS 정책을 위반한 요청을 차단하게 됩니다.
    //
    //따라서 CORS 구성은 웹 애플리케이션의 보안과 상호운용성을 조절하기 위한 중요한 도구 중 하나입니다.
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true);
        configuration.addAllowedOriginPattern("*");  // or specify origins if needed
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.setExposedHeaders(Arrays.asList("Authorization")); // 클라이언트가 Authorization 헤더에 접근할 수 있도록 설정
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, UserService userService) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder
                .userDetailsService(userService)
                .passwordEncoder(passwordEncoder);

        return authenticationManagerBuilder.build();
    }

}