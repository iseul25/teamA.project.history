package com.lms.history.boards.repository;

import com.lms.history.boards.entity.Board;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class BoardRepository {
    private final JdbcTemplate jdbc;

    public BoardRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // 전체 게시글 조회
    public List<Board> findAll() {
        String sql = "SELECT * FROM Board";
        return jdbc.query(sql, boardRowMapper());
    }

    // 각 타입별 게시글 목록 조회
    public List<Board> findByBoardType(String boardType) {
        String sql = "SELECT * FROM Board b JOIN users u ON b.userId = u.userId WHERE b.boardType = ?";
        return jdbc.query(sql, (rs, rowNum) -> {
            Board board = new Board();
            board.setBoardId(rs.getInt("boardId"));
            board.setUserId(rs.getInt("userId"));
            board.setTitle(rs.getString("title"));
            board.setContent(rs.getString("content"));
            board.setBoardType(rs.getString("boardType"));
            board.setCreated(rs.getTimestamp("date"));
            board.setUpdated(rs.getTimestamp("date"));
            board.setName(rs.getString("name"));
            board.setImgUrl(rs.getString("imgUrl"));
            board.setImgDescription(rs.getString("imgDescription"));
            board.setVideoUrl(rs.getString("videoUrl"));   // ⬅️ 추가
            return board;
        }, boardType);
    }

    // 페이징 처리된 게시글 조회 (최신순)
    public List<Board> findByBoardTypeWithPaging(String boardType, int page, int size) {
        int offset = page * size;
        String sql = "SELECT b.*, u.name " +
                "FROM Board b " +
                "JOIN users u ON b.userId = u.userId " +
                "WHERE b.boardType = ? " +
                "ORDER BY b.boardId DESC " +  // date → boardId로 변경
                "LIMIT ? OFFSET ?";

        List<Board> boards = jdbc.query(sql, (rs, rowNum) -> {
            Board board = new Board();
            board.setBoardId(rs.getInt("boardId"));
            board.setUserId(rs.getInt("userId"));
            board.setTitle(rs.getString("title"));
            board.setContent(rs.getString("content"));
            board.setBoardType(rs.getString("boardType"));
            board.setCreated(rs.getTimestamp("date"));
            board.setUpdated(rs.getTimestamp("date"));
            board.setName(rs.getString("name"));
            board.setImgUrl(rs.getString("imgUrl"));
            board.setImgDescription(rs.getString("imgDescription"));
            board.setVideoUrl(rs.getString("videoUrl"));
            return board;
        }, boardType, size, offset);

        return boards;
    }

    // 특정 타입의 총 게시글 수 조회
    public long countByBoardType(String boardType) {
        String sql = "SELECT COUNT(*) FROM Board WHERE boardType = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, boardType);
        return count != null ? count.longValue() : 0L;
    }

    private RowMapper<Board> boardRowMapper() {
        return (rs, rowNum) -> {
            Board board = new Board();
            board.setBoardId(rs.getInt("boardId"));
            board.setTitle(rs.getString("title"));
            board.setCreated(rs.getTimestamp("date"));
            board.setBoardType(rs.getString("boardType"));
            board.setImgUrl(rs.getString("imgUrl"));
            board.setImgDescription(rs.getString("imgDescription"));
            board.setVideoUrl(rs.getString("videoUrl"));   // ⬅️ 추가
            return board;
        };
    }

    public Board save(Board board, int userId) {
        String sql = "INSERT INTO Board (boardType, title, content, userId, videoUrl, imgUrl, imgDescription) VALUES (?, ?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, board.getBoardType());
            ps.setString(2, board.getTitle());
            ps.setString(3, board.getContent());
            ps.setInt(4, userId);
            ps.setString(5, board.getVideoUrl());      // ⬅️ 추가
            ps.setString(6, board.getImgUrl());
            ps.setString(7, board.getImgDescription());
            return ps;
        }, keyHolder);

        if (keyHolder.getKey() != null) {
            board.setBoardId(keyHolder.getKey().intValue());
            return board;
        } else {
            return null;
        }
    }

    public void update(Board board) {
        String sql = "UPDATE Board SET boardType=?, title=?, content=?, videoUrl=?, imgUrl=?, imgDescription=? WHERE boardId=?";
        jdbc.update(sql,
                board.getBoardType(),
                board.getTitle(),
                board.getContent(),
                board.getVideoUrl(),
                board.getImgUrl(),
                board.getImgDescription(),
                board.getBoardId()
        );
    }

    public Board findById(int boardId) {
        String sql = "SELECT * FROM Board WHERE boardId = ?";
        try {
            return jdbc.queryForObject(sql, (rs, rowNum) -> {
                Board board = new Board();
                board.setBoardId(rs.getInt("boardId"));
                board.setUserId(rs.getInt("userId"));
                board.setTitle(rs.getString("title"));
                board.setContent(rs.getString("content"));
                board.setBoardType(rs.getString("boardType"));
                board.setCreated(rs.getTimestamp("date"));
                board.setUpdated(rs.getTimestamp("date"));
                board.setImgUrl(rs.getString("imgUrl"));
                board.setImgDescription(rs.getString("imgDescription"));
                board.setVideoUrl(rs.getString("videoUrl"));   // ⬅️ 추가
                return board;
            }, boardId);
        } catch (EmptyResultDataAccessException e) {
            System.out.println("Board ID " + boardId + "를 찾을 수 없습니다.");
            return null;
        }
    }

    public Board findByTitleAndType(String title, String boardType) {
        String sql = "SELECT * FROM board WHERE title = ? AND boardType = ? LIMIT 1";
        List<Board> results = jdbc.query(sql, (rs, rowNum) -> {
            Board board = new Board();
            board.setBoardId(rs.getInt("boardId"));
            board.setTitle(rs.getString("title"));
            board.setBoardType(rs.getString("boardType"));
            // ... 나머지 필드 설정 ...
            return board;
        }, title, boardType);

        return results.isEmpty() ? null : results.get(0);
    }

    public void deleteById(int boardId) {
        String sql = "DELETE FROM Board WHERE boardId = ?";
        jdbc.update(sql, boardId);
    }
}
