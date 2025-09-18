package com.lms.history.boards.repository;

import com.lms.history.boards.entity.Comment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class CommentRepository {

    private final JdbcTemplate jdbc;

    public CommentRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // 댓글 저장
    public Comment save(Comment comment) {
        String sql = "INSERT INTO board_comment (boardId, userId, comment, date) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, comment.getBoardId());
            ps.setInt(2, comment.getUserId());
            ps.setString(3, comment.getComment());
            ps.setTimestamp(4, Timestamp.valueOf(comment.getDate()));
            return ps;
        }, keyHolder);

        if (keyHolder.getKey() != null) {
            comment.setCommentId(keyHolder.getKey().intValue());
        }

        return comment;
    }

    // 특정 게시글의 댓글 목록 조회 (최신순)
    public List<Comment> findByBoardIdOrderByDateDesc(int boardId) {
        String sql = "SELECT c.*, u.name FROM board_comment c JOIN users u ON c.userId = u.userId WHERE c.boardId = ? ORDER BY c.date DESC";
        return jdbc.query(sql, (rs, rowNum) -> {
            Comment comment = new Comment();
            comment.setCommentId(rs.getInt("commentId"));
            comment.setBoardId(rs.getInt("boardId"));
            comment.setUserId(rs.getInt("userId"));
            comment.setComment(rs.getString("comment"));
            comment.setDate(rs.getTimestamp("date").toLocalDateTime());
            comment.setName(rs.getString("name")); // 작성자 이름 추가
            return comment;
        }, boardId);
    }

    // 댓글 단일 조회
    public Comment findById(int commentId) {
        String sql = "SELECT * FROM board_comment WHERE commentId = ?";
        return jdbc.queryForObject(sql, (rs, rowNum) -> {
            Comment comment = new Comment();
            comment.setCommentId(rs.getInt("commentId"));
            comment.setBoardId(rs.getInt("boardId"));
            comment.setUserId(rs.getInt("userId"));
            comment.setComment(rs.getString("comment"));
            comment.setDate(rs.getTimestamp("date").toLocalDateTime());
            return comment;
        }, commentId);
    }

    // 댓글 삭제
    public void delete(Comment comment) {
        String sql = "DELETE FROM board_comment WHERE commentId = ?";
        jdbc.update(sql, comment.getCommentId());
    }

    // 특정 게시글의 모든 댓글 삭제
    public void deleteByBoardId(int boardId) {
        String sql = "DELETE FROM board_comment WHERE boardId = ?";
        jdbc.update(sql, boardId);
    }
}
