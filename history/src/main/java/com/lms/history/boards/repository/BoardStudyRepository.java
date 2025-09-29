package com.lms.history.boards.repository;

import com.lms.history.boards.entity.BoardStudy;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class BoardStudyRepository {
    private final JdbcTemplate jdbc;

    public BoardStudyRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // 학습 시작 기록 (startAt과 endAt을 같은 시간으로 초기 기록)
    public void insertStart(int boardId, int userId, Date startAt) {
        String sql = "INSERT INTO board_study (boardId, userId, startAt, endAt) VALUES (?, ?, ?, ?)";
        jdbc.update(sql, boardId, userId, startAt, startAt);
    }

    // 학습 완료 기록 (endAt만 업데이트)
    public void updateEnd(int studyId, Date endAt) {
        String sql = "UPDATE board_study SET endAt = ? WHERE studyId = ?";
        jdbc.update(sql, endAt, studyId);
    }

    // 특정 사용자와 게시글의 모든 학습 기록 조회
    public List<BoardStudy> findByUserAndBoard(int userId, int boardId) {
        String sql = "SELECT * FROM board_study WHERE userId = ? AND boardId = ? ORDER BY startAt DESC";
        return jdbc.query(sql, (rs, rowNum) -> {
            BoardStudy bs = new BoardStudy();
            bs.setStudyId(rs.getInt("studyId"));
            bs.setBoardId(rs.getInt("boardId"));
            bs.setUserId(rs.getInt("userId"));
            bs.setStartAt(rs.getTimestamp("startAt"));
            bs.setEndAt(rs.getTimestamp("endAt"));
            return bs;
        }, userId, boardId);
    }

    // 특정 사용자와 게시글의 가장 최근 학습 기록 조회 (완료 여부 상관없이)
    public List<BoardStudy> findRecentByUserAndBoard(int userId, int boardId) {
        String sql = "SELECT * FROM board_study WHERE userId = ? AND boardId = ? ORDER BY startAt DESC LIMIT 1";
        return jdbc.query(sql, (rs, rowNum) -> {
            BoardStudy bs = new BoardStudy();
            bs.setStudyId(rs.getInt("studyId"));
            bs.setBoardId(rs.getInt("boardId"));
            bs.setUserId(rs.getInt("userId"));
            bs.setStartAt(rs.getTimestamp("startAt"));
            bs.setEndAt(rs.getTimestamp("endAt"));
            return bs;
        }, userId, boardId);
    }
}

