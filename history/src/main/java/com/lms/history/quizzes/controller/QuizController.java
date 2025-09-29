package com.lms.history.quizzes.controller;

import com.lms.history.boards.service.BoardService;
import com.lms.history.boards.service.BoardStudyService;
import com.lms.history.boards.entity.Board;
import com.lms.history.quizzes.entity.Attempt;
import com.lms.history.quizzes.entity.Category;
import com.lms.history.quizzes.entity.Quiz;
import com.lms.history.quizzes.entity.Score;
import com.lms.history.quizzes.service.QuizService;
import com.lms.history.users.entity.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/quiz")
public final class QuizController {
    private final QuizService quizService;
    private final BoardService boardService;
    private final BoardStudyService boardStudyService;

    public QuizController(QuizService quizService, BoardService boardService, BoardStudyService boardStudyService) {
        this.quizService = quizService;
        this.boardService = boardService;
        this.boardStudyService = boardStudyService;
    }

    // 시대 선택 페이지
    @GetMapping("/topic")
    public String quizTopic(HttpSession session, Model model) {
        return "quiz/quizTopic";
    }

    @GetMapping("/list")
    public String list(@RequestParam(value = "quizType", required=false) String quizType,
                       @RequestParam(value = "page", defaultValue = "1") int page,
                       HttpSession session,
                       Model model) {

        try {
            session.setAttribute("selectedQuizType", quizType);

            // 로그인 사용자 확인
            Object loginUserObj = session.getAttribute("loginUser");
            int userId = 0;
            boolean isAdmin = false;

            if (loginUserObj != null) {
                try {
                    if (loginUserObj instanceof User) {
                        User user = (User) loginUserObj;
                        userId = user.getUserId();
                        isAdmin = "관리자".equals(user.getUserType());
                    } else {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> userMap = (Map<String, Object>) loginUserObj;
                        userId = (Integer) userMap.get("userId");
                        isAdmin = "관리자".equals(userMap.get("userType"));
                    }
                } catch (Exception e) {
                    userId = 0;
                }
            }

            // quizType 검증
            if (quizType != null) {
                quizType = quizType.trim();
                if (quizType.isEmpty()) {
                    quizType = null;
                }
            }

            // 카테고리 + score 조회
            List<Map<String, Object>> allCategories = quizService.findByQuizTypeWithScores(quizType, userId);

            if (allCategories == null || allCategories.isEmpty()) {
                model.addAttribute("categories", new ArrayList<>());
                model.addAttribute("currentPage", 1);
                model.addAttribute("totalPages", 1);
                model.addAttribute("message", "해당 타입의 퀴즈 카테고리가 없습니다.");
            } else {
                // ✅ 학습 완료 여부 확인 추가 (일반 사용자만)
                if (userId > 0 && !isAdmin) {
                    for (Map<String, Object> category : allCategories) {
                        String quizListName = (String) category.get("quizListName");

                        // 같은 제목의 게시글 찾기
                        Board board = boardService.findByTitleAndType(quizListName, quizType);
                        boolean canTakeQuiz = false;

                        if (board != null) {
                            // 학습 완료 여부 확인 (startAt과 endAt이 다른 경우)
                            canTakeQuiz = boardStudyService.hasCompletedStudy(board.getBoardId(), userId);
                        }

                        category.put("canTakeQuiz", canTakeQuiz);
                    }
                } else {
                    // 관리자는 모든 퀴즈 접근 가능
                    for (Map<String, Object> category : allCategories) {
                        category.put("canTakeQuiz", true);
                    }
                }

                int pageSize = 10;
                int totalCategories = allCategories.size();
                int totalPages = Math.max(1, (int) Math.ceil((double) totalCategories / pageSize));
                page = Math.max(1, Math.min(page, totalPages));

                int startIdx = (page - 1) * pageSize;
                int endIdx = Math.min(startIdx + pageSize, totalCategories);
                List<Map<String, Object>> categoriesPage = allCategories.subList(startIdx, endIdx);

                model.addAttribute("categories", categoriesPage);
                model.addAttribute("currentPage", page);
                model.addAttribute("totalPages", totalPages);
            }

            model.addAttribute("selectedQuizType", quizType);

        } catch (Exception e) {
            model.addAttribute("error", "퀴즈 카테고리를 불러오는 중 오류가 발생했습니다: " + e.getMessage());
            return "redirect:/";
        }

        return "quiz/listQuiz";
    }

    @PostMapping("/delete")
    public String delete(@RequestParam("quizCategoryId") int quizCategoryId,
                         @RequestParam("quizType") String quizType,
                         RedirectAttributes redirectAttributes) {

        quizService.deleteByCategoryId(quizCategoryId);

        redirectAttributes.addAttribute("quizType", quizType);
        return "redirect:/quiz/list";
    }

    @GetMapping("/status/{quizCategoryId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getQuizStatus(
            @PathVariable int quizCategoryId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            HttpSession session) {

        try {
            int pageSize = 10;
            List<Map<String, Object>> allScores = quizService.findScoresByQuizCategoryId(quizCategoryId);

            Map<String, Object> response = new HashMap<>();

            if (allScores == null || allScores.isEmpty()) {
                response.put("scores", new ArrayList<>());
                response.put("currentPage", 1);
                response.put("totalPages", 1);
                response.put("totalCount", 0);
                response.put("message", "응시한 학생이 없습니다.");
            } else {
                int totalScores = allScores.size();
                int totalPages = Math.max(1, (int) Math.ceil((double) totalScores / pageSize));
                page = Math.max(1, Math.min(page, totalPages));

                int startIdx = (page - 1) * pageSize;
                int endIdx = Math.min(startIdx + pageSize, totalScores);
                List<Map<String, Object>> scoresPage = allScores.subList(startIdx, endIdx);

                response.put("scores", scoresPage);
                response.put("currentPage", page);
                response.put("totalPages", totalPages);
                response.put("totalCount", totalScores);
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "성적 현황을 불러오는 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/status/page/{quizCategoryId}")
    public String getQuizStatusPage(
            @PathVariable int quizCategoryId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            HttpSession session,
            Model model) {

        try {
            int pageSize = 10;
            List<Map<String, Object>> allScores = quizService.findScoresByQuizCategoryId(quizCategoryId);

            if (allScores == null || allScores.isEmpty()) {
                model.addAttribute("scores", new ArrayList<>());
                model.addAttribute("currentPage", 1);
                model.addAttribute("totalPages", 1);
                model.addAttribute("message", "응시한 학생이 없습니다.");
            } else {
                int totalScores = allScores.size();
                int totalPages = Math.max(1, (int) Math.ceil((double) totalScores / pageSize));
                page = Math.max(1, Math.min(page, totalPages));

                int startIdx = (page - 1) * pageSize;
                int endIdx = Math.min(startIdx + pageSize, totalScores);
                List<Map<String, Object>> scoresPage = allScores.subList(startIdx, endIdx);

                model.addAttribute("scores", scoresPage);
                model.addAttribute("currentPage", page);
                model.addAttribute("totalPages", totalPages);
            }

            model.addAttribute("quizCategoryId", quizCategoryId);

        } catch (Exception e) {
            model.addAttribute("error", "성적 현황을 불러오는 중 오류가 발생했습니다: " + e.getMessage());
            return "redirect:/quiz/list";
        }

        return "quiz/quizStatus";
    }
}