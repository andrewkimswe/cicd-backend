package board.demo.service;

import board.demo.model.Post;
import board.demo.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;

    public Post createPost(Post post) {
        return postRepository.save(post);
    }

    public List<Post> getAllPosts(String search) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt"); // 최신 글이 먼저 나오도록 정렬
        if (search == null || search.isEmpty()) {
            return postRepository.findAll(sort);
        } else {
            return postRepository.findByTitleContainingOrContentContaining(search, search, sort);
        }
    }

    public Post updatePost(String id, Post updatedPost) {
        Post existingPost = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        existingPost.setTitle(updatedPost.getTitle());
        existingPost.setContent(updatedPost.getContent());
        existingPost.setNickname(updatedPost.getNickname()); // 닉네임 업데이트 확인
        // 필요한 다른 필드도 업데이트
        return postRepository.save(existingPost);
    }


    public void deletePost(String id) {
        postRepository.deleteById(id);
    }

    public Optional<Post> getPostById(String id) {
        return postRepository.findById(id);
    }

    public void incrementViews(String postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));
        post.setViews(post.getViews() + 1);
        postRepository.save(post);
    }

    public void incrementLikes(String postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));
        post.setLikes(post.getLikes() + 1);
        postRepository.save(post);
    }

    public void decrementLikes(String postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));
        post.setLikes(post.getLikes() - 1);
        postRepository.save(post);
    }
}
