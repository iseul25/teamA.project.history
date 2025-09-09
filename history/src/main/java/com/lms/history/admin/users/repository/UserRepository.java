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

    // 회원정보 저장
    public User save(User user) {
        String sql = "INSERT INTO users (userType, name, password, email) VALUES (?,?,?,?)";
        int result = jdbc.update(sql,
                user.getUserType(),
                user.getName(),
                user.getPassword(),
                user.getEmail()
        );
        if (result == 1) {
            return user;
        } else {
            return null;
        }
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

    // 이름으로 회원 조회 (사용하지 않는 경우 삭제 가능)
    public Optional<User> findByName(String name) {
        String sql = "SELECT * FROM users WHERE name = ?";
        try {
            User user = jdbc.queryForObject(sql, userRowMapper(), name);
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    // ID로 회원 조회
    public Optional<User> findById(Long id) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        try {
            User user = jdbc.queryForObject(sql, userRowMapper(), id);
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    // 모든 회원 목록 조회
    public List<User> findAll() {
        String sql = "SELECT * FROM users";
        return jdbc.query(sql, userRowMapper());
    }

    // 관리자 페이지에 필요한 회원 목록 조회
    public List<User> findAllUsers() {
        String sql = "SELECT user_id, email, userType, name FROM users";
        return jdbc.query(sql, userRowMapper());
    }

    // 총 회원 수 반환
    public int countAllUsers() {
        String sql = "SELECT COUNT(*) FROM users";
        return jdbc.queryForObject(sql, Integer.class);
    }

    // 페이징 처리된 회원 목록 반환
    public List<User> findUsersByPage(int page, int size) {
        String sql = "SELECT * FROM users LIMIT ? OFFSET ?";
        int offset = (page - 1) * size;
        return jdbc.query(sql, userRowMapper(), size, offset);
    }

    // 데이터베이스 컬럼을 User 객체에 매핑
    private RowMapper<User> userRowMapper() {
        return (rs, rowNum) -> {
            User user = new User();
            //user.setUserId(rs.getInt("user_id"));
            user.setEmail(rs.getString("email"));
            // userType 필드를 그대로 사용
            user.setUserType(rs.getString("userType"));
            user.setName(rs.getString("name"));
            user.setPassword(rs.getString("password"));
            // 필요한 경우 주석 해제하여 사용
            // user.setSignDay(rs.getDate("signDay").toLocalDate());
            // user.setPoints(rs.getInt("points"));
            return user;
        };
    }

    // 수정
    public void update(User user) {
        String sql = "UPDATE users SET password = ? WHERE email = ?";
        jdbc.update(sql,
                user.getPassword(),
                user.getEmail()
        );
    }

    // 삭제
    public void deleteByEmail(String email) {
        String sql = "DELETE FROM users WHERE email = ?";
        jdbc.update(sql, email);
    }
}
