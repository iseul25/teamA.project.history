// board/controller/BoardViewController.java
package com.lms.history.board.controller;

import com.lms.history.board.entity.Comment;
import com.lms.history.board.entity.Post;
import com.lms.history.board.service.CommentService;
import com.lms.history.board.service.PostService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/posts")  // 웹 페이지는 /posts 로 접근
public class BoardViewController {

    private final PostService postService;
    private final CommentService commentService;

    public BoardViewController(PostService postService, CommentService commentService) {
        this.postService = postService;
        this.commentService = commentService;
    }

    @GetMapping
    public String posts(Model model) {
        model.addAttribute("posts", postService.getAllPosts());
        return "posts";  // src/main/resources/templates/posts.html
    }

    @GetMapping("/{id}")
    public String postDetail(@PathVariable Long id, Model model) {
        Post post = postService.getPostById(id).orElseThrow(() -> new RuntimeException("글 없음"));
        List<Comment> comments = commentService.getCommentsByPostId(id);
        model.addAttribute("post", post);
        model.addAttribute("comments", comments);
        return "post_detail";  // src/main/resources/templates/post_detail.html
    }

    @PostMapping
    public String createPostFromForm(Post post) {
        postService.createPost(post);
        return "redirect:/posts";
    }

    @PostMapping("/{postId}/comments")
    public String addCommentFromForm(@PathVariable Long postId, Comment comment) {
        commentService.addComment(postId, comment);
        return "redirect:/posts/" + postId;
    }
}
