package com.lms.history.admin.service;

import com.lms.history.quizzes.entity.Quiz;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.file.*;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AdminQuizService {

    private final JdbcTemplate jdbc;

    public AdminQuizService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // 카테고리 보장: 없으면 생성 후 ID 반환
    public int ensureCategory(String quizType, String quizListName) {
        Integer id = jdbc.query(
                "SELECT quizCategoryId FROM quiz_category WHERE quizType=? AND quizListName=? LIMIT 1",
                ps -> { ps.setString(1, quizType); ps.setString(2, quizListName); },
                rs -> rs.next() ? rs.getInt(1) : null
        );
        if (id != null) return id;

        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO quiz_category (quizType, quizListName, created_at) VALUES (?,?,?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, quizType);
            ps.setString(2, quizListName);
            ps.setObject(3, LocalDateTime.now());
            return ps;
        }, kh);
        Number key = kh.getKey();
        if (key == null) throw new IllegalStateException("quiz_category PK 생성 실패");
        return key.intValue();
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // 이미지 저장 (MultipartFile)
    public String storeImage(MultipartFile file) {
        try {
            Path root = Paths.get("src/main/resources/static/uploads/quizzes");
            Files.createDirectories(root);
            String original = Optional.ofNullable(file.getOriginalFilename()).orElse("image");
            String safe = original.replaceAll("[^a-zA-Z0-9._-]", "_");
            String filename = System.currentTimeMillis() + "_" + safe;
            Path target = root.resolve(filename);
            try (InputStream in = file.getInputStream()) {
                Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
            }
            return "/uploads/quizzes/" + filename; // 정적 리소스 매핑 기준 URL
        } catch (Exception e) {
            throw new RuntimeException("이미지 저장 실패: " + e.getMessage(), e);
        }
    }

    // 이미지 저장 (Servlet Part) — 벌크 생성용
    public String storeImage(Part part) {
        try {
            Path root = Paths.get("src/main/resources/static/uploads/quizzes");
            Files.createDirectories(root);
            String submitted = Optional.ofNullable(part.getSubmittedFileName()).orElse("image");
            String safe = submitted.replaceAll("[^a-zA-Z0-9._-]", "_");
            String filename = System.currentTimeMillis() + "_" + safe;
            Path target = root.resolve(filename);
            try (InputStream in = part.getInputStream()) {
                Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
            }
            return "/uploads/quizzes/" + filename;
        } catch (Exception e) {
            throw new RuntimeException("이미지 저장 실패: " + e.getMessage(), e);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // 목록/단건 조회 (모달 목록/로딩)
    public List<Quiz> findQuestionsByCategory(int quizCategoryId) {
        String sql = "SELECT quizId, quizCategoryId, quizNumber, imgUrl, question, item1, item2, item3, item4, answer, quizScore " +
                "FROM quiz WHERE quizCategoryId=? ORDER BY quizNumber, quizId";
        return jdbc.query(sql, (rs, i) -> {
            Quiz q = new Quiz();
            q.setQuizId(rs.getInt("quizId"));
            q.setQuizCategoryId(rs.getInt("quizCategoryId"));
            q.setQuizNumber((Integer) rs.getObject("quizNumber"));
            q.setImgUrl(rs.getString("imgUrl"));
            q.setQuestion(rs.getString("question"));
            q.setItem1(rs.getString("item1"));
            q.setItem2(rs.getString("item2"));
            q.setItem3(rs.getString("item3"));
            q.setItem4(rs.getString("item4"));
            q.setAnswer((Integer) rs.getObject("answer"));
            q.setQuizScore((Integer) rs.getObject("quizScore"));
            return q;
        }, quizCategoryId);
    }

    public Quiz findOne(int quizId) {
        String sql = "SELECT quizId, quizCategoryId, quizNumber, imgUrl, question, item1, item2, item3, item4, answer, quizScore " +
                "FROM quiz WHERE quizId=?";
        var list = jdbc.query(sql, (rs, i) -> {
            Quiz q = new Quiz();
            q.setQuizId(rs.getInt("quizId"));
            q.setQuizCategoryId(rs.getInt("quizCategoryId"));
            q.setQuizNumber((Integer) rs.getObject("quizNumber"));
            q.setImgUrl(rs.getString("imgUrl"));
            q.setQuestion(rs.getString("question"));
            q.setItem1(rs.getString("item1"));
            q.setItem2(rs.getString("item2"));
            q.setItem3(rs.getString("item3"));
            q.setItem4(rs.getString("item4"));
            q.setAnswer((Integer) rs.getObject("answer"));
            q.setQuizScore((Integer) rs.getObject("quizScore"));
            return q;
        }, quizId);
        return list.isEmpty() ? null : list.get(0);
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // 단건 수정/이미지 수정/추가/삭제
    public boolean updateQuiz(Quiz patch) {
        var sets = new ArrayList<String>();
        var params = new ArrayList<Object>();

        if (patch.getQuizNumber() != null) { sets.add("quizNumber=?"); params.add(patch.getQuizNumber()); }
        if (patch.getQuestion() != null)   { sets.add("question=?");   params.add(patch.getQuestion()); }
        if (patch.getItem1() != null)      { sets.add("item1=?");      params.add(patch.getItem1()); }
        if (patch.getItem2() != null)      { sets.add("item2=?");      params.add(patch.getItem2()); }
        if (patch.getItem3() != null)      { sets.add("item3=?");      params.add(patch.getItem3()); }
        if (patch.getItem4() != null)      { sets.add("item4=?");      params.add(patch.getItem4()); }
        if (patch.getAnswer() != null)     { sets.add("answer=?");     params.add(patch.getAnswer()); }
        if (patch.getQuizScore() != null)  { sets.add("quizScore=?");  params.add(patch.getQuizScore()); }

        if (sets.isEmpty()) return true; // 변경 없음
        params.add(patch.getQuizId());

        String sql = "UPDATE quiz SET " + String.join(", ", sets) + " WHERE quizId=?";
        return jdbc.update(sql, params.toArray()) > 0;
    }

    public boolean updateQuizImage(int quizId, String imgUrl) {
        String sql = "UPDATE quiz SET imgUrl=? WHERE quizId=?";
        return jdbc.update(sql, imgUrl, quizId) > 0;
    }

    public int insertOne(Quiz q) {
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            var ps = con.prepareStatement(
                    "INSERT INTO quiz (quizCategoryId, quizNumber, imgUrl, question, item1, item2, item3, item4, answer, quizScore) " +
                            "VALUES (?,?,?,?,?,?,?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setInt(1, q.getQuizCategoryId());
            if (q.getQuizNumber() == null) ps.setNull(2, java.sql.Types.INTEGER); else ps.setInt(2, q.getQuizNumber());
            ps.setString(3, q.getImgUrl());
            ps.setString(4, q.getQuestion());
            ps.setString(5, q.getItem1());
            ps.setString(6, q.getItem2());
            ps.setString(7, q.getItem3());
            ps.setString(8, q.getItem4());
            if (q.getAnswer() == null) ps.setNull(9, java.sql.Types.INTEGER); else ps.setInt(9, q.getAnswer());
            if (q.getQuizScore() == null) ps.setNull(10, java.sql.Types.INTEGER); else ps.setInt(10, q.getQuizScore());
            return ps;
        }, kh);
        var key = kh.getKey();
        return key == null ? 0 : key.intValue();
    }

    public int deleteQuiz(int quizId) {
        return jdbc.update("DELETE FROM quiz WHERE quizId=?", quizId);
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // 벌크 생성(모달 네이밍 그대로)
    public List<Quiz> buildBulkFromRequest(HttpServletRequest request, int quizCategoryId) {
        List<Quiz> list = new ArrayList<>();
        try {
            for (int i = 1; i <= 10; i++) {
                String text = request.getParameter("quizText" + i);
                String o1   = request.getParameter("option1_" + i);
                String o2   = request.getParameter("option2_" + i);
                String o3   = request.getParameter("option3_" + i);
                String o4   = request.getParameter("option4_" + i);
                String ansS = request.getParameter("answer" + i);

                if (!StringUtils.hasText(text) || !StringUtils.hasText(o1) || !StringUtils.hasText(o2)
                        || !StringUtils.hasText(o3) || !StringUtils.hasText(o4) || !StringUtils.hasText(ansS)) {
                    continue; // 비어있으면 스킵
                }

                Integer ans;
                try { ans = Integer.valueOf(ansS); } catch (NumberFormatException e) { continue; }

                Quiz q = new Quiz();
                q.setQuizCategoryId(quizCategoryId);
                q.setQuizNumber(i);
                q.setQuestion(text.trim());
                q.setItem1(o1.trim());
                q.setItem2(o2.trim());
                q.setItem3(o3.trim());
                q.setItem4(o4.trim());
                q.setAnswer(ans);

                Part p = null;
                try { p = request.getPart("quizImage" + i); } catch (Exception ignore) {}
                if (p != null && p.getSize() > 0) {
                    q.setImgUrl(storeImage(p));
                }

                list.add(q);
            }
        } catch (Exception e) {
            throw new RuntimeException("벌크 파싱 실패: " + e.getMessage(), e);
        }
        return list;
    }

    public void insertQuizzes(List<Quiz> list) {
        if (list == null || list.isEmpty()) return;
        String sql = "INSERT INTO quiz (quizCategoryId, quizNumber, imgUrl, question, item1, item2, item3, item4, answer, quizScore) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?)";
        jdbc.batchUpdate(sql, list, list.size(), (ps, q) -> {
            ps.setInt(1, q.getQuizCategoryId());
            if (q.getQuizNumber() == null) ps.setNull(2, java.sql.Types.INTEGER); else ps.setInt(2, q.getQuizNumber());
            ps.setString(3, q.getImgUrl());
            ps.setString(4, q.getQuestion());
            ps.setString(5, q.getItem1());
            ps.setString(6, q.getItem2());
            ps.setString(7, q.getItem3());
            ps.setString(8, q.getItem4());
            if (q.getAnswer() == null) ps.setNull(9, java.sql.Types.INTEGER); else ps.setInt(9, q.getAnswer());
            if (q.getQuizScore() == null) ps.setNull(10, java.sql.Types.INTEGER); else ps.setInt(10, q.getQuizScore());
        });
    }
}
