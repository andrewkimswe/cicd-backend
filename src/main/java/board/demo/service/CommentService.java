package board.demo.service;

import com.github.andrewkimswe.chat.model.Comment;
import com.github.andrewkimswe.chat.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;

    public Comment createComment(Comment comment, String password) {
        comment.setPassword(password); // Set password
        return commentRepository.save(comment);
    }

    public Optional<Comment> getCommentById(String commentId) {
        return commentRepository.findById(commentId);
    }

    public List<Comment> getCommentsByPostId(String postId) {
        Sort sort = Sort.by(Sort.Direction.ASC, "createdAt"); // 과거 댓글이 먼저 나오도록 정렬
        return commentRepository.findByPostId(postId, sort);
    }

    public Comment updateComment(String commentId, String password, Comment updatedComment) {
        Optional<Comment> existingComment = getCommentById(commentId);
        if (existingComment.isPresent()) {
            Comment comment = existingComment.get();
            if (!comment.getPassword().equals(password)) {
                throw new RuntimeException("Incorrect password");
            }
            comment.setContent(updatedComment.getContent());
            return commentRepository.save(comment);
        } else {
            throw new RuntimeException("Comment not found with id: " + commentId);
        }
    }


    public void deleteComment(String commentId, String password) {
        Optional<Comment> existingComment = commentRepository.findById(commentId);
        if (!existingComment.isPresent()) {
            throw new RuntimeException("Comment not found with id: " + commentId);
        }

        Comment comment = existingComment.get();
        if (!comment.getPassword().equals(password)) {
            throw new RuntimeException("Incorrect password");
        }

        commentRepository.deleteById(commentId);
    }

    // 기타 필요한 메서드...
}
