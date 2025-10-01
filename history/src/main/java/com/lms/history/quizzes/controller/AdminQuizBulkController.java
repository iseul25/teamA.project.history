package com.lms.history.quizzes.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lms.history.quizzes.entity.Category;
import com.lms.history.quizzes.entity.Quiz;
import com.lms.history.quizzes.repository.QuizRepository;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * DTO 없이 JSON(Map) + 개별 이미지 업로드로 퀴즈 일괄 등록
 * 보상 트랜잭션 로직을 포함하여 데이터 정합성 문제 해결
 */
@RestController
@RequestMapping
public class AdminQuizBulkController {

    private final QuizRepository quizRepository;
    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 파일 저장 루트 (원하면 application.properties로 분리 가능)
    private static final Path UPLOAD_ROOT = Paths.get("uploads/quiz");

    public AdminQuizBulkController(QuizRepository quizRepository, JdbcTemplate jdbcTemplate) {
        this.quizRepository = quizRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 1) JSON 본문만 받아 카테고리/문제 저장 (DB 잔존 문제 해결)
     * - 저장 중 예외 발생 시, 이미 커밋된 카테고리를 삭제하는 보상 로직 추가
     * - 성공 시 한글이 포함된 리다이렉트 대신 JSON 응답을 반환하여 Tomcat 인코딩 오류 방지
     */
    @PostMapping(value = "/quiz/create-bulk-json", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createBulkJson(@RequestBody Map<String, Object> body) {
        // 필수값 추출
        String quizType = asString(body.get("quizType"));
        String title = asString(body.get("title"));
        Integer userId = asInt(body.get("userId"), 0);

        if (!StringUtils.hasText(quizType) || !StringUtils.hasText(title)) {
            return ResponseEntity.badRequest().body(Map.of("error", "quizType, title은 필수입니다."));
        }

        int quizCategoryId = 0; // 카테고리 ID를 저장할 변수 초기화

        try {
            // 카테고리 저장 (이 시점에 DB에 커밋됨)
            Category category = new Category();
            category.setUserId(userId != null ? userId : 0);
            category.setQuizType(quizType);
            category.setQuizListName(title);
            category.setCreateAt(LocalDateTime.now());
            quizCategoryId = quizRepository.saveCategory(category);

            // 문제들 저장
            List<?> questions = asList(body.get("questions"));
            int number = 1;
            if (questions != null) {
                for (Object qObj : questions) {
                    Map<String, Object> q = castMap(qObj);

                    String text = asString(q.get("text"));
                    List<?> opts = asList(q.get("options"));
                    Integer answer = asInt(q.get("answer"), null);

                    // 옵션 4개 보정
                    String item1 = getOpt(opts, 0);
                    String item2 = getOpt(opts, 1);
                    String item3 = getOpt(opts, 2);
                    String item4 = getOpt(opts, 3);

                    Quiz quiz = new Quiz();
                    quiz.setQuizCategoryId(quizCategoryId);
                    quiz.setQuizNumber(number++);
                    quiz.setImgUrl(null);
                    quiz.setQuestion(text);
                    quiz.setItem1(item1);
                    quiz.setItem2(item2);
                    quiz.setItem3(item3);
                    quiz.setItem4(item4);
                    quiz.setAnswer(answer);
                    quiz.setQuizScore(10);

                    quizRepository.saveQuiz(quiz); // 각 문제가 DB에 커밋됨
                }
            }

            // 💡 성공: JSON 응답만 반환하여 Tomcat 헤더 인코딩 오류 방지
            return ResponseEntity.ok(Map.of(
                    "quizCategoryId", quizCategoryId,
                    "message", "본문(이미지 제외) 저장 완료"
            ));

        } catch (Exception e) {
            // 💡 예외 발생: DB에 데이터가 부분적으로 커밋된 상태일 가능성이 높음
            if (quizCategoryId != 0) {
                // 💡 보상 로직: 이미 저장된 카테고리와 문제를 삭제합니다.
                try {
                    quizRepository.deleteAllQuizzesByCategoryId(quizCategoryId);
                    quizRepository.deleteCategoryById(quizCategoryId);
                    // 파일 시스템 롤백은 이미지 업로드 실패 시 클라이언트가 담당
                } catch (Exception rollbackException) {
                    // 롤백 실패는 별도로 로그 기록
                    System.err.println("CRITICAL ROLLBACK FAILED for Category ID: " + quizCategoryId);
                    rollbackException.printStackTrace();
                }
            }

            // 500 Internal Server Error 반환
            return ResponseEntity.internalServerError().body(Map.of(
                    "error", "퀴즈 저장 중 오류 발생: " + e.getMessage(),
                    "details", "DB에 커밋된 데이터는 롤백되었거나(성공 시) 부분적으로 남아있을 수 있습니다(롤백 실패 시)."
            ));
        }
    }

    /**
     * 2) 개별 이미지 업로드 (문제별 한 장씩)
     * - 파일 I/O 실패 시: DB의 imgUrl을 NULL로 되돌리는 보상 로직 포함
     */
    @PostMapping(value = "/quiz/upload-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadImage(@RequestParam int quizCategoryId,
                                         @RequestParam int quizNumber,
                                         @RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "빈 파일입니다."));
        }

        Path dest = null;
        String filename = null;

        try {
            // 저장 경로 생성 및 파일명 생성 로직은 동일
            Path dir = UPLOAD_ROOT.resolve(String.valueOf(quizCategoryId));
            Files.createDirectories(dir);
            String original = Optional.ofNullable(file.getOriginalFilename()).orElse("image");
            String safeName = original.replaceAll("[\\r\\n]", "_");
            filename = quizNumber + "_" + System.currentTimeMillis() + "_" + safeName;
            dest = dir.resolve(filename);
            Files.copy(file.getInputStream(), dest, StandardCopyOption.REPLACE_EXISTING);

            String imgUrl = "/uploads/quiz/" + quizCategoryId + "/" + filename;

            // 해당 문제 imgUrl 업데이트
            String sql = "UPDATE quiz SET imgUrl = ? WHERE quizCategoryId = ? AND quizNumber = ?";
            int updated = jdbcTemplate.update(sql, imgUrl, quizCategoryId, quizNumber);
            if (updated == 0) {
                // 해당 번호 문제 없으면 저장된 파일 삭제 및 에러 반환
                try { Files.deleteIfExists(dest); } catch (Exception ignored) {}
                return ResponseEntity.badRequest().body(Map.of("error", "해당 quizNumber 문제가 없습니다."));
            }

            return ResponseEntity.ok(Map.of(
                    "quizCategoryId", quizCategoryId,
                    "quizNumber", quizNumber,
                    "imgUrl", imgUrl,
                    "message", "이미지 업로드 완료"
            ));

        } catch (IOException e) {
            // ❌ 파일 I/O 실패 시: 파일 시스템 정리 및 DB 보상 로직 실행
            if (dest != null) {
                try { Files.deleteIfExists(dest); } catch (Exception ignored) {}
            }

            // ❌ 보상 트랜잭션: 이미 커밋된 레코드의 imgUrl을 NULL로 되돌려 실패를 반영
            String rollbackSql = "UPDATE quiz SET imgUrl = NULL WHERE quizCategoryId = ? AND quizNumber = ?";
            jdbcTemplate.update(rollbackSql, quizCategoryId, quizNumber);

            // 클라이언트에게 실패 보고
            return ResponseEntity.internalServerError().body(Map.of(
                    "error", "파일 저장 중 오류가 발생했습니다: " + e.getMessage()
            ));
        }
    }

    /**
     * 3) 퀴즈 카테고리 ID 전체 삭제 (보상 트랜잭션용)
     */
    @PostMapping(value = "/quiz/rollback-category")
    public ResponseEntity<?> rollbackCategory(@RequestParam int quizCategoryId) {
        int deletedQuizzes = 0;
        int deletedCategory = 0;

        try {
            // 1. DB 롤백: 문제와 카테고리 삭제
            deletedQuizzes = quizRepository.deleteAllQuizzesByCategoryId(quizCategoryId);
            deletedCategory = quizRepository.deleteCategoryById(quizCategoryId);

            // 2. 파일 시스템 롤백: 카테고리 폴더 삭제
            Path dir = UPLOAD_ROOT.resolve(String.valueOf(quizCategoryId));
            if (Files.exists(dir)) {
                Files.walk(dir)
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(java.io.File::delete);
            }

            return ResponseEntity.ok(Map.of(
                    "quizCategoryId", quizCategoryId,
                    "deletedQuizzes", deletedQuizzes,
                    "deletedCategory", deletedCategory,
                    "message", "등록 실패에 따른 카테고리 및 문제 롤백 완료"
            ));
        } catch (Exception e) {
            // 💡 롤백 자체 실패 시 500 에러 반환
            return ResponseEntity.internalServerError().body(Map.of(
                    "error", "롤백 중 심각한 오류 발생. 데이터 불일치 가능성 높음: " + e.getMessage(),
                    "quizCategoryId", quizCategoryId
            ));
        }
    }


    // ===== 유틸(캐스팅/안전 파싱) =====
    // ... (유틸 메서드는 변경 없음)
    private static String asString(Object o) {
        return o == null ? null : String.valueOf(o).trim();
    }

    private static Integer asInt(Object o, Integer def) {
        if (o == null) return def;
        try {
            if (o instanceof Number n) return n.intValue();
            return Integer.parseInt(String.valueOf(o));
        } catch (Exception e) {
            return def;
        }
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> castMap(Object o) {
        if (o instanceof Map<?, ?> m) {
            return (Map<String, Object>) m;
        }
        return Collections.emptyMap();
    }

    @SuppressWarnings("unchecked")
    private static List<?> asList(Object o) {
        if (o instanceof List<?> l) return l;
        return null;
    }

    private static String getOpt(List<?> opts, int idx) {
        if (opts == null || opts.size() <= idx || opts.get(idx) == null) return "";
        return String.valueOf(opts.get(idx));
    }
}