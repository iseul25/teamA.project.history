// board/service/PostService.java
package com.lms.history.board.service;

import com.lms.history.board.entity.Post;
import com.lms.history.board.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {
    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public Post createPost(Post post) {
        post.setCreated(LocalDateTime.now());
        post.setUpdated(LocalDateTime.now());
        return postRepository.save(post);
    }

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    public Optional<Post> getPostById(Long id) {
        return postRepository.findById(id);
    }

    public Optional<Post> updatePost(Long id, Post updatedPost) {
        return postRepository.findById(id).map(post -> {
            post.setTitle(updatedPost.getTitle());
            post.setContent(updatedPost.getContent());
            post.setUpdated(LocalDateTime.now());
            return postRepository.save(post);
        });
    }

    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }
}
