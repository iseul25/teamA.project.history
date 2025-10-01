package com.lms.history.admin.controller;

import com.lms.history.quizzes.entity.Category;
import com.lms.history.quizzes.entity.Quiz;
import com.lms.history.quizzes.repository.QuizRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 관리자 전용 퀴즈 등록/이미지 업로드 API
 * - 이미지 업로드: Multipart 단일 파일(한 번에 1장) -> URL 반환
 * - 카테고리 생성: JSON
 * - 문제 생성: JSON (문제 1건 또는 배열로 여러 건 등록 지원)
 *
 * ※ DTO 별도 파일 없이 Map/내부 정적 클래스로 처리
 */
@RestController
@RequestMapping("/quiz/api/admin")
public class AdminQuizApiController {

    private static final Logger log = LoggerFactory.getLogger(AdminQuizApiController.class);

    private final QuizRepository quizRepository;

    /**
     * 이미지 저장 루트 디렉토리 (실행 계정에 쓰기 권한 필요)
     * 예) application.properties에 app.upload.quiz-dir=C:/parkdotori/webDev/uploads/quiz
     * 미지정 시 ./uploads/quiz 로 저장
     */
    @Value("${app.upload.quiz-dir:uploads/quiz}")
    private String uploadRootDir;

    /**
     * 클라이언트에서 접근할 때 사용할 URL prefix
     * 정적 리소스 매핑(/uploads/**)이 되어 있다면 기본값 그대로 사용해도 됨.
     * 예) WebMvcConfigurer로 "file:uploads/" -> "/uploads/**" 매핑
     */
    @Value("${app.upload.quiz-url-prefix:/uploads/quiz}")
    private String uploadUrlPrefix;

    public AdminQuizApiController(QuizRepository quizRepository) {
        this.quizRepository = quizRepository;
    }

