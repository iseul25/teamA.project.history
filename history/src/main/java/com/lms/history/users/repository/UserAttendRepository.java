package com.lms.history.users.repository;

import com.lms.history.users.entity.Attend;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class UserAttendRepository {
    private final JdbcTemplate jdbc;

    public UserAttendRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // ---------------- 특정 사용자가 특정 날짜에 출석했는지 확인 ----------------
    // 🚩 수정: 컬럼명과 날짜 비교 로직 변경 (LocalDateTime에 맞게)
    public boolean existsByUserIdAndDate(int userId, LocalDate date) {
        String sql = "SELECT COUNT(*) FROM user_attendance WHERE userId = ? AND attendanceDate >= ? AND attendanceDate < ?";
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();
        Integer count = jdbc.queryForObject(sql, Integer.class, userId, startOfDay, endOfDay);
        return count != null && count > 0;
    }

    // ---------------- 출석 기록을 저장 ----------------
    // 🚩 수정: 컬럼명과 저장 값 변경
    public void save(Attend attend) {
        String sql = "INSERT INTO user_attendance (userId, attendanceDate, pointAdd) VALUES (?, ?, ?)";
        jdbc.update(sql, attend.getUserId(), attend.getAttendanceDate(), attend.getPointAdd());
    }

    // ---------------- 특정 사용자의 총 출석 횟수를 조회 ----------------
    // 🚩 수정: 테이블명 변경
    public int getAttendanceCountByUserId(int userId) {
        String sql = "SELECT COUNT(*) FROM user_attendance WHERE userId = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, userId);
        return count != null ? count : 0;
    }

    // ---------------- 특정 사용자의 모든 출석 기록 조회 ----------------
    // 🚩 수정: 테이블명 변경
    public List<Attend> findByUserId(int userId) {
        String sql = "SELECT * FROM user_attendance WHERE userId = ?";
        return jdbc.query(sql, attendRowMapper(), userId);
    }

    // ---------------- RowMapper: DB 결과를 Attend 객체로 매핑 ----------------
    // 🚩 수정: 컬럼명과 데이터 타입 변경
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