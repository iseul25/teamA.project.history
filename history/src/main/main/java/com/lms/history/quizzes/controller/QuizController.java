package com.lms.history.quizzes.controller;

import com.lms.history.boards.service.BoardService;
import com.lms.history.boards.service.BoardStudyService;
import com.lms.history.boards.entity.Board;
import com.lms.history.quizzes.entity.Attempt;
import com.lms.history.quizzes.entity.Category;
import com.lms.history.quizzes.entity.Quiz;
import com.lms.history.quizzes.service.QuizService;
import com.lms.history.users.entity.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/quiz")
public final class QuizController {
    private final QuizService quizService;
    private final BoardService boardService;
    private final BoardStudyService boardStudyService;
    private final JdbcTemplate jdbcTemplate;

    public QuizController(QuizService quizService, BoardService boardService, BoardStudyService boardStudyService, JdbcTemplate jdbcTemplate) {
        this.quizService = quizService;
        this.boardService = boardService;
        this.boardStudyService = boardStudyService;
        this.jdbcTemplate = jdbcTemplate;
    }

    // 시대 선택 페이지
    @GetMapping("/topic")
    public String quizTopic(HttpSession session, Model model) {
        return "quiz/quizTopic";
    }

    @GetMapping("/list")
    public String list(@RequestParam(value = "quizType", required = false) String quizType,
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
                // 학습 완료 여부 확인 추가 (일반 사용자만)
                if (userId > 0 && !isAdmin) {
                    for (Map<String, Object> category : allCategories) {
                        String quizListName = (String) category.get("quizListName");

                        // 같은 제목의 게시글 찾기
                        Board board = boardService.findByTitleAndType(quizListName, quizType);
                        boolean canTakeQuiz = false;

                        if (board != null) {
                            // 학습 완료 여부 확인
                            canTakeQuiz = boardStudyService.hasCompletedStudy(board.getBoardId(), userId);

                            // 디버깅 로그
                            System.out.println("=== 학습 완료 체크 ===");
                            System.out.println("퀴즈명: " + quizListName);
                            System.out.println("퀴즈타입: " + quizType);
                            System.out.println("찾은 게시글 ID: " + board.getBoardId());
                            System.out.println("게시글 제목: " + board.getTitle());
                            System.out.println("사용자 ID: " + userId);
                            System.out.println("학습 완료 여부: " + canTakeQuiz);
                            System.out.println("=====================");
                        } else {
                            System.out.println("경고: '" + quizListName + "' (타입: " + quizType + ") 제목의 게시글을 찾을 수 없습니다.");
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

        return "quiz/quizList";
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

    // ========== 퀴즈 풀이 관련 API ==========

    // 퀴즈 시작 페이지
    @GetMapping("/start")
    public String startQuiz(@RequestParam("quizCategoryId") int quizCategoryId,
                            HttpSession session,
                            Model model,  // ← 이 파라미터 추가!
                            RedirectAttributes redirectAttributes) {

        Object loginUserObj = session.getAttribute("loginUser");
        if (loginUserObj == null) {
            return "redirect:/";
        }

        int userId = 0;
        try {
            if (loginUserObj instanceof User) {
                userId = ((User) loginUserObj).getUserId();
            } else {
                @SuppressWarnings("unchecked")
                Map<String, Object> userMap = (Map<String, Object>) loginUserObj;
                userId = (Integer) userMap.get("userId");
            }
        } catch (Exception e) {
            return "redirect:/";
        }

        // 수료 여부 확인
        try {
            String passStatus = quizService.checkPassStatus(userId, quizCategoryId);
            if ("수료".equals(passStatus)) {
                Category category = quizService.findQuizCategoryById(quizCategoryId);
                if (category != null) {
                    redirectAttributes.addAttribute("quizType", category.getQuizType());
                    redirectAttributes.addAttribute("error", "already_passed");
                }
                return "redirect:/quiz/list";
            }
        } catch (Exception e) {
            // 수료 기록이 없으면 정상 진행
        }

        model.addAttribute("quizCategoryId", quizCategoryId);
        return "quiz/quizSolve";
    }

    // 퀴즈 문제 조회 API
    @GetMapping("/api/questions")
    @ResponseBody
    public ResponseEntity<?> getQuizQuestions(
            @RequestParam("quizCategoryId") int quizCategoryId,
            HttpSession session) {

        System.out.println("=== 퀴즈 문제 조회 API 호출 ===");
        System.out.println("quizCategoryId: " + quizCategoryId);

        try {
            Object loginUserObj = session.getAttribute("loginUser");
            if (loginUserObj == null) {
                System.out.println("로그인 안됨");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "로그인이 필요합니다."));
            }
            System.out.println("로그인 확인됨");

            // 카테고리 정보 조회
            System.out.println("카테고리 조회 시작...");
            Category category = quizService.findQuizCategoryById(quizCategoryId);
            if (category == null) {
                System.out.println("카테고리를 찾을 수 없음");
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "퀴즈를 찾을 수 없습니다."));
            }
            System.out.println("카테고리 찾음: " + category.getQuizListName());

            // 퀴즈 문제 목록 조회
            System.out.println("퀴즈 문제 조회 시작...");
            List<Quiz> quizzes = quizService.findQuizListByCategoryId(quizCategoryId);
            System.out.println("조회된 문제 수: " + (quizzes != null ? quizzes.size() : 0));

            if (quizzes == null || quizzes.isEmpty()) {
                System.out.println("문제가 없음 - 빈 배열 반환");
                return ResponseEntity.ok(Map.of(
                        "title", category.getQuizListName(),
                        "questions", new ArrayList<>()
                ));
            }

            // 응답 데이터 구성 (정답은 제외)
            List<Map<String, Object>> questions = new ArrayList<>();
            for (Quiz quiz : quizzes) {
                System.out.println("문제 처리 중: quizId=" + quiz.getQuizId() + ", question=" + quiz.getQuestion());
                Map<String, Object> q = new HashMap<>();
                q.put("quizId", quiz.getQuizId());
                q.put("text", quiz.getQuestion());
                q.put("image", quiz.getImgUrl());
                q.put("options", List.of(
                        quiz.getItem1(),
                        quiz.getItem2(),
                        quiz.getItem3(),
                        quiz.getItem4()
                ));
                q.put("answer", quiz.getAnswer());  // ← 이 줄 추가!
                questions.add(q);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("title", category.getQuizListName());
            response.put("questions", questions);

            System.out.println("성공적으로 응답 반환: " + questions.size() + "개 문제");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("===== 오류 발생 =====");
            System.err.println("오류 메시지: " + e.getMessage());
            System.err.println("오류 타입: " + e.getClass().getName());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "문제를 불러오는 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    // 퀴즈 제출 및 채점 API
    @PostMapping("/api/submit")
    @ResponseBody
    public ResponseEntity<?> submitQuiz(
            @RequestBody Map<String, Object> payload,
            HttpSession session) {

        try {
            Object loginUserObj = session.getAttribute("loginUser");
            if (loginUserObj == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "로그인이 필요합니다."));
            }

            int userId;
            if (loginUserObj instanceof User) {
                userId = ((User) loginUserObj).getUserId();
            } else {
                @SuppressWarnings("unchecked")
                Map<String, Object> userMap = (Map<String, Object>) loginUserObj;
                userId = (Integer) userMap.get("userId");
            }

            int quizCategoryId = (Integer) payload.get("quizCategoryId");
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> answers = (List<Map<String, Object>>) payload.get("answers");

            if (answers == null || answers.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "답안이 제출되지 않았습니다."));
            }

            // 기존 시도 기록 삭제 (트리거가 자동으로 score도 삭제)
            quizService.deleteAttemptsByUserAndCategory(userId, quizCategoryId);

            // 새로운 시도 기록 저장 및 채점
            int correctCount = 0;
            int totalScore = 0;

            List<Quiz> quizzes = quizService.findQuizListByCategoryId(quizCategoryId);
            Map<Integer, Quiz> quizMap = new HashMap<>();
            for (Quiz q : quizzes) {
                quizMap.put(q.getQuizId(), q);
            }

            List<Map<String, Object>> reviewList = new ArrayList<>();

            for (Map<String, Object> answer : answers) {
                int quizId = (Integer) answer.get("quizId");
                int picked = (Integer) answer.get("picked");

                Quiz quiz = quizMap.get(quizId);
                if (quiz == null) continue;

                boolean isCorrect = (quiz.getAnswer() != null && quiz.getAnswer().intValue() == picked);

                int scoreValue = (quiz.getQuizScore() != null && quiz.getQuizScore() > 0)
                        ? quiz.getQuizScore()
                        : 10;
                int earnedScore = isCorrect ? scoreValue : 0;

                if (isCorrect) {
                    correctCount++;
                    totalScore += earnedScore;
                }

                // attempt 저장
                Attempt attempt = new Attempt();
                attempt.setUserId(userId);
                attempt.setQuizCategoryId(quizCategoryId);
                attempt.setQuizId(quizId);
                attempt.setSelected(picked);
                attempt.setAttemptAt(LocalDateTime.now());
                quizService.saveAttempt(attempt);

                // 리뷰 데이터 생성
                Map<String, Object> review = new HashMap<>();
                review.put("quizId", quizId);
                review.put("correct", isCorrect);

                String pickedText = getOptionText(quiz, picked);
                String correctText = getOptionText(quiz, quiz.getAnswer() != null ? quiz.getAnswer() : -1);

                review.put("picked", pickedText);
                review.put("correctAnswer", correctText);
                review.put("explanation", quiz.getCommentary());

                reviewList.add(review);
            }

            // 포인트 계산 (60점 이상만 지급)
            int earnedPoints = totalScore >= 60 ? totalScore : 0;

            // ✅ 포인트를 points 테이블에 저장
            if (earnedPoints > 0) {
                // quiz_score 테이블에서 방금 생성된 scoreId 조회
                String findScoreIdSql = "SELECT scoreId FROM quiz_score WHERE userId = ? AND quizCategoryId = ? ORDER BY scoreId DESC LIMIT 1";
                Integer scoreId = jdbcTemplate.queryForObject(findScoreIdSql, Integer.class, userId, quizCategoryId);

                // 현재 총 포인트 조회
                String getTotalPointSql = "SELECT COALESCE(SUM(pointChange), 0) FROM points WHERE userId = ?";
                int currentTotalPoint = jdbcTemplate.queryForObject(getTotalPointSql, Integer.class, userId);
                int newTotalPoint = currentTotalPoint + earnedPoints;

                // points 테이블에 INSERT
                String insertPointSql = "INSERT INTO points (userId, scoreId, pointChange, totalPoint, createAt) VALUES (?, ?, ?, ?, NOW())";
                jdbcTemplate.update(insertPointSql, userId, scoreId, earnedPoints, newTotalPoint);
            }

            // 응답 데이터
            Map<String, Object> response = new HashMap<>();
            response.put("totalScore", totalScore);
            response.put("correctCount", correctCount);
            response.put("earnedPoints", earnedPoints);
            response.put("pass", totalScore >= 60);
            response.put("review", reviewList);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "제출 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    // ⭐ 5. 보기 번호를 텍스트로 변환하는 헬퍼 메서드 추가
    private String getOptionText(Quiz quiz, int optionNumber) {
        return switch (optionNumber) {
            case 1 -> quiz.getItem1();
            case 2 -> quiz.getItem2();
            case 3 -> quiz.getItem3();
            case 4 -> quiz.getItem4();
            default -> "-";
        };
    }

    // ========== 퀴즈 등록 관련 API ==========

    // 퀴즈 일괄 등록 API (관리자용)
    @PostMapping("/create-bulk")
    public String createBulkQuiz(
            @RequestParam("quizType") String quizType,
            @RequestParam("title") String quizTitle,
            @RequestParam Map<String, String> params,
            @RequestParam(required = false) Map<String, MultipartFile> files,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        try {
            Object loginUserObj = session.getAttribute("loginUser");
            if (loginUserObj == null) {
                redirectAttributes.addFlashAttribute("error", "로그인이 필요합니다.");
                return "redirect:/";
            }

            int userId;
            boolean isAdmin;
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

            if (!isAdmin) {
                redirectAttributes.addFlashAttribute("error", "권한이 없습니다.");
                // ✅ 한글 인코딩 문제 방지: 쿼리 파라미터는 addAttribute로
                redirectAttributes.addAttribute("quizType", quizType);
                return "redirect:/quiz/list";
            }

            // 카테고리 생성
            Category category = new Category();
            category.setUserId(userId);
            category.setQuizType(quizType);
            category.setQuizListName(quizTitle);
            category.setCreateAt(LocalDateTime.now());

            int quizCategoryId = quizService.saveCategory(category);

            // 10개 문제 저장
            for (int i = 1; i <= 10; i++) {
                Quiz quiz = new Quiz();
                quiz.setQuizCategoryId(quizCategoryId);
                quiz.setQuizNumber(i);
                quiz.setQuestion(params.getOrDefault("quizText" + i, null));
                quiz.setItem1(params.getOrDefault("option1_" + i, null));
                quiz.setItem2(params.getOrDefault("option2_" + i, null));
                quiz.setItem3(params.getOrDefault("option3_" + i, null));
                quiz.setItem4(params.getOrDefault("option4_" + i, null));
                quiz.setAnswer(Integer.parseInt(params.get("answer" + i)));
                quiz.setQuizScore(10); // 각 문제당 10점

                // 이미지 처리
                if (files != null) {
                    MultipartFile imageFile = files.get("quizImage" + i);
                    if (imageFile != null && !imageFile.isEmpty()) {
                        String imagePath = saveQuizImage(imageFile);
                        quiz.setImgUrl(imagePath);
                    }
                }

                quizService.saveQuiz(quiz);
            }

            redirectAttributes.addFlashAttribute("success", "퀴즈가 등록되었습니다.");
            // ✅ 성공 리다이렉트
            redirectAttributes.addAttribute("quizType", quizType);
            return "redirect:/quiz/list";

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "퀴즈 등록 중 오류: " + e.getMessage());
            // ✅ 실패 리다이렉트
            redirectAttributes.addAttribute("quizType", quizType);
            return "redirect:/quiz/list";
        }
    }

    // 이미지 저장 헬퍼 메서드
    private String saveQuizImage(MultipartFile file) throws IOException {
        String uploadDir = "uploads/quiz/";
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path uploadPath = Paths.get(uploadDir);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath);

        return "/" + uploadDir + fileName;
    }

    @GetMapping("/api/check-pass")
    @ResponseBody
    public ResponseEntity<?> checkPass(@RequestParam("quizCategoryId") int quizCategoryId,
                                       HttpSession session) {
        try {
            Object loginUserObj = session.getAttribute("loginUser");
            if (loginUserObj == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "로그인이 필요합니다."));
            }

            int userId;
            if (loginUserObj instanceof User) {
                userId = ((User) loginUserObj).getUserId();
            } else {
                @SuppressWarnings("unchecked")
                Map<String, Object> userMap = (Map<String, Object>) loginUserObj;
                userId = (Integer) userMap.get("userId");
            }

            String passStatus = quizService.checkPassStatus(userId, quizCategoryId);
            boolean passed = "수료".equals(passStatus);

            return ResponseEntity.ok(Map.of("passed", passed));

        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("passed", false));
        }
    }
}