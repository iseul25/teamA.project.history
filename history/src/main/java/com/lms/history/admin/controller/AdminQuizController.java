package com.lms.history.admin.controller;

import com.lms.history.quizzes.entity.Category;
import com.lms.history.quizzes.service.QuizService;
import com.lms.history.users.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/quiz")
@RequiredArgsConstructor
public class AdminQuizController {

    private final QuizService quizService;
    private static final int PAGE_SIZE = 10;

    // 날짜 포맷 정의 (LocalDateTime용)
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * 퀴즈 목록 페이지
     */
    @GetMapping
    public String quizList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "전체") String category,
            Model model,
            HttpSession session) {

        // 관리자 권한 체크
        if (!isAdmin(session)) {
            return "redirect:/";
        }

        // 전체 또는 특정 카테고리 퀴즈 조회
        List<Category> allQuizzes = "전체".equals(category)
                ? quizService.findAll()
                : quizService.findByQuizType(category);

        // 페이징 처리
        int totalItems = allQuizzes.size();
        int totalPages = (int) Math.ceil((double) totalItems / PAGE_SIZE);
        int start = (page - 1) * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, totalItems);
        List<Category> pagedQuizzes = allQuizzes.subList(start, end);

        // DTO 변환 (필요한 필드만)
        List<Map<String, Object>> quizList = pagedQuizzes.stream()
                .map(quiz -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", quiz.getQuizCategoryId());
                    map.put("title", quiz.getQuizListName());
                    if (quiz.getCreateAt() != null) {
                        map.put("date", quiz.getCreateAt().format(DATE_FORMATTER));
                    } else {
                        map.put("date", "-");
                    }
                    map.put("category", quiz.getQuizType());
                    return map;
                })
                .collect(Collectors.toList());

        // 모델에 데이터 전달
        model.addAttribute("quizList", quizList);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pageSize", PAGE_SIZE);
        model.addAttribute("currentCategory", category);

        return "admin/adminQuizList";
    }

    /**
     * 퀴즈 삭제
     */
    @DeleteMapping("/{quizCategoryId}")
    @ResponseBody
    public ResponseEntity<Map<String, String>> deleteQuiz(
            @PathVariable int quizCategoryId,
            HttpSession session) {

        if (!isAdmin(session)) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "권한이 없습니다.");
            return ResponseEntity.status(403).body(error);
        }

        try {
            quizService.deleteByCategoryId(quizCategoryId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "삭제되었습니다.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "삭제에 실패했습니다.");
            return ResponseEntity.status(500).body(error);
        }
    }

    /**
     * 관리자 권한 체크
     */
    private boolean isAdmin(HttpSession session) {
        User user = (User) session.getAttribute("loginUser");
        return user != null && "관리자".equals(user.getUserType());
    }
}
