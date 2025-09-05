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
        String sql = "INSERT INTO user (userType, name, password, email) " +
                " VALUES (?,?,?,?)";
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

    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM user WHERE email = ?";
        try {
            User user = jdbc.queryForObject(sql, userRowMapper(), email);
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<User> findByName(String name) {
        String sql = "SELECT * FROM user WHERE name = ?";
        try {
            User user = jdbc.queryForObject(sql, userRowMapper(), name);
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<User> findAll() {
        String sql = "SELECT * FROM user";
        return jdbc.query(sql, userRowMapper());
    }

    private RowMapper<User> userRowMapper() {
        return (rs, rowNum) -> {
            return new User(
                    rs.getInt("userId"),
                    rs.getString("userType"),
                    rs.getString("name"),
                    rs.getString("password"),
                    rs.getString("email")
            );
        };
    }

    // 수정
    public void update(User user) {
        String sql = "UPDATE user SET password = ? WHERE userId = ?";
        jdbc.update(sql,
                user.getPassword(),
                user.getUserId()
        );
    }

    // 삭제
    public void deleteByEmail(String email) {
        String sql = "DELETE FROM user WHERE email = ?";
        jdbc.update(sql, email);
    }
}
