package com.lms.history.pointStore.repository;

import com.lms.history.pointStore.entity.PointStore;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class PointStoreRepository {

    private static JdbcTemplate jdbc = new JdbcTemplate();

    public PointStoreRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // point_shop 테이블에서 모든 상품 조회
    public List<PointStore> findAllProducts() {
        // 💡 SQL 쿼리에 brand 컬럼을 명시적으로 추가합니다.
        String sql = "SELECT itemId, category, brand, imgUrl, itemName, cost FROM point_shop";
        return jdbc.query(sql, pointStoreRowMapper());
    }

    // 카테고리별 상품 조회
    public List<PointStore> findByCategory(String category) {
        String sql = "SELECT * FROM point_shop WHERE category = ? ORDER BY cost";
        return jdbc.query(sql, pointStoreRowMapper(), category);
    }

    // 상품명과 가격으로 상품 조회
    public Optional<PointStore> findByItemNameAndCost(String itemName, int cost) {
        String sql = "SELECT * FROM point_shop WHERE itemName = ? AND cost = ?";
        try {
            PointStore item = jdbc.queryForObject(sql, pointStoreRowMapper(), itemName, cost);
            return Optional.ofNullable(item);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    // itemId로 상품 조회
    public Optional<PointStore> findById(int itemId) {
        String sql = "SELECT * FROM point_shop WHERE itemId = ?";
        try {
            PointStore item = jdbc.queryForObject(sql, pointStoreRowMapper(), itemId);
            return Optional.ofNullable(item);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    // 사용자별 구매 내역 조회 (points 테이블에서)
    public List<Map<String, Object>> findPurchaseHistoryByUserId(int userId, int limit) {
        String sql = """
                SELECT p.pointChange, p.totalPoint, ps.itemName, ps.category, ps.cost
                FROM points p 
                LEFT JOIN point_shop ps ON p.itemId = ps.itemId 
                WHERE p.userId = ? AND p.pointChange < 0 AND p.itemId IS NOT NULL
                ORDER BY p.pointId DESC 
                LIMIT ?
                """;

        return jdbc.queryForList(sql, userId, limit);
    }

    // 구매 통계 (points 테이블에서 itemId가 있는 음수 기록)
    public Map<String, Object> getPurchaseStats(int userId) {
        try {
            // 총 구매 횟수
            String countSql = "SELECT COUNT(*) FROM points WHERE userId = ? AND pointChange < 0 AND itemId IS NOT NULL";
            Integer totalPurchases = jdbc.queryForObject(countSql, Integer.class, userId);

            // 총 사용 포인트 (절댓값)
            String sumSql = "SELECT COALESCE(ABS(SUM(pointChange)), 0) FROM points WHERE userId = ? AND pointChange < 0 AND itemId IS NOT NULL";
            Integer totalSpent = jdbc.queryForObject(sumSql, Integer.class, userId);

            Map<String, Object> stats = new HashMap<>();
            stats.put("totalPurchases", totalPurchases != null ? totalPurchases : 0);
            stats.put("totalSpent", totalSpent != null ? totalSpent : 0);

            return stats;
        } catch (EmptyResultDataAccessException e) {
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalPurchases", 0);
            stats.put("totalSpent", 0);
            return stats;
        }
    }

    // 상품 추가 (관리자용)
    public PointStore saveItem(PointStore item) {
        String sql = "INSERT INTO point_shop (category, imgUrl, itemName, cost) VALUES (?, ?, ?, ?)";
        jdbc.update(sql, item.getCategory(), item.getImgUrl(), item.getItemName(), item.getCost());
        return item;
    }

    // 상품 수정 (관리자용)
    public boolean updateItem(PointStore item) {
        String sql = "UPDATE point_shop SET category = ?, imgUrl = ?, itemName = ?, cost = ? WHERE itemId = ?";
        int rowsAffected = jdbc.update(sql, item.getCategory(), item.getImgUrl(), item.getItemName(), item.getCost(), item.getItemId());
        return rowsAffected > 0;
    }

    // 상품 삭제 (관리자용)
    public boolean deleteItem(int itemId) {
        String sql = "DELETE FROM point_shop WHERE itemId = ?";
        int rowsAffected = jdbc.update(sql, itemId);
        return rowsAffected > 0;
    }

    // RowMapper
    private RowMapper<PointStore> pointStoreRowMapper() {
        return (rs, rowNum) -> {
            PointStore item = new PointStore();
            item.setItemId(rs.getInt("itemId"));
            item.setCategory(rs.getString("category"));
            item.setImgUrl(rs.getString("imgUrl"));
            item.setItemName(rs.getString("itemName"));
            item.setCost(rs.getInt("cost"));
            // ⭐ 이 줄을 추가하여 brand 컬럼을 객체에 매핑합니다.
            item.setBrand(rs.getString("brand"));
            return item;
        };
    }
}