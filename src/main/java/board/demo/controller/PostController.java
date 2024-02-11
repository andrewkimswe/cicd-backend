package board.demo.controller;

import board.demo.model.post.FilterT;
import board.demo.model.post.Post;
import board.demo.service.FileStorageService;
import board.demo.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private FileStorageService fileStorageService;

    @PostMapping
    public ResponseEntity<Post> createPost(
            @RequestParam("nickname") String nickname,
            @RequestParam("password") String password,
            @RequestParam("type") String type,
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam(value = "file", required = false) MultipartFile file) {

        Post post = new Post();
        post.setNickname(nickname);
        post.setPassword(password);
        post.setType(type);
        post.setTitle(title);
        post.setContent(content);

        if (image != null && !image.isEmpty()) {
            String imagePath = fileStorageService.storeFile(image);
            post.setImagePath("저장된 이미지 경로/" + imagePath);
        }

        if (file != null && !file.isEmpty()) {
            String filePath = fileStorageService.storeFile(file);
            post.setFilePath("저장된 파일 경로/" + filePath);
        }

        Post savedPost = postService.createPost(post);
        return ResponseEntity.ok(savedPost);
    }



    @GetMapping("/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable String id) {
        Post post = postService.getPostById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        postService.incrementViews(id); // Incrementing the views
        return ResponseEntity.ok(post);
    }

    @GetMapping
    public ResponseEntity<List<Post>> getAllPosts(@RequestParam(required = false) String search, @RequestParam(required = false) String filter) {
        FilterT filterType;

        try {
            filterType = filter != null ? FilterT.valueOf(filter.toUpperCase()) : FilterT.ALL;
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null); // In case of an invalid filter type
        }

        return ResponseEntity.ok(postService.getAllPosts(search, filterType));
    }


    @PutMapping("/{id}")
    public ResponseEntity<Post> updatePost(@PathVariable String id,
                                           @RequestBody Post post,
                                           @RequestParam("password") String password,
                                           @RequestParam(value = "image", required = false) MultipartFile image,
                                           @RequestParam(value = "file", required = false) MultipartFile file) {
        Post existingPost = postService.getPostById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loggedInUsername = authentication.getName();

        String imagePath = null;
        String filePath = null;

        if (image != null && !image.isEmpty()) {
            imagePath = fileStorageService.storeFile(image);
            imagePath = "저장된 이미지 경로/" + imagePath;
        }

        if (file != null && !file.isEmpty()) {
            filePath = fileStorageService.storeFile(file);
            filePath = "저장된 파일 경로/" + filePath;
        }

        if (loggedInUsername.equals("admin") || existingPost.getPassword().equals(password)) {
            Post updatedPost = postService.updatePost(id, post, imagePath, filePath);
            return ResponseEntity.ok(updatedPost);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
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
}
