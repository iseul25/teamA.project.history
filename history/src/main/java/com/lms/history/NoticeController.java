package com.lms.history.boards.controller;

import com.lms.history.boards.entity.Board;
import com.lms.history.boards.service.BoardService;
import com.lms.history.users.entity.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class NoticeController {

    private final BoardService boardService;

    public NoticeController(BoardService boardService) {
        this.boardService = boardService;
    }

    @GetMapping("/notice")
    public String notice(@RequestParam(value = "page", defaultValue = "1") int page,
                         Model model, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        model.addAttribute("loginUser", loginUser);

        List<Board> boards = boardService.findByBoardType("공지사항");
        model.addAttribute("boards", boards);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", 1);

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