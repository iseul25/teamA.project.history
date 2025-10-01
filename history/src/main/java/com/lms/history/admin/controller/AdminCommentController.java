package com.lms.history.admin.controller;

import com.lms.history.boards.entity.Board;
import com.lms.history.boards.entity.Comment;
import com.lms.history.boards.entity.CommentReply;
import com.lms.history.boards.service.BoardService;
import com.lms.history.boards.service.CommentService;
import com.lms.history.boards.service.CommentReplyService;
import com.lms.history.users.entity.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Controller
@RequestMapping("/admin")
public class AdminCommentController {

    private final CommentService commentService;
    private final CommentReplyService replyService;
    private final BoardService boardService;

    public AdminCommentController(CommentService commentService,
                                  CommentReplyService replyService,
                                  BoardService boardService) {
        this.commentService = commentService;
        this.replyService = replyService;
        this.boardService = boardService;
    }

    // 댓글 관리 페이지
    @GetMapping("/comment")
    public String getCommentManagementPage(
            @RequestParam(value = "category", defaultValue = "전체") String category,
            @RequestParam(value = "page", defaultValue = "1") int page,
            Model model,
            HttpSession session) {

        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null || !"관리자".equals(loginUser.getUserType())) {
            return "redirect:/login";
        }

        int pageSize = 8;

        List<Board> boards = "전체".equals(category) ? boardService.findAll() : boardService.findByBoardType(category);

        List<Comment> allComments = new ArrayList<>();
        Map<Integer, String> boardTitleMap = new HashMap<>();
        Map<Integer, String> boardTypeMap = new HashMap<>();
        Map<Integer, List<CommentReply>> repliesMap = new HashMap<>();

        for (Board board : boards) {
            boardTitleMap.put(board.getBoardId(), board.getTitle());
            boardTypeMap.put(board.getBoardId(), board.getBoardType());

            List<Comment> comments = commentService.findByBoardId(board.getBoardId());
            allComments.addAll(comments);

            for (Comment comment : comments) {
                List<CommentReply> replies = replyService.findByCommentId(comment.getCommentId());
                repliesMap.put(comment.getCommentId(), replies);
            }
        }

        allComments.sort(Comparator.comparingInt(Comment::getCommentId));

        int start = (page - 1) * pageSize;
        int end = Math.min(start + pageSize, allComments.size());
        List<Comment> pagedComments = allComments.subList(start, end);

        int totalPages = (int) Math.ceil((double) allComments.size() / pageSize);

        model.addAttribute("comments", pagedComments);
        model.addAttribute("repliesMap", repliesMap);
        model.addAttribute("boardTitleMap", boardTitleMap);
        model.addAttribute("boardTypeMap", boardTypeMap);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("totalElements", allComments.size());

        return "admin/adminComment";
    }

    // 댓글 삭제
    @PostMapping("/comment/delete/{commentId}")
    public String deleteComment(
            @PathVariable int commentId,
            @RequestParam(value = "category", defaultValue = "전체") String category,
            @RequestParam(value = "page", defaultValue = "1") int page,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null || !"관리자".equals(loginUser.getUserType())) {
            return "redirect:/login";
        }

        try {
            replyService.deleteByCommentId(commentId);
            commentService.delete(commentId, loginUser.getUserId(), loginUser.getUserType());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "삭제 중 오류가 발생했습니다.");
        }

        return "redirect:/admin/comment?category=" + URLEncoder.encode(category, StandardCharsets.UTF_8) + "&page=" + page;
    }

    // 답글 삭제
    @PostMapping("/reply/delete/{replyId}")
    public String deleteReply(
            @PathVariable int replyId,
            @RequestParam(value = "category", defaultValue = "전체") String category,
            @RequestParam(value = "page", defaultValue = "1") int page,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null || !"관리자".equals(loginUser.getUserType())) {
            return "redirect:/login";
        }

        try {
            replyService.delete(replyId, loginUser.getUserId(), loginUser.getUserType());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "삭제 중 오류가 발생했습니다.");
        }

        return "redirect:/admin/comment?category=" + URLEncoder.encode(category, StandardCharsets.UTF_8) + "&page=" + page;
    }
}
