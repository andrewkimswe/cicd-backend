package board.demo.service;

import board.demo.model.Comment;
import board.demo.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    // 댓글 생성
    public Comment createComment(Comment comment) {
        return commentRepository.save(comment);
    }

    // 특정 게시글의 모든 댓글 조회
    public Page<Comment> getCommentsByPostId(String postId, Pageable pageable) {
        return commentRepository.findByPostId(postId, pageable);
    }

    // 댓글 업데이트
    public Comment updateComment(String commentId, String content, String password) {
        Comment existingComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found: " + commentId));

        if (!existingComment.getPassword().equals(password)) {
            throw new RuntimeException("Invalid password for comment: " + commentId);
        }

        existingComment.setContent(content);
        return commentRepository.save(existingComment);
    }




    // 댓글 삭제
    public void deleteComment(String commentId, String password) {
        Comment existingComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        if (!existingComment.getPassword().equals(password)) {
            throw new RuntimeException("Invalid password");
        }
        commentRepository.delete(existingComment);
    }

}
