package com.lms.history.admin.repository;

import com.lms.history.users.entity.User;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class AdminRepository {

    private final JdbcTemplate jdbc;

    public AdminRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public User save(User user) {
        String sql = "INSERT INTO users (email, user_type, name, password) VALUES (?, ?, ?, ?)";
        jdbc.update(sql, user.getEmail(), user.getUser_type(), user.getName(), user.getPassword());
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
    /**
     * 모든 사용자의 목록을 데이터베이스에서 조회합니다.
     *
     * @return 모든 사용자의 리스트
     */
    public List<User> findAllUsers() {
        String sql = "SELECT email, user_type, name FROM user";
        return jdbc.query(sql, userRowMapper());
    }

    /**
     * User 엔티티에 데이터베이스 결과를 매핑하는 RowMapper를 정의합니다.
     * @return RowMapper 객체
     */
    private RowMapper<User> userRowMapper() {
        return (rs, rowNum) -> {
            User user = new User();
            user.setEmail(rs.getString("email"));
            user.setUser_type(rs.getString("user_type"));
            user.setName(rs.getString("name"));
            //user.setPoints(rs.getInt("points"));
            return user;
        };
    }
    // 총 회원 수 반환
    public int countAllUsers() {
        String sql = "SELECT COUNT(*) FROM users";
        return jdbc.queryForObject(sql, Integer.class);
    }

    // 페이징 처리된 회원 목록 반환
    public List<User> findUsersByPage(int page, int size) {
        String sql = "SELECT * FROM users LIMIT ? OFFSET ?";
        int offset = page * size;
        return jdbc.query(sql, userRowMapper(), size, offset);
    }
}
