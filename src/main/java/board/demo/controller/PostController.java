package board.demo.controller;

import com.github.andrewkimswe.chat.model.Post;
import com.github.andrewkimswe.chat.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    @Autowired
    private PostService postService;

    @PostMapping
    public ResponseEntity<Post> createPost(@RequestParam("nickname") String nickname,
                                           @RequestParam("password") String password,
                                           @RequestParam("type") String type,
                                           @RequestParam("title") String title,
                                           @RequestParam("content") String content,
                                           @RequestParam(value = "file", required = false) MultipartFile file) {
        // Here, create a new Post object and set its fields from the form data
        Post post = new Post();
        post.setNickname(nickname);
        post.setPassword(password); // Consider encrypting or hashing if sensitive
        post.setType(type);
        post.setTitle(title);
        post.setContent(content);
        post.setCreatedAt(new Date());

        // If file processing is required, handle the file here

        // Save the post
        Post savedPost = postService.createPost(post);
        return ResponseEntity.ok(savedPost);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable String id, @RequestHeader(value = "Increment-View", defaultValue = "false") boolean incrementView) {
        Post post = postService.getPostById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        if (incrementView) {
            postService.incrementViews(id);
        }
        return ResponseEntity.ok(post);
    }

    @GetMapping
    public ResponseEntity<List<Post>> getAllPosts(@RequestParam(required = false) String search) {
        return ResponseEntity.ok(postService.getAllPosts(search));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Post> updatePost(@PathVariable String id, @RequestBody Post post, @RequestParam("password") String password) {
        Post existingPost = postService.getPostById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loggedInUsername = authentication.getName();

        if (loggedInUsername.equals("admin")) {
            Post updatedPost = postService.updatePost(id, post);
            return ResponseEntity.ok(updatedPost);
        } else if (!existingPost.getPassword().equals(password)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        Post updatedPost = postService.updatePost(id, post);
        return ResponseEntity.ok(updatedPost);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(@PathVariable String id, @RequestParam("password") String password) {
        Post existingPost = postService.getPostById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loggedInUsername = authentication.getName();

        if (loggedInUsername.equals("admin")) {
            postService.deletePost(id);
            return ResponseEntity.ok().build();
        } else if (!existingPost.getPassword().equals(password)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        postService.deletePost(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/like")
    public ResponseEntity<?> likePost(@PathVariable String id) {
        postService.incrementLikes(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/unlike")
    public ResponseEntity<?> unlikePost(@PathVariable String id) {
        postService.decrementLikes(id);
        return ResponseEntity.ok().build();
    }

}
