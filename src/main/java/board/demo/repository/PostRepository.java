package board.demo.repository;

import com.github.andrewkimswe.chat.model.Post;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PostRepository extends MongoRepository<Post, String> {
    List<Post> findByTitleContainingOrContentContaining(String title, String content, Sort sort);
}