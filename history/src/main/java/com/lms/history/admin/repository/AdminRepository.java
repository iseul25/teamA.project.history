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

    /**
     * 유저 정보 저장 또는 업데이트
     */
    public User save(User user) {
        if (findByEmail(user.getEmail()).isPresent()) {
            // 이미 존재하는 유저: UPDATE 쿼리 실행
            String sql = "UPDATE user SET name = ?, password = ? WHERE email = ?";
            jdbc.update(sql, user.getName(), user.getPassword(), user.getEmail());
        } else {
            // 새로운 유저: INSERT 쿼리 실행
            String sql = "INSERT INTO user (email, userType, name, password) VALUES (?, ?, ?, ?)";
            jdbc.update(sql, user.getEmail(), user.getUserType(), user.getName(), user.getPassword());
        }
        return user;
    }

    /**
     * 유저 정보 삭제
     */
    public void deleteByEmail(String email) {
        String sql = "DELETE FROM user WHERE email = ?";
        jdbc.update(sql, email);
    }

    /**
     * 이메일로 회원 조회
     */
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM user WHERE email = ?";
        try {
            User user = jdbc.queryForObject(sql, userRowMapper(), email);
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    /**
     * 이메일이 데이터베이스에 존재하는지 확인하는 메서드입니다.
     * count(*)를 사용하여 존재 여부만 확인하므로, User 객체를 로드하는 것보다 더 효율적입니다.
     *
     * @param email 확인할 이메일 주소
     * @return 이메일이 존재하면 true, 존재하지 않으면 false
     */
    public boolean existsByEmail(String email) {
        String sql = "SELECT COUNT(*) FROM user WHERE email = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, email);
        return count != null && count > 0;
    }

    /**
     * 모든 사용자의 목록을 데이터베이스에서 조회합니다.
     */
    public List<User> findAllUsers() {
        String sql = "SELECT email, userType, userName FROM user";
        return jdbc.query(sql, userRowMapper());
    }

    /**
     * User 엔티티에 데이터베이스 결과를 매핑하는 RowMapper를 정의합니다.
     */
    private RowMapper<User> userRowMapper() {
        return (rs, rowNum) -> {
            User user = new User();
            user.setEmail(rs.getString("email"));
            user.setUserType(rs.getString("userType"));
            user.setName(rs.getString("name"));
            user.setPassword(rs.getString("password"));
            return user;
        };
    }

    /**
     * 총 회원 수 반환
     */
    public int countAllUsers() {
        String sql = "SELECT COUNT(*) FROM user";
        return jdbc.queryForObject(sql, Integer.class);
    }

    /**
     * 페이징 처리된 회원 목록 반환
     */
    public List<User> findUsersByPage(int page, int size) {
        String sql = "SELECT * FROM user LIMIT ? OFFSET ?";
        int offset = (page - 1) * size;
        return jdbc.query(sql, userRowMapper(), size, offset);
    }
}
