package board.demo.service;

import board.demo.model.post.FilterT;
import board.demo.model.post.Post;
import board.demo.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;


    public Post createPost(Post post) {
        post.setCreatedAt(new Date());
        return postRepository.save(post);
    }


    public List<Post> getAllPosts(String search, FilterT filterType) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");

        switch (filterType) {
            case POPULAR:
                Sort popularSort = Sort.by(Sort.Direction.DESC, "views");
                return postRepository.findAllBy(popularSort);

            case NOTICES:
                Sort noticesSort = Sort.by(Sort.Direction.DESC, "createdAt");
                return postRepository.findByType("notice", noticesSort);

            default:
                return postRepository.findAll(sort);
        }
    }


    public Post updatePost(String id, Post updatedPost, String imagePath, String filePath) {
        Post existingPost = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        existingPost.setTitle(updatedPost.getTitle());
        existingPost.setContent(updatedPost.getContent());
        existingPost.setNickname(updatedPost.getNickname());

        if (imagePath != null) {
            existingPost.setImagePath(imagePath); // 이미지 경로 업데이트
        }
        if (filePath != null) {
            existingPost.setFilePath(filePath);   // 파일 경로 업데이트
        }

        return postRepository.save(existingPost);
    }


    public void deletePost(String id) {
        postRepository.deleteById(id);
    }


    public Optional<Post> getPostById(String id) {
        return postRepository.findById(id);
    }


    @Transactional
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
