package board.demo.model;

import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class Comment {
    @Id
    private String id;
    private String postId; // 연결된 Post의 ID
    private String content;
    private String author; // 댓글 작성자
    private String password; // 댓글 수정/삭제를 위한 비밀번호
}

