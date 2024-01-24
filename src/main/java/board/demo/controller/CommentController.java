package board.demo.controller;

import com.github.andrewkimswe.chat.model.Comment;
import com.github.andrewkimswe.chat.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments") // URL 경로 변경
public class CommentController {
    @Autowired
    private CommentService commentService;

    // 댓글 생성
    @PostMapping
    public ResponseEntity<Comment> createComment(@RequestBody Comment comment) {
        Comment savedComment = commentService.createComment(comment, comment.getPassword());
        return ResponseEntity.ok(savedComment);
    }

    // 특정 포스트의 댓글 조회
    @GetMapping("/posts/{postId}")
    public ResponseEntity<List<Comment>> getCommentsByPostId(@PathVariable String postId) {
        List<Comment> comments = commentService.getCommentsByPostId(postId);
        return ResponseEntity.ok(comments);
    }

    // 댓글 수정
    @PutMapping("/{commentId}")
    public ResponseEntity<Comment> updateComment(@PathVariable String commentId,
                                                 @RequestBody Comment comment,
                                                 @RequestParam("password") String password) {
        Comment updatedComment = commentService.updateComment(commentId, password, comment);
        return ResponseEntity.ok(updatedComment);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable String commentId,
                                           @RequestParam("password") String password) {
        commentService.deleteComment(commentId, password);
        return ResponseEntity.ok().build();
    }
}
