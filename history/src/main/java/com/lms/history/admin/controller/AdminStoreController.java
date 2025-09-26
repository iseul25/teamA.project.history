package com.lms.history.admin.controller;

import com.lms.history.admin.service.AdminStoreService;
import com.lms.history.pointStore.entity.PointStore;
import com.lms.history.users.entity.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/store")
public class AdminStoreController {
    private final AdminStoreService adminStoreService;

    public AdminStoreController(AdminStoreService adminStoreService) {
        this.adminStoreService = adminStoreService;
    }

    private boolean isAdmin(HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        return loginUser != null && "관리자".equals(loginUser.getUserType());
    }

    // --- 상품 관련 API ---

    @GetMapping("/products")
    public ResponseEntity<Map<String, Object>> getProducts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size,
            HttpSession session) {
        // ... 기존 코드와 동일 ...
        Map<String, Object> response = new HashMap<>();
        if (!isAdmin(session)) {
            response.put("success", false);
            response.put("message", "관리자 권한이 필요합니다.");
            return ResponseEntity.status(403).body(response);
        }
        try {
            Map<String, Object> result = adminStoreService.getProductsWithPaging(page, size);
            response.put("success", true);
            response.putAll(result);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "상품 목록을 불러오는 중 오류가 발생했습니다.");
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/products/{itemId}")
    public ResponseEntity<Map<String, Object>> getProductById(@PathVariable int itemId, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        if (!isAdmin(session)) {
            response.put("success", false);
            response.put("message", "관리자 권한이 필요합니다.");
            return ResponseEntity.status(403).body(response);
        }
        PointStore product = adminStoreService.getProductById(itemId);
        if (product != null) {
            response.put("success", true);
            response.put("product", product);
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "상품을 찾을 수 없습니다.");
            return ResponseEntity.status(404).body(response);
        }
    }

    // ✅ 수정된 부분 1: @PostMapping 경로 변경 및 @RequestPart 수정
    @PostMapping(value = "/products", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> addProduct(
            @RequestParam("itemName") String itemName,
            @RequestParam("cost") int cost,
            @RequestParam("category") String category,
            @RequestParam("brand") String brand,
            @RequestPart(value = "imgFile", required = false) MultipartFile imgFile, // required = false 추가
            HttpSession session) {
        // ... 기존 코드와 거의 동일 ...
        Map<String, Object> response = new HashMap<>();
        if (!isAdmin(session)) {
            response.put("success", false);
            response.put("message", "관리자 권한이 필요합니다.");
            return ResponseEntity.status(403).body(response);
        }
        try {
            PointStore newProduct = adminStoreService.addProduct(itemName, cost, category, brand, imgFile);
            response.put("success", true);
            response.put("message", "상품이 추가되었습니다.");
            response.put("product", newProduct);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            response.put("success", false);
            response.put("message", "이미지 파일 처리 중 오류가 발생했습니다.");
            return ResponseEntity.status(500).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "상품 추가 중 오류가 발생했습니다.");
            return ResponseEntity.status(500).body(response);
        }
    }

    // ✅ 수정된 부분 2: @PutMapping 경로 및 파라미터 전체 수정
    @PutMapping(value = "/products/{itemId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> updateProduct(
            @PathVariable int itemId,
            @RequestParam("itemName") String itemName,
            @RequestParam("cost") int cost,
            @RequestParam("category") String category,
            @RequestParam("brand") String brand,
            @RequestPart(value = "imgFile", required = false) MultipartFile imgFile,
            HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        if (!isAdmin(session)) {
            response.put("success", false);
            response.put("message", "관리자 권한이 필요합니다.");
            return ResponseEntity.status(403).body(response);
        }

        try {
            // 서비스 로직 호출 (서비스 레이어 수정 필요)
            boolean isUpdated = adminStoreService.updateProduct(itemId, itemName, cost, category, brand, imgFile);

            if (isUpdated) {
                response.put("success", true);
                response.put("message", "상품 정보가 성공적으로 수정되었습니다.");
            } else {
                response.put("success", false);
                response.put("message", "상품을 찾을 수 없거나 수정에 실패했습니다.");
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "상품 수정 중 오류가 발생했습니다.");
            return ResponseEntity.status(500).body(response);
        }
    }


    // ✅ 수정된 부분 3: @DeleteMapping 경로 변경
    @DeleteMapping("/products/{itemId}")
    public ResponseEntity<Map<String, Object>> deleteProduct(@PathVariable int itemId, HttpSession session) {
        // ... 기존 코드와 동일 ...
        Map<String, Object> response = new HashMap<>();
        if (!isAdmin(session)) {
            response.put("success", false);
            response.put("message", "관리자 권한이 필요합니다.");
            return ResponseEntity.status(403).body(response);
        }
        try {
            boolean isDeleted = adminStoreService.deleteProduct(itemId);
            if (isDeleted) {
                response.put("success", true);
                response.put("message", "상품이 성공적으로 삭제되었습니다.");
            } else {
                response.put("success", false);
                response.put("message", "상품을 찾을 수 없습니다.");
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "상품 삭제 중 오류가 발생했습니다.");
            return ResponseEntity.status(500).body(response);
        }
    }

    // --- 주문(구매 내역) 관련 API ---

    @GetMapping("/orders")
    public ResponseEntity<Map<String, Object>> getOrders(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size,
            HttpSession session) {
        // ... 기존 코드와 동일 ...
        Map<String, Object> response = new HashMap<>();
        if (!isAdmin(session)) {
            response.put("success", false);
            response.put("message", "관리자 권한이 필요합니다.");
            return ResponseEntity.status(403).body(response);
        }
        try {
            Map<String, Object> result = adminStoreService.getOrdersWithPaging(page, size);
            response.put("success", true);
            response.putAll(result);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "주문 목록을 불러오는 중 오류가 발생했습니다.");
            return ResponseEntity.status(500).body(response);
        }
    }
}