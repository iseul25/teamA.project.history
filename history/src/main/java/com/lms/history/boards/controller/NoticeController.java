package com.lms.history.boards.controller;

import com.lms.history.boards.entity.Board;
import com.lms.history.boards.service.BoardService;
import com.lms.history.users.entity.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class NoticeController {

    private final BoardService boardService;
    private static final int PAGE_SIZE = 10; // 페이지당 게시글 수

    public NoticeController(BoardService boardService) {
        this.boardService = boardService;
    }

    @GetMapping("/notice")
    public String notice(@RequestParam(value = "page", defaultValue = "0") int page,
                         Model model, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        model.addAttribute("loginUser", loginUser);

        // 페이징 처리
        List<Board> boards = boardService.findByBoardTypeWithPaging("공지사항", page, PAGE_SIZE);
        long totalElements = boardService.countByBoardType("공지사항");
        int totalPages = boardService.getTotalPages("공지사항", PAGE_SIZE);

        // 페이징 정보를 모델에 추가
        model.addAttribute("boards", boards);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages > 0 ? totalPages : 1);
        model.addAttribute("totalElements", totalElements);
        model.addAttribute("pageSize", PAGE_SIZE);

        return "notice";
    }

    // 등록 폼은 BoardController로 리다이렉트 (관리자만 접근 가능)
    @GetMapping("/notice/create")
    public String noticeCreateForm(HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");

        if (loginUser == null || !"관리자".equals(loginUser.getUserType())) {
            return "redirect:/notice";
        }

        return "redirect:/board/create?boardType=공지사항";
    }

    @GetMapping("/faq")
    public String faq() {
        return "faq";
    }
}