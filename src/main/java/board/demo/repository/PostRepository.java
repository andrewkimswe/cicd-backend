package board.demo.repository;

import board.demo.model.post.Post;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PostRepository extends MongoRepository<Post, String> {

    // In PostRepository
    List<Post> findAllBy(Sort sort);

    List<Post> findByType(String type, Sort sort);
}