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
        // user.getUserId()가 0보다 크면 (즉, ID가 이미 할당된 기존 사용자면)
        if (user.getUserId() > 0) {
            // UPDATE 쿼리 실행
            String sql = "UPDATE users SET userType = ?, name = ?, password = ?, email = ? WHERE userId = ?";
            jdbc.update(sql,
                    user.getUserType(),
                    user.getName(),
                    user.getPassword(),
                    user.getEmail(),
                    user.getUserId()
            );
        } else {
            // 신규 사용자이므로 INSERT 쿼리 실행
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

    // 회원 전체 조회
    public List<User> findAll() {
        String sql = "SELECT * FROM users";
        return jdbc.query(sql, userRowMapper());
    }

    // 🚩 기존 update 메서드는 삭제 또는 주석 처리하는 것을 추천합니다.
    // 현재 코드에서는 사용하지 않으므로 혼란을 방지하기 위함입니다.
    // public void update(User user) { ... }

    // 🚩 updateProfile 메서드는 더 이상 필요하지 않습니다.
    // save 메서드가 이 기능을 대신합니다.
    // public int updateProfile(String currentEmail, String newName, String newEmail) { ... }

    // 회원 삭제 (ID 기준)
    public void deleteById(int userId) {
        String sql = "DELETE FROM users WHERE userId = ?";
        jdbc.update(sql, userId);
    }

    // RowMapper
    private RowMapper<User> userRowMapper() {
        return (rs, rowNum) -> {
            User user = new User();
            user.setUserId(rs.getInt("userId"));
            user.setUserType(rs.getString("userType"));
            user.setName(rs.getString("name"));
            user.setPassword(rs.getString("password"));
            user.setEmail(rs.getString("email"));
            // 🚩 포인트와 다른 필드도 매핑하는 것을 권장합니다.
            // user.setPoint(rs.getInt("point"));
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

    // 포인트는 points 테이블에서 관리
    public Integer getTotalPointByUserId(int userId) {
        String sql = "SELECT totalPoint FROM points WHERE userId = ?";
        try {
            return jdbc.queryForObject(sql, Integer.class, userId);
        } catch (EmptyResultDataAccessException e) {
            return 0; // 포인트 정보가 없을 경우 0으로 반환
        }
    }

    public Integer getAttendanceCountByUserId(int userId) {
        String sql = "SELECT COUNT(*) FROM attendance WHERE userId = ?";
        try {
            Integer count = jdbc.queryForObject(sql, Integer.class, userId);
            return count != null ? count : 0;
        } catch (EmptyResultDataAccessException e) {
            return 0; // 출석 정보가 없을 경우 0으로 반환
        }
    }

    public JdbcTemplate getJdbcTemplate() {
        return this.jdbc;
    }
}