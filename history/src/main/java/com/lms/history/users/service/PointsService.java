package com.lms.history.users.service;

import com.lms.history.users.entity.Points;
import com.lms.history.users.repository.PointsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class PointsService {

    private final PointsRepository pointsRepository;

    public PointsService(PointsRepository pointsRepository) {
        this.pointsRepository = pointsRepository;
    }

    /**
     * 출석 포인트 적립
     * @param userId 사용자 ID
     * @param attendanceId 출석 기록 ID
     * @param pointChange 적립할 포인트
     * @return 적립 후 총 포인트
     */
    public int addAttendancePoint(int userId, int attendanceId, int pointChange) {
        // 현재 총 포인트 조회
        int currentTotal = getTotalPoints(userId);
        int newTotal = currentTotal + pointChange;

        // 포인트 기록 생성 및 저장
        Points points = new Points(userId, attendanceId, pointChange, newTotal);
        pointsRepository.save(points);

        return newTotal;
    }

    /**
     * 사용자의 현재 총 포인트 조회
     * @param userId 사용자 ID
     * @return 현재 총 포인트
     */
    public int getTotalPoints(int userId) {
        return pointsRepository.getTotalPointsByUserId(userId);
    }

    /**
     * 사용자의 포인트샵 구매 내역 조회
     * @param userId 사용자 ID
     * @return 구매 내역 목록
     */
    public List<Map<String, Object>> getPurchaseHistory(int userId) {
        return pointsRepository.getPurchaseHistoryByUserId(userId);
    }

    /**
     * 포인트샵 구매 시 포인트 차감
     * @param userId 사용자 ID
     * @param itemId 상품 ID
     * @param cost 상품 가격
     * @return 차감 후 총 포인트
     */
    public int purchaseItem(int userId, int itemId, int cost) {
        int currentTotal = getTotalPoints(userId);
        if (currentTotal < cost) {
            throw new IllegalArgumentException("포인트가 부족합니다.");
        }

        int newTotal = currentTotal - cost;
        Points points = new Points();
        points.setUserId(userId);
        points.setItemId(itemId);
        points.setPointChange(-cost); // 차감이므로 음수
        points.setTotalPoint(newTotal);

        pointsRepository.save(points);
        return newTotal;
    }

    /**
     * 포인트 기록 저장 시 자동으로 totalPoint 계산
     */
    public Points addPointRecord(int userId, Integer attendanceId, Integer itemId,
                                 Integer scoreId, int pointChange) {
        Points points = new Points();
        points.setUserId(userId);
        points.setAttendanceId(attendanceId);
        points.setItemId(itemId);
        points.setScoreId(scoreId);
        points.setPointChange(pointChange);

        return pointsRepository.saveWithCalculatedTotal(points);
    }

    /**
     * 환불 처리 - 수정된 버전
     */
    /** 환불: +포인트 기록 추가 → 총합 복원 */
    @Transactional
    public Map<String, Object> refundItem(int pointId, int userId) {
        Map<String, Object> purchase = pointsRepository.getPurchaseRecord(pointId, userId)
                .orElseThrow(() -> new IllegalArgumentException("구매 기록을 찾을 수 없습니다."));

        String status = String.valueOf(purchase.get("purchase_status"));
        if ("REFUNDED".equalsIgnoreCase(status)) throw new IllegalArgumentException("이미 환불된 상품입니다.");
        if ("USED".equalsIgnoreCase(status))     throw new IllegalArgumentException("이미 사용된 상품입니다.");

        int itemId       = (Integer) purchase.get("itemId");
        int refundAmount = (Integer) purchase.get("cost"); // 환불액 = 구매 차감액

        // 1) 환불(+포인트) 기록 추가 → 총합 복원
        Points refundRecord = pointsRepository.createRefundRecord(userId, itemId, refundAmount);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("refundAmount", refundAmount);
        result.put("newTotalPoint", refundRecord.getTotalPoint());
        result.put("status", "REFUNDED");
        return result;
    }

    /**
     * 상품 사용 처리
     * - 포인트 변화는 없고, 구매건의 상태만 'USED'로 변경
     */
    /** 사용: 0포인트 사용기록 추가 → 총합 불변 */
    @Transactional
    public Map<String, Object> useItem(int pointId, int userId) {
        Map<String, Object> purchase = pointsRepository.getPurchaseRecord(pointId, userId)
                .orElseThrow(() -> new IllegalArgumentException("구매 기록을 찾을 수 없습니다."));

        String status = String.valueOf(purchase.get("purchase_status"));
        if ("REFUNDED".equalsIgnoreCase(status)) throw new IllegalArgumentException("이미 환불된 상품입니다.");
        if ("USED".equalsIgnoreCase(status))     throw new IllegalArgumentException("이미 사용된 상품입니다.");

        int itemId = (Integer) purchase.get("itemId");

        // 0포인트 사용기록 추가 (총합 변화 없음)
        Points useRecord = pointsRepository.createUseRecord(userId, itemId);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "사용 처리되었습니다.");
        result.put("newTotalPoint", useRecord.getTotalPoint()); // 변동 없음
        result.put("status", "USED");
        result.put("pointId", pointId);
        return result;
    }

    /** 사용 가능 잔여 수량: 구매 - 사용 + 환불 */
    public long remainCount(int userId, int itemId) {
        long purchases = pointsRepository.countPurchases(userId, itemId);
        long uses      = pointsRepository.countUses(userId, itemId);
        long refunds   = pointsRepository.countRefunds(userId, itemId);
        return purchases - uses + refunds;
    }

    /** 같은 상품이라도 '한 번 호출 = 1개만 사용' */
    @Transactional
    public Points useOne(int userId, int itemId) {
        long remain = remainCount(userId, itemId);
        if (remain <= 0) {
            throw new IllegalStateException("사용 가능한 수량이 없습니다.");
        }

        // pointChange=0, totalPoint 변화 없음
        return pointsRepository.createUseRecord(userId, itemId);
    }
}