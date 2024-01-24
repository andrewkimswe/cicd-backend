package board.demo.service;

import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Properties;

@Service
public class MailService {

    private final String accessToken; // OAuth 2.0 액세스 토큰을 저장하는 필드

    public String getAccessToken() {
        String accessTokenValue = null;

        try {
            GoogleCredentials credentials = GoogleCredentials
                    .fromStream(new FileInputStream("src/main/resources/client_secret_216085716340-ep8bbvpviq346n7iornnj6posmoktu9g.apps.googleusercontent.com.json"))
                    .createScoped(Collections.singleton("https://mail.google.com/"));
            credentials.refreshIfExpired();
            AccessToken token = credentials.getAccessToken();
            accessTokenValue = token.getTokenValue();
        } catch (IOException e) {
            e.printStackTrace(); // 적절한 예외 처리를 해야 합니다.
        }

        return accessTokenValue;
    }
    public MailService() {
        this.accessToken = getAccessToken();
    }

    public void sendEmail(String to, String subject, String text) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.connectiontimeout", "5000");
        props.put("mail.smtp.timeout", "5000");
        props.put("mail.smtp.writetimeout", "5000");

        // OAuth 2.0 액세스 토큰을 사용하여 Session 객체를 구성합니다.
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("", accessToken); // 첫 번째 인자는 빈 문자열, 두 번째 인자는 액세스 토큰
            }
        });

        mailSender.setSession(session);

        // 메일을 준비하고 발송하는 로직
        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setFrom("your-email@gmail.com");
            messageHelper.setTo(to);
            messageHelper.setSubject(subject);
            messageHelper.setText(text);
        };

        mailSender.send(messagePreparator);
    }
}
