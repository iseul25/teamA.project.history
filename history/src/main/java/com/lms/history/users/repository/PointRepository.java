package com.lms.history.users.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class PointRepository {
    private final JdbcTemplate jdbc;

    public PointRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public Integer getTotalPointByUserId(int userId) {
        String sql = "SELECT totalPoint FROM points WHERE userId = ?";
        return jdbc.queryForObject(sql, Integer.class, userId);
    }

    // 포인트 관련 메서드들 추가 가능
}
