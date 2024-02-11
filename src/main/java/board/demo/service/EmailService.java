package board.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    private final String baseUrl; // 이메일 인증 링크의 기본 URL


    @Autowired
    public EmailService(JavaMailSender mailSender, @Value("${app.baseUrl}") String baseUrl) {
        this.mailSender = mailSender;
        this.baseUrl = baseUrl;
    }


    public void sendVerificationEmail(String to, String verificationToken) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom("andrewkimswe@gmail.com");
        mailMessage.setTo(to);
        mailMessage.setSubject("이메일 인증");
        String confirmUrl = baseUrl + "/api/users/confirm?token=" + verificationToken;
        mailMessage.setText("인증 링크: " + confirmUrl);
        mailSender.send(mailMessage);
    }

    // EmailService 클래스 내에 추가
    public void sendPasswordResetEmail(String to, String resetToken) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom("noreply@example.com"); // 적절한 발신자 주소로 변경
        mailMessage.setTo(to);
        mailMessage.setSubject("비밀번호 재설정 요청");
        String resetUrl = baseUrl + "/api/users/reset-password?token=" + resetToken;
        mailMessage.setText("비밀번호를 재설정하려면 다음 링크를 클릭하세요: " + resetUrl);
        mailSender.send(mailMessage);
    }

}