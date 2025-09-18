package com.lms.history.boards.repository;

import com.lms.history.boards.entity.CommentReply;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class CommentReplyRepository {
    private final JdbcTemplate jdbc;

    public CommentReplyRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // 특정 댓글의 답글 목록 조회
    public List<CommentReply> findByCommentId(int commentId) {
        String sql = "SELECT cr.*, u.name FROM comment_reply cr " +
                "JOIN users u ON cr.userId = u.userId " +
                "WHERE cr.commentId = ? ORDER BY cr.date ASC";
        return jdbc.query(sql, (rs, rowNum) -> {
            CommentReply reply = new CommentReply();
            reply.setReplyId(rs.getInt("replyId"));
            reply.setCommentId(rs.getInt("commentId"));
            reply.setUserId(rs.getInt("userId"));
            reply.setReply(rs.getString("reply"));
            reply.setDate(rs.getTimestamp("date").toLocalDateTime());
            reply.setName(rs.getString("name"));
            return reply;
        }, commentId);
    }

    // 답글 저장
    public CommentReply save(CommentReply reply) {
        String sql = "INSERT INTO comment_reply (commentId, userId, reply) VALUES (?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, reply.getCommentId());
            ps.setInt(2, reply.getUserId());
            ps.setString(3, reply.getReply());
            return ps;
        }, keyHolder);

        if (keyHolder.getKey() != null) {
            reply.setReplyId(keyHolder.getKey().intValue());
        }
        return reply;
    }


    // 답글 삭제
    public void deleteById(int replyId) {
        String sql = "DELETE FROM comment_reply WHERE replyId = ?";
        jdbc.update(sql, replyId);
    }

    // 댓글 삭제 시 관련 답글 모두 삭제
    public void deleteByCommentId(int commentId) {
        String sql = "DELETE FROM comment_reply WHERE commentId = ?";
        jdbc.update(sql, commentId);
    }
}
