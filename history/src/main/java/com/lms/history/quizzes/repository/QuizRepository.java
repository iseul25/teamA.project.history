package com.lms.history.quizzes.repository;

import com.lms.history.quizzes.entity.Attempt;
import com.lms.history.quizzes.entity.Category;
import com.lms.history.quizzes.entity.Quiz;
import com.lms.history.quizzes.entity.Score;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class QuizRepository {

    private final JdbcTemplate jdbc;

    public QuizRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // 전체 게시글 조회
    public List<Category> findAll() {
        String sql = "SELECT * FROM QUIZ_CATEGORY";
        return jdbc.query(sql, categoryRowMapper());
    }

    // QuizType으로 Category 조회
    public List<Category> findByQuizType(String quizType) {
        String sql = "SELECT quizCategoryId, userId, quizType, quizListName, createAt " +
                "FROM QUIZ_CATEGORY WHERE quizType = ?";
        return jdbc.query(sql, new Object[]{quizType}, categoryRowMapper());
    }

    // QuizRepository.java의 findCategoriesWithScores 메서드 수정
    public List<Map<String, Object>> findCategoriesWithScores(String quizType, int userId) {
        String sql = "SELECT c.quizCategoryId, c.userId, c.quizType, c.quizListName, c.createAt, ";

        if (userId > 0) {
            // 로그인한 사용자: 실제 점수 조회
            sql += "COALESCE(s.totalScore, 0) as totalScore, " +
                    "CASE WHEN s.totalScore >= 60 THEN true ELSE false END as pass " +
                    "FROM QUIZ_CATEGORY c " +
                    "LEFT JOIN QUIZ_SCORE s ON c.quizCategoryId = s.quizCategoryId AND s.userId = ? ";
        } else {
            // 로그인하지 않은 사용자: 기본값 사용
            sql += "0 as totalScore, " +
                    "false as pass " +
                    "FROM QUIZ_CATEGORY c ";
        }

        Object[] params;
        if (quizType != null && !quizType.trim().isEmpty()) {
            sql += " WHERE c.quizType = ?";
            if (userId > 0) {
                params = new Object[]{userId, quizType};
            } else {
                params = new Object[]{quizType};
            }
        } else {
            if (userId > 0) {
                params = new Object[]{userId};
            } else {
                params = new Object[]{};
            }
        }

        sql += " ORDER BY c.createAt DESC";

        return jdbc.queryForList(sql, params);
    }

    public void deleteByQuizCategoryId(int quizCategoryId) {
        String sql = "DELETE FROM QUIZ_CATEGORY WHERE QUIZCATEGORYID = ?";
        jdbc.update(sql, quizCategoryId);
    }

    public List<Map<String, Object>> findScoresByQuizCategoryId(int quizCategoryId) {
        String sql = """
        SELECT 
            s.scoreid as scoreId,
            s.quizcategoryid as quizCategoryId,
            s.userid as userId,
            s.totalscore as score,
            s.pass as passed,
            u.name as username
        FROM quiz_score s
        INNER JOIN users u ON s.userid = u.userid
        WHERE s.quizcategoryid = ?
        ORDER BY s.totalscore DESC, u.name ASC
        """;

        return jdbc.queryForList(sql, quizCategoryId);
    }


    // 카테고리 ID로 카테고리 정보 조회
    public Category findQuizCategoryById(int quizCategoryId) {
        String sql = "SELECT * FROM QUIZ_CATEGORY WHERE quizCategoryId = ?";
        List<Category> categories = jdbc.query(sql, new Object[]{quizCategoryId}, categoryRowMapper());
        return categories.isEmpty() ? null : categories.get(0);
    }

    // 카테고리 ID로 퀴즈 목록 조회
    public List<Quiz> findQuizListByCategoryId(int quizCategoryId) {
        String sql = "SELECT * FROM QUIZ WHERE quizCategoryId = ? ORDER BY quizId";
        return jdbc.query(sql, new Object[]{quizCategoryId}, quizRowMapper());
    }

    // 퀴즈 ID로 단일 퀴즈 조회
    public Quiz findQuizById(int quizId) {
        String sql = "SELECT * FROM QUIZ WHERE quizId = ?";
        List<Quiz> quizzes = jdbc.query(sql, new Object[]{quizId}, quizRowMapper());
        return quizzes.isEmpty() ? null : quizzes.get(0);
    }

    // Category RowMapper 재사용 메서드
    private RowMapper<Category> categoryRowMapper() {
        return (rs, rowNum) -> {
            Category category = new Category();
            category.setQuizCategoryId(rs.getInt("quizCategoryId"));
            category.setUserId(rs.getInt("userId"));
            category.setQuizType(rs.getString("quizType"));
            category.setQuizListName(rs.getString("quizListName"));
            category.setCreateAt(rs.getTimestamp("createAt").toLocalDateTime());
            return category;
        };
    }

    // Quiz RowMapper
    private RowMapper<Quiz> quizRowMapper() {
        return (rs, rowNum) -> {
            Quiz quiz = new Quiz();
            quiz.setQuizId(rs.getInt("quizId"));
            quiz.setQuizCategoryId(rs.getInt("quizCategoryId"));
            quiz.setQuestion(rs.getString("question"));
            quiz.setItem1(rs.getString("item1"));
            quiz.setItem2(rs.getString("item2"));
            quiz.setItem3(rs.getString("item3"));
            quiz.setItem4(rs.getString("item4"));
            quiz.setAnswer(rs.getInt("answer"));
            quiz.setCommentary(rs.getString("commentary"));
            quiz.setQuizScore(rs.getInt("quizScore"));
            return quiz;
        };
    }
}