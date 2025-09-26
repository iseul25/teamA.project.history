package com.lms.history.pointStore.service;

import com.lms.history.pointStore.entity.PointStore;
import com.lms.history.pointStore.repository.PointStoreRepository;
import com.lms.history.users.entity.Points;
import com.lms.history.users.repository.PointsRepository;
import com.lms.history.users.repository.UserRepository;
import com.lms.history.users.service.PointsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class PointStoreService {
    private final UserRepository userRepository;
    private final PointsRepository pointsRepository;
    private final PointsService pointsService;
    private final PointStoreRepository pointStoreRepository;

    public PointStoreService(UserRepository userRepository,
                             PointsRepository pointsRepository,
                             PointsService pointsService,
                             PointStoreRepository pointStoreRepository) {
        this.userRepository = userRepository;
        this.pointsRepository = pointsRepository;
        this.pointsService = pointsService;
        this.pointStoreRepository = pointStoreRepository;
    }

    public List<PointStore> getAllProducts() {
        return pointStoreRepository.findAllProducts();
    }

    @Transactional
    public Map<String, Object> purchaseProduct(int userId, String productName, int price) {
        Map<String, Object> response = new HashMap<>();

        try {
            // 사용자 존재 확인
            if (!userRepository.existsById(userId)) {
                response.put("success", false);
                response.put("message", "사용자를 찾을 수 없습니다.");
                return response;
            }

            // 현재 포인트 조회
            int currentPoint = pointsService.getTotalPoints(userId);

            // 포인트 부족 체크
            if (currentPoint < price) {
                response.put("success", false);
                response.put("message", "보유 포인트가 부족습니다.");
                response.put("currentPoint", currentPoint);
                return response;
            }

            // point_shop 테이블에서 상품 정보 확인 (선택적)
            Optional<PointStore> shopItem = pointStoreRepository.findByItemNameAndCost(productName, price);
            Integer itemId = shopItem.map(PointStore::getItemId).orElse(null);

            // 포인트 차감을 위한 Points 기록 생성
            int newTotalPoint = currentPoint - price;

            Points pointsRecord = new Points();
            pointsRecord.setUserId(userId);
            pointsRecord.setAttendanceId(null);  // 상품 구매는 출석과 무관
            pointsRecord.setItemId(itemId);      // point_shop의 itemId 연결
            pointsRecord.setPointChange(-price); // 음수로 차감
            pointsRecord.setTotalPoint(newTotalPoint);

            // Points 테이블에 기록 저장
            pointsRepository.save(pointsRecord);

            // 성공 응답
            response.put("success", true);
            response.put("message", "구매가 완료되었습니다.");
            response.put("remainingPoint", newTotalPoint);
            response.put("purchasedItem", productName);

            return response;

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "구매 처리 중 오류가 발생했습니다.");
            System.err.println("Purchase error: " + e.getMessage());
            e.printStackTrace();
            return response;
        }
    }
}