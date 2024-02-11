package board.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String status;
    private String token;
    private String role;
    private String message;

    // 성공 응답을 위한 생성자
    public AuthResponse(String token, String role) {
        this.status = "success";
        this.token = token;
        this.role = role;
        this.message = "Login successful";
    }

    // 실패 응답을 위한 정적 팩토리 메소드
    public static AuthResponse failure(String message) {
        AuthResponse response = new AuthResponse();
        response.setStatus("fail");
        response.setMessage(message);
        // 실패 시 token과 role은 null 또는 적절한 기본값을 설정
        return response;
    }
}
