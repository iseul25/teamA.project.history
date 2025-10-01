package com.lms.history.admin.repository;

import com.lms.history.users.entity.User;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class AdminRepository {

    private final JdbcTemplate jdbc;

    public AdminRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /**
     * 회원 정보를 저장하거나 업데이트합니다.
     * 새로운 회원은 users 테이블과 points 테이블에 데이터를 추가합니다.
     */
    public User save(User user) {
        // 기존 회원은 이메일로 찾아 업데이트합니다.
        if (findByEmail(user.getEmail()).isPresent()) {
            // 이 로직은 포인트와 출석 데이터를 직접 수정하지 않으므로 변경 불필요
            String sql = "UPDATE users SET name = ?, password = ? WHERE email = ?";
            jdbc.update(sql, user.getName(), user.getPassword(), user.getEmail());
        } else {
            // 💡 새로운 회원을 등록합니다.
            String sql = "INSERT INTO users (email, userType, name, password) VALUES (?, ?, ?, ?)";

            KeyHolder keyHolder = new GeneratedKeyHolder();

            // 1. users 테이블에 새 유저 정보 삽입
            jdbc.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, user.getEmail());
                ps.setString(2, user.getUserType());
                ps.setString(3, user.getName());
                ps.setString(4, user.getPassword());
                return ps;
            }, keyHolder);

            // 2. 방금 생성된 userId를 가져옵니다.
            Number generatedId = keyHolder.getKey();
            if (generatedId != null) {
                int userId = generatedId.intValue();

                // 3. points 테이블에 초기 포인트 (0)를 삽입합니다.
                String pointsSql = "INSERT INTO points (userId, totalPoint) VALUES (?, ?)";
                jdbc.update(pointsSql, userId, 0);
            }
        }
        return user;
    }

    /**
     * 이메일로 회원 삭제
     */
    public void deleteByEmail(String email) {
        String sql = "DELETE FROM users WHERE email = ?";
        jdbc.update(sql, email);
    }

    /**
     * 이메일로 회원 조회 (출석 및 포인트 정보 포함)
     */
    public Optional<User> findByEmail(String email) {
        String sql = """
        SELECT
            u.userId, u.userType, u.name, u.password, u.email,
            CASE 
                WHEN ua.attendanceDate IS NOT NULL THEN ua.attendanceDate
                ELSE NULL 
            END as attendanceDate,
            COALESCE(p.totalPoint, 0) as totalPoint
        FROM users u 
        LEFT JOIN (
            SELECT userId, MAX(attendanceDate) as attendanceDate
            FROM user_attendance 
            WHERE DATE(attendanceDate) = CURDATE()
            GROUP BY userId
        ) ua ON u.userId = ua.userId
        LEFT JOIN (
            SELECT userId, totalPoint
            FROM (
                SELECT userId, totalPoint,
                       ROW_NUMBER() OVER (PARTITION BY userId ORDER BY pointId DESC) as rn
                FROM points
            ) ranked_points
            WHERE rn = 1
        ) p ON u.userId = p.userId
        WHERE u.email = ?
        """;
        try {
            User user = jdbc.queryForObject(sql, userRowMapper(), email);
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    /**
     * 이메일 존재 여부 확인
     */
    public boolean existsByEmail(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, email);
        return count != null && count > 0;
    }

    /**
     * 모든 회원을 조회합니다 (출석 및 포인트 정보 포함).
     */
    public List<User> findAllUsers() {
        String sql = """
        SELECT
            u.userId, u.userType, u.name, u.password, u.email,
            CASE 
                WHEN ua.attendanceDate IS NOT NULL THEN ua.attendanceDate
                ELSE NULL 
            END as attendanceDate,
            COALESCE(p.totalPoint, 0) as totalPoint
        FROM users u 
        LEFT JOIN (
            SELECT userId, MAX(attendanceDate) as attendanceDate
            FROM user_attendance 
            WHERE DATE(attendanceDate) = CURDATE()
            GROUP BY userId
        ) ua ON u.userId = ua.userId
        LEFT JOIN (
            SELECT userId, totalPoint
            FROM (
                SELECT userId, totalPoint,
                       ROW_NUMBER() OVER (PARTITION BY userId ORDER BY pointId DESC) as rn
                FROM points
            ) ranked_points
            WHERE rn = 1
        ) p ON u.userId = p.userId
        ORDER BY u.userId ASC
        """;
        return jdbc.query(sql, userRowMapper());
    }
    /**
     * DB 결과를 User 객체로 매핑합니다.
     */
    private RowMapper<User> userRowMapper() {
        return (rs, rowNum) -> {
            User user = new User();
            user.setUserId(rs.getInt("userId"));
            user.setEmail(rs.getString("email"));
            user.setUserType(rs.getString("userType"));
            user.setName(rs.getString("name"));
            user.setPassword(rs.getString("password"));

            // 출석 상태는 attendanceDate를 기반으로 결정합니다.
            try {
                java.sql.Timestamp attendanceTimestamp = rs.getTimestamp("attendanceDate");
                if (attendanceTimestamp != null) {
                    LocalDate attendanceDate = attendanceTimestamp.toLocalDateTime().toLocalDate();
                    user.setAttend(attendanceDate.isEqual(LocalDate.now()) ? "Y" : "N");
                } else {
                    user.setAttend("N");
                }
            } catch (SQLException e) {
                user.setAttend("N");
            }

            // 포인트 정보는 totalPoint를 가져옵니다.
            try {
                int totalPoint = rs.getInt("totalPoint");
                user.setPoint(rs.wasNull() ? 0 : totalPoint);
            } catch (SQLException e) {
                user.setPoint(0);
            }

            return user;
        };
    }

    /**
     * 총 회원 수를 반환합니다.
     */
    public int countAllUsers() {
        String sql = "SELECT COUNT(*) FROM users";
        return jdbc.queryForObject(sql, Integer.class);
    }

    /**
     * 페이징 처리된 회원 목록을 반환합니다.
     */
    public List<User> findUsersByPage(int page, int size) {
        String sql = """
        SELECT
            u.userId, u.userType, u.name, u.password, u.email,
            CASE 
                WHEN ua.attendanceDate IS NOT NULL THEN ua.attendanceDate
                ELSE NULL 
            END as attendanceDate,
            COALESCE(p.totalPoint, 0) as totalPoint
        FROM users u 
        LEFT JOIN (
            SELECT userId, MAX(attendanceDate) as attendanceDate
            FROM user_attendance 
            WHERE DATE(attendanceDate) = CURDATE()
            GROUP BY userId
        ) ua ON u.userId = ua.userId
        LEFT JOIN (
            SELECT userId, totalPoint
            FROM (
                SELECT userId, totalPoint,
                       ROW_NUMBER() OVER (PARTITION BY userId ORDER BY pointId DESC) as rn
                FROM points
            ) ranked_points
            WHERE rn = 1
        ) p ON u.userId = p.userId
        ORDER BY u.userId ASC
        LIMIT ? OFFSET ?
        """;
        int offset = (page - 1) * size;
        return jdbc.query(sql, userRowMapper(), size, offset);
    }

    public void resetAllAttendance() {
        // user_attendance 테이블에 있는 모든 출석 기록을 삭제하는 쿼리
        // 모든 출석 기록을 삭제하면, 출석 여부를 확인할 때 오늘 날짜의 기록이 없으므로
        // 모든 사용자의 출석 상태가 'N'으로 표시됩니다.
        String sql = "DELETE FROM user_attendance WHERE attendanceDate < CURDATE()";
        jdbc.update(sql);
    }

    /**
     * 매일 자정 출석 초기화 로직 (이 메서드는 더 이상 필요하지 않습니다.)
     * user_attendance 테이블에 기록을 삭제할 필요 없이,
     * 출석 여부는 '오늘 날짜'의 기록 존재 여부로 판단되므로, 이 메서드를 호출하지 않아도 됩니다.
     */
    // @Scheduled(cron = "0 0 0 * * *")
    // @Transactional
    // public void resetAllAttendance() {
    //     // 이 로직은 users 테이블에 attend 컬럼이 있을 때만 유효함
    // }
}