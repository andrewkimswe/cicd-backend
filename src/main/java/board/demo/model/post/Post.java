package board.demo.model.post;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;

@Data
@Document(collection = "post")
public class Post {
    @Id
    private String id;
    private String title;
    private String content;
    private String nickname;
    private String password;
    private String type;
    private Date createdAt;
    private Integer views = 0; // Default value is 0
    private Integer likes = 0; // Default value is 0
    private String imagePath;
    private String filePath;
}