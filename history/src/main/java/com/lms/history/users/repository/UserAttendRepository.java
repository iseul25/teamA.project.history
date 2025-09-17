package com.lms.history.users.repository;

import com.lms.history.users.entity.Attend;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class UserAttendRepository {
    private final JdbcTemplate jdbc;

    public UserAttendRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // ---------------- 특정 사용자가 특정 날짜에 출석했는지 확인 ----------------
    public boolean existsByUserIdAndDate(int userId, LocalDate date) {
        String sql = "SELECT COUNT(*) FROM user_attendance WHERE userId = ? AND attendanceDate >= ? AND attendanceDate < ?";
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();
        Integer count = jdbc.queryForObject(sql, Integer.class, userId, startOfDay, endOfDay);
        return count != null && count > 0;
    }

    // ---------------- 출석 기록을 저장 ----------------
    public void save(Attend attend) {
        String sql = "INSERT INTO user_attendance (userId, attendanceDate, pointAdd) VALUES (?, ?, ?)";
        jdbc.update(sql, attend.getUserId(), attend.getAttendanceDate(), attend.getPointAdd());
    }

    // ---------------- 출석 기록을 저장하고 생성된 ID 반환 ----------------
    public Attend saveAndReturn(Attend attend) {
        String sql = "INSERT INTO user_attendance (userId, attendanceDate, pointAdd) VALUES (?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, attend.getUserId());
            ps.setObject(2, attend.getAttendanceDate());
            ps.setInt(3, attend.getPointAdd());
            return ps;
        }, keyHolder);

        // 생성된 키 가져오기
        Number generatedId = keyHolder.getKey();
        if (generatedId != null) {
            attend.setAttendanceId(generatedId.intValue());
        }

        return attend;
    }

    // ---------------- 특정 출석 기록 ID로 조회 ----------------
    public Optional<Attend> findById(int attendanceId) {
        String sql = "SELECT * FROM user_attendance WHERE attendanceId = ?";
        try {
            Attend attend = jdbc.queryForObject(sql, attendRowMapper(), attendanceId);
            return Optional.ofNullable(attend);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    // ---------------- 특정 사용자의 특정 날짜 출석 기록 조회 ----------------
    public Optional<Attend> findByUserIdAndDate(int userId, LocalDate date) {
        String sql = "SELECT * FROM user_attendance WHERE userId = ? AND attendanceDate >= ? AND attendanceDate < ?";
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();

        try {
            Attend attend = jdbc.queryForObject(sql, attendRowMapper(), userId, startOfDay, endOfDay);
            return Optional.ofNullable(attend);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    // ---------------- 특정 사용자의 총 출석 횟수를 조회 ----------------
    public int getAttendanceCountByUserId(int userId) {
        String sql = "SELECT COUNT(*) FROM user_attendance WHERE userId = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, userId);
        return count != null ? count : 0;
    }

    // ---------------- 특정 사용자의 모든 출석 기록 조회 ----------------
    public List<Attend> findByUserId(int userId) {
        String sql = "SELECT * FROM user_attendance WHERE userId = ? ORDER BY attendanceDate DESC";
        return jdbc.query(sql, attendRowMapper(), userId);
    }

    // ---------------- 특정 사용자의 월별 출석 일수 카운트 ----------------
    public int countByUserIdAndYearMonth(int userId, int year, int month) {
        String sql = "SELECT COUNT(*) FROM user_attendance WHERE userId = ? AND YEAR(attendanceDate) = ? AND MONTH(attendanceDate) = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, userId, year, month);
        return count != null ? count : 0;
    }

    // ---------------- 특정 사용자의 기간별 출석 기록 조회 ----------------
    public List<Attend> findByUserIdAndDateRange(int userId, LocalDateTime startDate, LocalDateTime endDate) {
        String sql = "SELECT * FROM user_attendance WHERE userId = ? AND attendanceDate >= ? AND attendanceDate <= ? ORDER BY attendanceDate DESC";
        return jdbc.query(sql, attendRowMapper(), userId, startDate, endDate);
    }

    // ---------------- 특정 사용자의 최근 출석 기록 조회 (limit 적용) ----------------
    public List<Attend> findRecentByUserId(int userId, int limit) {
        String sql = "SELECT * FROM user_attendance WHERE userId = ? ORDER BY attendanceDate DESC LIMIT ?";
        return jdbc.query(sql, attendRowMapper(), userId, limit);
    }

    // ---------------- 특정 사용자의 연속 출석을 위한 날짜별 출석 기록 조회 ----------------
    public List<LocalDate> findAttendanceDatesByUserId(int userId) {
        String sql = "SELECT DATE(attendanceDate) as attend_date FROM user_attendance WHERE userId = ? ORDER BY attendanceDate DESC";
        return jdbc.query(sql, (rs, rowNum) -> rs.getDate("attend_date").toLocalDate(), userId);
    }

    // ---------------- 특정 기간 내 전체 사용자 출석 통계 ----------------
    public int countTotalAttendanceByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        String sql = "SELECT COUNT(*) FROM user_attendance WHERE attendanceDate >= ? AND attendanceDate <= ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, startDate, endDate);
        return count != null ? count : 0;
    }

    // ---------------- 출석 기록 삭제 ----------------
    public boolean deleteById(int attendanceId) {
        String sql = "DELETE FROM user_attendance WHERE attendanceId = ?";
        int rowsAffected = jdbc.update(sql, attendanceId);
        return rowsAffected > 0;
    }

    // ---------------- 특정 사용자의 모든 출석 기록 삭제 ----------------
    public int deleteByUserId(int userId) {
        String sql = "DELETE FROM user_attendance WHERE userId = ?";
        return jdbc.update(sql, userId);
    }

    // ---------------- 출석 기록 수정 ----------------
    public boolean updateAttendance(Attend attend) {
        String sql = "UPDATE user_attendance SET pointAdd = ? WHERE attendanceId = ?";
        int rowsAffected = jdbc.update(sql, attend.getPointAdd(), attend.getAttendanceId());
        return rowsAffected > 0;
    }

    // ---------------- RowMapper: DB 결과를 Attend 객체로 매핑 ----------------
    private RowMapper<Attend> attendRowMapper() {
        return (rs, rowNum) -> {
            Attend attend = new Attend();
            attend.setAttendanceId(rs.getInt("attendanceId"));
            attend.setUserId(rs.getInt("userId"));
            attend.setAttendanceDate(rs.getTimestamp("attendanceDate").toLocalDateTime());
            attend.setPointAdd(rs.getInt("pointAdd"));
            return attend;
        };
    }
}