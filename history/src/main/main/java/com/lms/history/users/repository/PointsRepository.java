package com.lms.history.users.repository;

import com.lms.history.users.entity.Points;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class PointsRepository {
    private final JdbcTemplate jdbc;

    public PointsRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /**
     * 포인트 기록 저장 (INSERT)
     */
    public Points save(Points points) {
        String sql = "INSERT INTO points (userId, attendanceId, itemId, scoreId, pointChange, totalPoint) VALUES (?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, points.getUserId());
            ps.setObject(2, points.getAttendanceId());
            ps.setObject(3, points.getItemId());
            ps.setObject(4, points.getScoreId());
            ps.setInt(5, points.getPointChange());
            ps.setInt(6, points.getTotalPoint());
            return ps;
        }, keyHolder);

        points.setPointId(keyHolder.getKey().intValue());
        return points;
    }

    /**
     * 환불 기록 생성 (pointChange = +환불액, totalPoint = 환불 후 총합)
     */
    public Points createRefundRecord(int userId, int itemId, int refundAmount) {
        Points refundRecord = new Points();
        refundRecord.setUserId(userId);
        refundRecord.setItemId(itemId);
        refundRecord.setPointChange(refundAmount); // +환불액

        Integer currentTotalPoint = this.getTotalPointsByUserId(userId); // 환불 전 총합
        refundRecord.setTotalPoint(currentTotalPoint + refundAmount);    // 환불 후 총합

        return this.save(refundRecord);
    }

    /**
     * (주의) DDL에 purchase_status/updated_at 컬럼이 없어 SQL 오류가 발생하므로
     * 안전하게 no-op으로 유지. 컬럼 추가 후 아래 주석의 업데이트 쿼리를 복원하여 사용하세요.
     */
    public int updatePurchaseStatus(int pointId, int userId, String status) {
        // String sql = "UPDATE points SET purchase_status = ?, updated_at = NOW() " +
        //         "WHERE pointId = ? AND userId = ? AND pointChange < 0";
        // return jdbc.update(sql, status, pointId, userId);
        return 0; // no-op
    }

    /**
     * 구매 내역 조회
     * - 같은 itemId를 여러 번 구매/사용/환불해도, 각 구매 N번째 ↔ 환불/사용 N번째만 1:1 매칭
     * - MySQL 8+ 윈도우 함수(ROW_NUMBER) 사용
     */
    public List<Map<String, Object>> getPurchaseHistoryByUserId(int userId) {
        String sql = """
            WITH
            purchases AS (
                SELECT
                    p.pointId, p.userId, p.itemId, p.pointChange, p.totalPoint,
                    ROW_NUMBER() OVER (PARTITION BY p.userId, p.itemId ORDER BY p.pointId) AS seq
                FROM points p
                WHERE p.userId = ? AND p.pointChange < 0
            ),
            refunds AS (
                SELECT
                    r.pointId, r.userId, r.itemId,
                    ROW_NUMBER() OVER (PARTITION BY r.userId, r.itemId ORDER BY r.pointId) AS seq
                FROM points r
                WHERE r.userId = ? AND r.pointChange > 0
            ),
            uses AS (
                SELECT
                    u.pointId, u.userId, u.itemId,
                    ROW_NUMBER() OVER (PARTITION BY u.userId, u.itemId ORDER BY u.pointId) AS seq
                FROM points u
                WHERE u.userId = ? AND u.pointChange = 0
            )
            SELECT
                p.pointId,
                p.userId,
                p.itemId,
                p.pointChange,
                p.totalPoint,
                CASE
                    WHEN r.seq IS NOT NULL THEN 'REFUNDED'
                    WHEN u.seq IS NOT NULL THEN 'USED'
                    ELSE 'PURCHASED'
                END AS purchaseStatus,
                ps.itemName,
                ps.brand,
                ps.category,
                ABS(p.pointChange) AS cost,
                COALESCE(ps.imgUrl, '') AS imgUrl
            FROM purchases p
            LEFT JOIN refunds r
              ON r.userId = p.userId AND r.itemId = p.itemId AND r.seq = p.seq
            LEFT JOIN uses u
              ON u.userId = p.userId AND u.itemId = p.itemId AND u.seq = p.seq
            LEFT JOIN point_shop ps ON p.itemId = ps.itemId
            ORDER BY p.pointId DESC
            """;

        return jdbc.query(sql, (rs, rowNum) -> {
            Map<String, Object> result = new HashMap<>();
            result.put("pointId", rs.getInt("pointId"));
            result.put("userId", rs.getInt("userId"));
            result.put("itemId", rs.getInt("itemId"));
            result.put("pointChange", rs.getInt("pointChange"));
            result.put("totalPoint", rs.getInt("totalPoint"));
            result.put("purchaseStatus", rs.getString("purchaseStatus"));
            result.put("itemName", rs.getString("itemName"));
            result.put("brand", rs.getString("brand"));
            result.put("category", rs.getString("category"));
            result.put("cost", rs.getInt("cost"));
            result.put("imgUrl", rs.getString("imgUrl"));
            return result;
        }, userId, userId, userId);
    }

    /**
     * 특정 구매 기록 조회 (pointId 기준)
     * - 상태 판정도 seq 매칭으로 일관 처리
     */
    public Optional<Map<String, Object>> getPurchaseRecord(int pointId, int userId) {
        String sql = """
            WITH
            purchases AS (
                SELECT
                    p.pointId, p.userId, p.itemId, p.pointChange,
                    ROW_NUMBER() OVER (PARTITION BY p.userId, p.itemId ORDER BY p.pointId) AS seq
                FROM points p
                WHERE p.userId = ? AND p.pointChange < 0
            ),
            refunds AS (
                SELECT
                    r.pointId, r.userId, r.itemId,
                    ROW_NUMBER() OVER (PARTITION BY r.userId, r.itemId ORDER BY r.pointId) AS seq
                FROM points r
                WHERE r.userId = ? AND r.pointChange > 0
            ),
            uses AS (
                SELECT
                    u.pointId, u.userId, u.itemId,
                    ROW_NUMBER() OVER (PARTITION BY u.userId, u.itemId ORDER BY u.pointId) AS seq
                FROM points u
                WHERE u.userId = ? AND u.pointChange = 0
            )
            SELECT 
                p.pointId,
                p.userId,
                p.itemId,
                ps.cost,
                ps.itemName,
                CASE
                    WHEN r.seq = p.seq THEN 'REFUNDED'
                    WHEN u.seq = p.seq THEN 'USED'
                    ELSE 'PURCHASED'
                END AS purchase_status
            FROM purchases p
            LEFT JOIN refunds r
              ON r.userId = p.userId AND r.itemId = p.itemId AND r.seq = p.seq
            LEFT JOIN uses u
              ON u.userId = p.userId AND u.itemId = p.itemId AND u.seq = p.seq
            JOIN point_shop ps ON p.itemId = ps.itemId
            WHERE p.pointId = ?
            """;

        try {
            Map<String, Object> result = jdbc.queryForMap(sql, userId, userId, userId, pointId);
            return Optional.of(result);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    /**
     * 사용자 총 포인트 합계
     */
    public Integer getTotalPointsByUserId(int userId) {
        String sql = "SELECT COALESCE(SUM(pointChange), 0) FROM points WHERE userId = ?";
        Integer result = jdbc.queryForObject(sql, Integer.class, userId);
        return result != null ? result : 0;
    }

    /**
     * 포인트 기록 저장 시 totalPoint 자동계산 버전
     */
    public Points saveWithCalculatedTotal(Points points) {
        Integer currentTotal = getTotalPointsByUserId(points.getUserId());
        points.setTotalPoint(currentTotal + points.getPointChange());
        return save(points);
    }

    /**
     * 사용 기록 생성 (pointChange = 0, totalPoint 변화 없음)
     */
    public Points createUseRecord(int userId, int itemId) {
        Points useRecord = new Points();
        useRecord.setUserId(userId);
        useRecord.setItemId(itemId);
        useRecord.setPointChange(0); // 사용은 포인트 변화 없음

        Integer currentTotalPoint = this.getTotalPointsByUserId(userId); // 사용 전 총합
        useRecord.setTotalPoint(currentTotalPoint);                      // 사용 후 총합 = 동일

        return this.save(useRecord);
    }

    /** 구매(<0) 건수 */
    public long countPurchases(int userId, int itemId) {
        String sql = """
        SELECT COUNT(*) 
        FROM points 
        WHERE userId = ? AND itemId = ? AND pointChange < 0
    """;
        Long cnt = jdbc.queryForObject(sql, Long.class, userId, itemId);
        return cnt != null ? cnt : 0L;
    }

    /** 사용(=0) 건수 */
    public long countUses(int userId, int itemId) {
        String sql = """
        SELECT COUNT(*) 
        FROM points 
        WHERE userId = ? AND itemId = ? AND pointChange = 0
    """;
        Long cnt = jdbc.queryForObject(sql, Long.class, userId, itemId);
        return cnt != null ? cnt : 0L;
    }

    /** 환불(>0) 건수 */
    public long countRefunds(int userId, int itemId) {
        String sql = """
        SELECT COUNT(*) 
        FROM points 
        WHERE userId = ? AND itemId = ? AND pointChange > 0
    """;
        Long cnt = jdbc.queryForObject(sql, Long.class, userId, itemId);
        return cnt != null ? cnt : 0L;
    }
}