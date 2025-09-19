package com.lms.history.users.repository;

import com.lms.history.users.entity.Points;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class PointsRepository {
    private final JdbcTemplate jdbc;

    public PointsRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // 포인트 기록 저장
    public Points save(Points points) {
        String sql = "INSERT INTO points (userId, attendanceId, itemId, pointChange, totalPoint) VALUES (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, points.getUserId());
            ps.setObject(2, points.getAttendanceId()); // null 가능
            ps.setObject(3, points.getItemId()); // null 가능
//            ps.setObject(4, points.getQuizId()); // null 가능
            ps.setInt(4, points.getPointChange());
            ps.setInt(5, points.getTotalPoint());
            return ps;
        }, keyHolder);

        Number generatedId = keyHolder.getKey();
        if (generatedId != null) {
            points.setPointId(generatedId.intValue());
        }

        return points;
    }

    // 특정 사용자의 현재 총 포인트 조회 (최신 기록 기준)
    public Integer getTotalPointsByUserId(int userId) {
        String sql = "SELECT totalPoint FROM points WHERE userId = ? ORDER BY pointId DESC LIMIT 1";
        try {
            return jdbc.queryForObject(sql, Integer.class, userId);
        } catch (EmptyResultDataAccessException e) {
            return 0;
        }
    }

    // 특정 사용자의 모든 포인트 기록 조회
    public List<Points> findByUserId(int userId) {
        String sql = "SELECT * FROM points WHERE userId = ? ORDER BY pointId DESC";
        return jdbc.query(sql, pointsRowMapper(), userId);
    }

    // 특정 출석에 대한 포인트 기록 조회
    public Optional<Points> findByAttendanceId(int attendanceId) {
        String sql = "SELECT * FROM points WHERE attendanceId = ?";
        try {
            Points points = jdbc.queryForObject(sql, pointsRowMapper(), attendanceId);
            return Optional.ofNullable(points);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    // RowMapper
    private RowMapper<Points> pointsRowMapper() {
        return (rs, rowNum) -> {
            Points points = new Points();
            points.setPointId(rs.getInt("pointId"));
            points.setUserId(rs.getInt("userId"));

            // nullable 컬럼들 처리
            Integer attendanceId = (Integer) rs.getObject("attendanceId");
            points.setAttendanceId(attendanceId);

            Integer itemId = (Integer) rs.getObject("itemId");
            points.setItemId(itemId);

//            Integer quizId = (Integer) rs.getObject("quizId");
//            points.setQuizId(quizId);

            points.setPointChange(rs.getInt("pointChange"));
            points.setTotalPoint(rs.getInt("totalPoint"));

            return points;
        };
    }
}