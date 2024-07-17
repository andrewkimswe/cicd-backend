package board.demo.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "comment")
public class Comment {

    @Id
    private String id;
    private String postId; // 연결된 Post의 ID
    private String parentId; // 부모 댓글의 ID
    private String content;
    private String password; // 댓글 수정/삭제를 위한 비밀번호
}
