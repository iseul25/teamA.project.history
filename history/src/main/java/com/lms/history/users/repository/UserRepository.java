package com.lms.history.users.repository;

import com.lms.history.users.entity.User;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository {
    private final JdbcTemplate jdbc;

    public UserRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // 🚩 수정된 save 메서드
    public User save(User user) {
        if (user.getUserId() > 0) {
            String sql = "UPDATE users SET userType = ?, name = ?, password = ?, email = ? WHERE userId = ?";
            jdbc.update(sql,
                    user.getUserType(),
                    user.getName(),
                    user.getPassword(),
                    user.getEmail(),
                    user.getUserId()
            );
        } else {
            String sql = "INSERT INTO users (userType, name, password, email) VALUES (?,?,?,?)";
            jdbc.update(sql,
                    user.getUserType(),
                    user.getName(),
                    user.getPassword(),
                    user.getEmail()
            );
        }
        return user;
    }

    // 이메일로 회원 조회
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try {
            User user = jdbc.queryForObject(sql, userRowMapper(), email);
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    // ID로 회원 조회
    public Optional<User> findById(int id) {
        String sql = "SELECT * FROM users WHERE userId = ?";
        try {
            User user = jdbc.queryForObject(sql, userRowMapper(), id);
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    // 회원 전체 조회 (포인트 제외)
    public List<User> findAll() {
        String sql = "SELECT * FROM users";
        return jdbc.query(sql, userRowMapper());
    }

    // 🚩 회원 전체 조회 (누적 포인트 포함)
    public List<User> findAllWithPoints() {
        String sql =
                "SELECT u.userId, u.userType, u.name, u.password, u.email, " +
                        "       COALESCE(SUM(p.pointChange), 0) AS totalPoint " +
                        "FROM users u " +
                        "LEFT JOIN points p ON u.userId = p.userId " +
                        "GROUP BY u.userId, u.userType, u.name, u.password, u.email";

        return jdbc.query(sql, (rs, rowNum) -> {
            User user = new User();
            user.setUserId(rs.getInt("userId"));
            user.setUserType(rs.getString("userType"));
            user.setName(rs.getString("name"));
            user.setPassword(rs.getString("password"));
            user.setEmail(rs.getString("email"));
            user.setPoint(rs.getInt("totalPoint")); // 🚩 User 엔티티에 point 필드 있어야 함
            return user;
        });
    }

    // 회원 삭제 (ID 기준)
    public void deleteById(int userId) {
        String sql = "DELETE FROM users WHERE userId = ?";
        jdbc.update(sql, userId);
    }

    // RowMapper (findAll, findById, findByEmail 등에서 사용)
    private RowMapper<User> userRowMapper() {
        return (rs, rowNum) -> {
            User user = new User();
            user.setUserId(rs.getInt("userId"));
            user.setUserType(rs.getString("userType"));
            user.setName(rs.getString("name"));
            user.setPassword(rs.getString("password"));
            user.setEmail(rs.getString("email"));
            // 포인트는 points 테이블에서 관리하므로 여기서는 매핑 안 함
            return user;
        };
    }

    // 이메일 중복 여부 체크
    public boolean existsByEmail(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, email);
        return count != null && count > 0;
    }

    // 로그인 (이메일 + 비밀번호)
    public Optional<User> findByEmailAndPassword(String email, String password) {
        String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
        try {
            User user = jdbc.queryForObject(sql, userRowMapper(), email, password);
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    // 사용자 존재 여부 (ID 기준)
    public boolean existsById(int userId) {
        String sql = "SELECT COUNT(*) FROM users WHERE userId = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, userId);
        return count != null && count > 0;
    }

    // 특정 사용자 총 포인트 조회
    public Integer getTotalPointByUserId(int userId) {
        String sql = "SELECT COALESCE(SUM(pointChange), 0) FROM points WHERE userId = ?";
        return jdbc.queryForObject(sql, Integer.class, userId);
    }

    public Integer getAttendanceCountByUserId(int userId) {
        String sql = "SELECT COUNT(*) FROM attendance WHERE userId = ?";
        try {
            Integer count = jdbc.queryForObject(sql, Integer.class, userId);
            return count != null ? count : 0;
        } catch (EmptyResultDataAccessException e) {
            return 0;
        }
    }

    public JdbcTemplate getJdbcTemplate() {
        return this.jdbc;
    }
}
