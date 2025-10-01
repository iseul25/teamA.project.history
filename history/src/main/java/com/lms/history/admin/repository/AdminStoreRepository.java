package com.lms.history.admin.repository;

import com.lms.history.pointStore.entity.PointStore;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

@Repository
public class AdminStoreRepository {
    private final JdbcTemplate jdbc;

    public AdminStoreRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // 상품 목록 조회 (페이징)
    public List<PointStore> findProductsByPage(int page, int size) {
        String sql = "SELECT itemId, category, brand, imgUrl, itemName, cost FROM point_shop ORDER BY itemId DESC LIMIT ? OFFSET ?"; // <<-- brand 컬럼 추가
        int offset = (page - 1) * size;
        return jdbc.query(sql, pointStoreRowMapper(), size, offset);
    }

    // 전체 상품 수 조회
    public int countAllProducts() {
        String sql = "SELECT COUNT(*) FROM point_shop";
        Integer count = jdbc.queryForObject(sql, Integer.class);
        return count != null ? count : 0;
    }

    // 상품 수정
    public boolean updateProduct(int itemId, String itemName, int cost, String brand, String category, String imgUrl) {
        String sql = "UPDATE point_shop SET itemName = ?, cost = ?, category = ?, brand = ? , imgUrl = ? WHERE itemId = ?"; // <<-- brand 컬럼 추가
        int rowsAffected = jdbc.update(sql, itemName, cost, category, brand, imgUrl, itemId);
        return rowsAffected > 0;
    }

    // 상품 삭제
    public boolean deleteProduct(int itemId) {
        String sql = "DELETE FROM point_shop WHERE itemId = ?";
        int rowsAffected = jdbc.update(sql, itemId);
        return rowsAffected > 0;
    }

    // 상품 추가
    public PointStore saveProduct(PointStore product) {
        String sql = "INSERT INTO point_shop (category, brand, imgUrl, itemName, cost) VALUES (?, ?, ?, ?, ?)"; // <<-- brand 컬럼 추가

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, product.getCategory());
            ps.setString(2, product.getBrand()); // <<-- 이 줄을 추가하세요.
            ps.setString(3, product.getImgUrl());
            ps.setString(4, product.getItemName());
            ps.setInt(5, product.getCost());
            return ps;
        }, keyHolder);

        Number generatedId = keyHolder.getKey();
        if (generatedId != null) {
            product.setItemId(generatedId.intValue());
        }

        return product;
    }

    // 구매 내역 조회 (페이징) - points 테이블에서 itemId가 있는 음수 기록들
    public List<Map<String, Object>> findOrdersByPage(int page, int size) {
        String sql = """
                SELECT 
                    p.pointId,
                    u.name as username,
                    u.email,
                    ps.itemName as productName,
                    ABS(p.pointChange) as price,
                    DATE_FORMAT(p.createAt, '%Y-%m-%d') as orderDate
                FROM points p
                JOIN users u ON p.userId = u.userId
                LEFT JOIN point_shop ps ON p.itemId = ps.itemId
                WHERE p.pointChange < 0 AND p.itemId IS NOT NULL
                ORDER BY p.pointId DESC
                LIMIT ? OFFSET ?
                """;

        int offset = (page - 1) * size;
        return jdbc.queryForList(sql, size, offset);
    }

    // 전체 주문 수 조회
    public int countAllOrders() {
        String sql = "SELECT COUNT(*) FROM points WHERE pointChange < 0 AND itemId IS NOT NULL";
        Integer count = jdbc.queryForObject(sql, Integer.class);
        return count != null ? count : 0;
    }

    // RowMapper
    private RowMapper<PointStore> pointStoreRowMapper() {
        return (rs, rowNum) -> {
            PointStore product = new PointStore();
            product.setItemId(rs.getInt("itemId"));
            product.setCategory(rs.getString("category"));
            product.setBrand(rs.getString("brand")); // <<-- 이 줄을 추가하세요.
            product.setImgUrl(rs.getString("imgUrl"));
            product.setItemName(rs.getString("itemName"));
            product.setCost(rs.getInt("cost"));
            return product;
        };
    }

    // 상품 단일 조회
    public PointStore findProductById(int itemId) {
        String sql = "SELECT * FROM point_shop WHERE itemId = ?";
        try {
            return jdbc.queryForObject(sql, pointStoreRowMapper(), itemId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}
