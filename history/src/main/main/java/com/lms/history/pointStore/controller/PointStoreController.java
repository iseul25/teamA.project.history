package com.lms.history.pointStore.controller;

import com.lms.history.pointStore.entity.PointStore;
import com.lms.history.pointStore.repository.PointStoreRepository;
import com.lms.history.pointStore.service.PointStoreService;
import com.lms.history.users.entity.User;
import com.lms.history.users.service.PointsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class PointStoreController {
    private final PointStoreService pointStoreService;
    private final PointsService pointsService;

    public PointStoreController(PointStoreService pointStoreService, PointsService pointsService) {
        this.pointStoreService = pointStoreService;
        this.pointsService = pointsService;
    }

    // 로그인 체크 API (포인트 정보 포함)
    @GetMapping("/check-login")
    public ResponseEntity<Map<String, Object>> checkLogin(HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");

        Map<String, Object> response = new HashMap<>();

        if (loginUser == null) {
            response.put("isLoggedIn", false);
            return ResponseEntity.ok(response);
        }

        try {
            // 최신 포인트 정보 조회
            int currentPoint = pointsService.getTotalPoints(loginUser.getUserId());

            response.put("isLoggedIn", true);
            response.put("username", loginUser.getName());
            response.put("point", currentPoint);
            response.put("role", "관리자".equals(loginUser.getUserType()) ? "admin" : "user");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("isLoggedIn", false);
            return ResponseEntity.ok(response);
        }
    }

    // 상품 구매 API
    @PostMapping("/purchase")
    public ResponseEntity<Map<String, Object>> purchase(
            @RequestBody Map<String, Object> request,
            HttpSession session) {

        User loginUser = (User) session.getAttribute("loginUser");

        Map<String, Object> response = new HashMap<>();

        if (loginUser == null) {
            response.put("success", false);
            response.put("message", "로그인이 필요합니다.");
            return ResponseEntity.status(401).body(response);
        }

        try {
            String productName = (String) request.get("productName");
            Object priceObj = request.get("price");

            // 입력값 검증
            if (productName == null || priceObj == null) {
                response.put("success", false);
                response.put("message", "잘못된 상품 정보입니다.");
                return ResponseEntity.badRequest().body(response);
            }

            int price;
            try {
                price = (Integer) priceObj;
                if (price <= 0) {
                    throw new IllegalArgumentException();
                }
            } catch (Exception e) {
                response.put("success", false);
                response.put("message", "잘못된 가격 정보입니다.");
                return ResponseEntity.badRequest().body(response);
            }

            // 구매 처리
            Map<String, Object> purchaseResult = pointStoreService.purchaseProduct(
                    loginUser.getUserId(),
                    productName,
                    price
            );

            return ResponseEntity.ok(purchaseResult);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "구매 처리 중 오류가 발생했습니다.");
            return ResponseEntity.status(500).body(response);
        }
    }

    // 현재 포인트 조회 API
    @GetMapping("/user/point")
    public ResponseEntity<Map<String, Object>> getUserPoint(HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");

        Map<String, Object> response = new HashMap<>();

        if (loginUser == null) {
            response.put("error", "로그인이 필요합니다.");
            return ResponseEntity.status(401).body(response);
        }

        try {
            int point = pointsService.getTotalPoints(loginUser.getUserId());
            response.put("point", point);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "포인트 조회 중 오류가 발생했습니다.");
            return ResponseEntity.status(500).body(response);
        }
    }

    // 상품 목록 조회 API (일반 사용자용)
    @GetMapping("/products")
    public ResponseEntity<Map<String, Object>> getProducts() {
        Map<String, Object> response = new HashMap<>();
        try {
            // ⭐ Service를 통해 모든 상품 목록을 가져오도록 수정합니다.
            List<PointStore> products = pointStoreService.getAllProducts();

            response.put("success", true);
            response.put("products", products);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "상품 목록 조회 중 오류가 발생했습니다.");
            return ResponseEntity.status(500).body(response);
        }
    }
}