// board/service/CommentService.java
package com.lms.history.board.service;

import com.lms.history.board.entity.Comment;
import com.lms.history.board.entity.Post;
import com.lms.history.board.repository.CommentRepository;
import com.lms.history.board.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    public CommentService(CommentRepository commentRepository, PostRepository postRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
    }

    public Optional<Comment> addComment(Long postId, Comment comment) {
        Optional<Post> postOptional = postRepository.findById(postId);
        if (postOptional.isEmpty()) return Optional.empty();

        comment.setPost(postOptional.get());
        comment.setCreated(LocalDateTime.now());
        return Optional.of(commentRepository.save(comment));
    }

    public List<Comment> getCommentsByPostId(Long postId) {
        return commentRepository.findByPostPostId(postId);
    }
}
