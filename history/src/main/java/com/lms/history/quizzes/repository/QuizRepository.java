package com.lms.history.quizzes.repository;

import com.lms.history.quizzes.entity.Attempt;
import com.lms.history.quizzes.entity.Category;
import com.lms.history.quizzes.entity.Quiz;
import com.lms.history.quizzes.entity.Score;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
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
        String sql = "SELECT * FROM QUIZ_CATEGORY ORDER BY quizCategoryId ASC";
        return jdbc.query(sql, categoryRowMapper());
    }

    // QuizType으로 Category 조회
    public List<Category> findByQuizType(String quizType) {
        String sql = "SELECT quizCategoryId, userId, quizType, quizListName, createAt " +
                "FROM QUIZ_CATEGORY WHERE quizType = ? ORDER BY quizCategoryId ASC";
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

        sql += " ORDER BY c.quizCategoryId ASC";

        return jdbc.queryForList(sql, params);
    }

    public void deleteByQuizCategoryId(int quizCategoryId) {
        String sql = "DELETE FROM QUIZ_CATEGORY WHERE QUIZCATEGORYID = ?";
        jdbc.update(sql, quizCategoryId);
    }

    // 💡 롤백 메서드 1: 특정 카테고리의 모든 퀴즈 문제를 삭제
    public int deleteAllQuizzesByCategoryId(int quizCategoryId) {
        String sql = "DELETE FROM quiz WHERE quizCategoryId = ?";
        return jdbc.update(sql, quizCategoryId);
    }

    // 💡 롤백 메서드 2: 특정 카테고리를 삭제 (반환형 int)
    public int deleteCategoryById(int quizCategoryId) {
        String sql = "DELETE FROM QUIZ_CATEGORY WHERE quizCategoryId = ?";
        return jdbc.update(sql, quizCategoryId);
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

            // quizNumber (nullable)
            quiz.setQuizNumber(rs.getObject("quizNumber", Integer.class));

            quiz.setImgUrl(rs.getString("imgUrl"));
            quiz.setQuestion(rs.getString("question"));
            quiz.setItem1(rs.getString("item1"));
            quiz.setItem2(rs.getString("item2"));
            quiz.setItem3(rs.getString("item3"));
            quiz.setItem4(rs.getString("item4"));

            // answer (int 컬럼이면 wasNull 처리 권장)
            int answer = rs.getInt("answer");
            quiz.setAnswer(rs.wasNull() ? null : answer);

            // ★ commentary는 DB에 없음 → 읽지 말자
            quiz.setCommentary(null);

            // quizScore (nullable)
            quiz.setQuizScore(rs.getObject("quizScore", Integer.class));

            return quiz;
        };
    }

    public void deleteAttemptsByUserAndCategory(int userId, int quizCategoryId) {
        String sql = "DELETE FROM quiz_attempt WHERE userId = ? AND quizCategoryId = ?";
        jdbc.update(sql, userId, quizCategoryId);
    }

    public void saveAttempt(Attempt attempt) {
        String sql = "INSERT INTO quiz_attempt (userId, quizCategoryId, quizId, selected, attemptAt) " +
                "VALUES (?, ?, ?, ?, ?)";
        jdbc.update(sql,
                attempt.getUserId(),
                attempt.getQuizCategoryId(),
                attempt.getQuizId(),
                attempt.getSelected(),
                attempt.getAttemptAt()
        );
    }

    public int saveCategory(Category category) {
        String sql = "INSERT INTO quiz_category (userId, quizType, quizListName, createAt) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, category.getUserId());
            ps.setString(2, category.getQuizType());
            ps.setString(3, category.getQuizListName());
            ps.setTimestamp(4, Timestamp.valueOf(category.getCreateAt()));
            return ps;
        }, keyHolder);

        return keyHolder.getKey().intValue();
    }

    public void saveQuiz(Quiz quiz) {
        String sql = "INSERT INTO quiz (quizCategoryId, quizNumber, imgUrl, question, item1, item2, item3, item4, answer, quizScore) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbc.update(sql,
                quiz.getQuizCategoryId(),
                quiz.getQuizNumber(),
                quiz.getImgUrl(),
                quiz.getQuestion(),
                quiz.getItem1(),
                quiz.getItem2(),
                quiz.getItem3(),
                quiz.getItem4(),
                quiz.getAnswer(),
                quiz.getQuizScore()
        );
    }

    // 1) 제목+타입으로 카테고리 조회
    public Integer findCategoryIdByTitleAndType(String title, String quizType) {
        String sql = "SELECT quizCategoryId FROM quiz_category WHERE quizListName = ? AND quizType = ?";
        try {
            return jdbc.queryForObject(sql, Integer.class, title, quizType);
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return null;
        }
    }

    // 2) 카테고리 업서트
    public int upsertQuizCategory(String title, String quizType, int userId) {
        Integer found = findCategoryIdByTitleAndType(title, quizType);
        if (found != null) return found;

        Category c = new Category();
        c.setUserId(userId);
        c.setQuizType(quizType);
        c.setQuizListName(title);
        c.setCreateAt(java.time.LocalDateTime.now());
        return saveCategory(c);
    }

    // 3) 퀴즈 저장 후 PK 반환
    public int insertQuizReturningId(Quiz quiz) {
        String sql = """
        INSERT INTO quiz
        (quizCategoryId, quizNumber, imgUrl, question, item1, item2, item3, item4, answer, quizScore)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    """;
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            int i = 1;
            ps.setInt(i++, quiz.getQuizCategoryId());
            if (quiz.getQuizNumber() == null) ps.setNull(i++, java.sql.Types.INTEGER); else ps.setInt(i++, quiz.getQuizNumber());
            if (quiz.getImgUrl() == null)     ps.setNull(i++, java.sql.Types.VARCHAR); else ps.setString(i++, quiz.getImgUrl());
            ps.setString(i++, quiz.getQuestion());
            ps.setString(i++, quiz.getItem1());
            ps.setString(i++, quiz.getItem2());
            ps.setString(i++, quiz.getItem3());
            ps.setString(i++, quiz.getItem4());
            if (quiz.getAnswer() == null)     ps.setNull(i++, java.sql.Types.INTEGER); else ps.setInt(i++, quiz.getAnswer());
            if (quiz.getQuizScore() == null)  ps.setNull(i++, java.sql.Types.INTEGER); else ps.setInt(i++, quiz.getQuizScore());
            return ps;
        }, kh);
        Number key = kh.getKey();
        if (key == null) throw new IllegalStateException("quiz PK 생성 실패");
        return key.intValue();
    }

    // 4) 이미지 URL 업데이트
    public void updateQuizImageUrl(int quizId, String imgUrl) {
        String sql = "UPDATE quiz SET imgUrl = ? WHERE quizId = ?";
        jdbc.update(sql, imgUrl, quizId);
    }

    // 수료 여부 확인
    public String checkPassStatus(int userId, int quizCategoryId) {
        String sql = "SELECT pass FROM quiz_score WHERE userId = ? AND quizCategoryId = ?";
        try {
            return jdbc.queryForObject(sql, String.class, userId, quizCategoryId);
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return null;
        }
    }

    // 문제 수정
    public void updateQuiz(Quiz quiz) {
        String sql = """
        UPDATE quiz 
        SET question = ?, item1 = ?, item2 = ?, item3 = ?, item4 = ?, 
            answer = ?, imgUrl = ?, quizNumber = ?, quizScore = ?
        WHERE quizId = ?
    """;
        jdbc.update(sql,
                quiz.getQuestion(),
                quiz.getItem1(),
                quiz.getItem2(),
                quiz.getItem3(),
                quiz.getItem4(),
                quiz.getAnswer(),
                quiz.getImgUrl(),
                quiz.getQuizNumber(),
                quiz.getQuizScore(),
                quiz.getQuizId()
        );
    }
}