    // ---------------------------
    // 1) 이미지 업로드 (한 번에 한 장)
    // ---------------------------
    @PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "업로드할 파일이 없습니다."
            ));
        }

        try {
            // 저장 폴더 보장
            Path root = Paths.get(uploadRootDir).toAbsolutePath().normalize();
            Files.createDirectories(root);

            // 확장자 유지한 랜덤 파일명
            String original = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
            String ext = "";
            int dot = original.lastIndexOf('.');
            if (dot > -1) ext = original.substring(dot);
            String storedName = UUID.randomUUID().toString().replace("-", "") + ext;

            Path target = root.resolve(storedName);
            file.transferTo(target.toFile());

            // 접근 URL
            String publicUrl = (uploadUrlPrefix.endsWith("/"))
                    ? uploadUrlPrefix + storedName
                    : uploadUrlPrefix + "/" + storedName;

            log.info("Image uploaded: {} -> {}", original, publicUrl);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "url", publicUrl,
                    "originalName", original,
                    "storedName", storedName
            ));
        } catch (IOException e) {
            log.error("Image upload failed", e);
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "파일 저장 중 오류가 발생했습니다."
            ));
        }
    }

    // ---------------------------
    // 2) 카테고리 생성 (JSON)
    // body: { "userId":1, "quizType":"KOREAN_HISTORY", "quizListName":"삼국시대" }
    // ---------------------------
    @PostMapping(value = "/category", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createCategory(@RequestBody Map<String, Object> body) {
        try {
            Integer userId = asInt(body.get("userId"));
            String quizType = asStr(body.get("quizType"));
            String quizListName = asStr(body.get("quizListName"));

            if (userId == null || userId <= 0) {
                return bad("userId 가 유효하지 않습니다.");
            }
            if (isBlank(quizType)) {
                return bad("quizType 은 필수입니다.");
            }
            if (isBlank(quizListName)) {
                return bad("quizListName 은 필수입니다.");
            }

            Category c = new Category();
            c.setUserId(userId);
            c.setQuizType(quizType.trim());
            c.setQuizListName(quizListName.trim());
            c.setCreateAt(LocalDateTime.now());

            int quizCategoryId = quizRepository.saveCategory(c);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "quizCategoryId", quizCategoryId
            ));
        } catch (Exception e) {
            log.error("createCategory error", e);
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "카테고리 생성 실패"
            ));
        }
    }

    // ---------------------------
    // 3) 문제 생성 (단건 또는 다건)
    //
    // 단건: {
    //   "quizCategoryId": 10,
    //   "quizNumber": 1,
    //   "imgUrl": "/uploads/quiz/xxx.png",   // 이미지가 없으면 생략/빈문자 허용
    //   "question": "문제 내용",
    //   "item1": "...","item2":"...","item3":"...","item4":"...",
    //   "answer": 2,
    //   "quizScore": 10
    // }
    //
    // 다건: { "quizCategoryId":10, "items":[{위 단건 구조}, {..}] }
    // items 안의 각 객체는 quizCategoryId 생략 가능(바깥 값 상속)
    // ---------------------------
    @PostMapping(value = "/quiz", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createQuiz(@RequestBody Map<String, Object> body) {
        try {
            Integer outerCategoryId = asInt(body.get("quizCategoryId"));

            // 다건 등록?
            Object items = body.get("items");
            if (items instanceof Collection<?> list) {
                if (outerCategoryId == null || outerCategoryId <= 0) {
                    return bad("quizCategoryId 가 필요합니다.");
                }
                int created = 0;
                for (Object o : list) {
                    if (!(o instanceof Map)) continue;
                    Map<?, ?> m = (Map<?, ?>) o;
                    created += insertOneQuiz(outerCategoryId, m);
                }
                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "created", created
                ));
            }

            // 단건 등록
            if (outerCategoryId == null || outerCategoryId <= 0) {
                return bad("quizCategoryId 가 필요합니다.");
            }
            int n = insertOneQuiz(outerCategoryId, body);
            return ResponseEntity.ok(Map.of(
                    "success", n == 1,
                    "created", n
            ));
        } catch (Exception e) {
            log.error("createQuiz error", e);
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "문제 생성 실패"
            ));
        }
    }

    // ========== 내부 유틸 ==========

    private int insertOneQuiz(Integer outerCategoryId, Map<?, ?> m) {
        Integer quizCategoryId = outerCategoryId;
        Integer qn = asInt(m.get("quizNumber"));
        String imgUrl = trimToNull(asStr(firstNonNull(m.get("imgUrl"), m.get("image"), m.get("imageUrl"), m.get("img"))));
        String question = asStr(firstNonNull(m.get("question"), m.get("text")));
        String item1 = asStr(firstNonNull(m.get("item1"), m.get("option1")));
        String item2 = asStr(firstNonNull(m.get("item2"), m.get("option2")));
        String item3 = asStr(firstNonNull(m.get("item3"), m.get("option3")));
        String item4 = asStr(firstNonNull(m.get("item4"), m.get("option4")));
        Integer answer = asInt(m.get("answer"));
        Integer quizScore = asInt(firstNonNull(m.get("quizScore"), m.get("score")));

        // m에 quizCategoryId가 따로 있으면 덮어쓰기
        Integer innerCat = asInt(m.get("quizCategoryId"));
        if (innerCat != null && innerCat > 0) {
            quizCategoryId = innerCat;
        }

        if (quizCategoryId == null || quizCategoryId <= 0) {
            throw new IllegalArgumentException("quizCategoryId 없음");
        }
        if (isBlank(question)) {
            throw new IllegalArgumentException("question 없음");
        }
        if (isBlank(item1) || isBlank(item2) || isBlank(item3) || isBlank(item4)) {
            throw new IllegalArgumentException("보기 4개는 필수");
        }
        if (answer == null || answer < 1 || answer > 4) {
            throw new IllegalArgumentException("answer(1~4) 필수");
        }

        Quiz q = new Quiz();
        q.setQuizCategoryId(quizCategoryId);
        q.setQuizNumber(qn);               // null 가능
        q.setImgUrl(imgUrl);               // null/빈값 가능
        q.setQuestion(question);
        q.setItem1(item1);
        q.setItem2(item2);
        q.setItem3(item3);
        q.setItem4(item4);
        q.setAnswer(answer);
        q.setQuizScore(quizScore);         // null 가능

        quizRepository.saveQuiz(q);
        return 1;
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private static String asStr(Object o) {
        return (o == null) ? null : String.valueOf(o);
    }

    private static Integer asInt(Object o) {
        if (o == null) return null;
        if (o instanceof Integer i) return i;
        if (o instanceof Number n) return n.intValue();
        try {
            String s = String.valueOf(o).trim();
            if (s.isEmpty()) return null;
            return Integer.parseInt(s);
        } catch (Exception e) {
            return null;
        }
    }

    private static Object firstNonNull(Object... vals) {
        for (Object v : vals) if (v != null) return v;
        return null;
    }

    private static String trimToNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    private ResponseEntity<Map<String, Object>> bad(String msg) {
        return ResponseEntity.badRequest().body(Map.of("success", false, "message", msg));
    }

    // ---------------------------
// 4) 문제 수정 (PUT)
// body: {
//   "quizId": 123,
//   "question": "수정된 문제",
//   "item1": "...", "item2": "...", "item3": "...", "item4": "...",
//   "answer": 3,
//   "imgUrl": "/uploads/quiz/xxx.png"  // 선택
// }
// ---------------------------
    @PutMapping(value = "/quiz/{quizId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateQuiz(@PathVariable int quizId, @RequestBody Map<String, Object> body) {
        try {
            // 기존 문제 조회
            Quiz existing = quizRepository.findQuizById(quizId);
            if (existing == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "해당 문제를 찾을 수 없습니다."
                ));
            }

            // 수정 데이터 적용
            String question = asStr(body.get("question"));
            if (!isBlank(question)) {
                existing.setQuestion(question);
            }

            String item1 = asStr(body.get("item1"));
            String item2 = asStr(body.get("item2"));
            String item3 = asStr(body.get("item3"));
            String item4 = asStr(body.get("item4"));

            if (!isBlank(item1)) existing.setItem1(item1);
            if (!isBlank(item2)) existing.setItem2(item2);
            if (!isBlank(item3)) existing.setItem3(item3);
            if (!isBlank(item4)) existing.setItem4(item4);

            Integer answer = asInt(body.get("answer"));
            if (answer != null && answer >= 1 && answer <= 4) {
                existing.setAnswer(answer);
            }

            // 이미지 URL (선택)
            if (body.containsKey("imgUrl")) {
                String imgUrl = trimToNull(asStr(body.get("imgUrl")));
                existing.setImgUrl(imgUrl);
            }

            // DB 업데이트
            quizRepository.updateQuiz(existing);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "문제가 수정되었습니다."
            ));
        } catch (Exception e) {
            log.error("updateQuiz error", e);
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "문제 수정 실패: " + e.getMessage()
            ));
        }
    }

    // ---------------------------
// 5) 특정 카테고리의 문제 목록 조회
// ---------------------------
    @GetMapping("/quiz/category/{quizCategoryId}")
    public ResponseEntity<?> getQuizzesByCategory(@PathVariable int quizCategoryId) {
        try {
            List<Quiz> quizzes = quizRepository.findQuizListByCategoryId(quizCategoryId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "quizzes", quizzes
            ));
        } catch (Exception e) {
            log.error("getQuizzesByCategory error", e);
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "문제 조회 실패"
            ));
        }
    }
}