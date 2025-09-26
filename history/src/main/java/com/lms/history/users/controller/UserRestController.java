package com.lms.history.users.controller;

import com.lms.history.users.entity.Points;
import com.lms.history.users.entity.User;
import com.lms.history.users.service.PointsService;
import com.lms.history.users.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserRestController {

    private final UserService userService;
    @Getter
    private final PointsService pointsService;

    public UserRestController(UserService userService, PointsService pointsService) {
        this.userService = userService;
        this.pointsService = pointsService;
    }

    // ---------------- 로그인 상태 확인 ----------------
    @GetMapping("/check-login")
    public ResponseEntity<LoginStatus> checkLogin(HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser != null) {
            return ResponseEntity.ok(new LoginStatus(true, loginUser.getName()));
        } else {
            return ResponseEntity.ok(new LoginStatus(false, null));
        }
    }

    private static class LoginStatus {
        private final boolean isLoggedIn;
        private final String username;

        public LoginStatus(boolean isLoggedIn, String username) {
            this.isLoggedIn = isLoggedIn;
            this.username = username;
        }

        public boolean isLoggedIn() {
            return isLoggedIn;
        }

        public String getUsername() {
            return username;
        }
    }

    // ---------------- 마이페이지 조회 ----------------
    @GetMapping("/myPage")
    public ResponseEntity<MyPageResponse> getMyPage(HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            User user = userService.findByEmail(loginUser.getEmail()).orElse(null);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            // 🚩 포인트는 항상 points 테이블 기준으로 조회
            int totalPoints = pointsService.getTotalPoints(user.getUserId());
            String attendanceStatus = userService.getAttendanceStatus(user.getUserId());

            MyPageResponse response = new MyPageResponse(
                    user.getName(),
                    user.getEmail(),
                    attendanceStatus,
                    totalPoints  // 수정: points 테이블 기준
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    private static class MyPageResponse {
        private final String name;
        private final String email;
        private final String attendance;
        private final int point;

        public MyPageResponse(String name, String email, String attendance, int point) {
            this.name = name;
            this.email = email;
            this.attendance = attendance;
            this.point = point;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }

        public String getAttendance() {
            return attendance;
        }

        public int getPoint() {
            return point;
        }
    }

    // ---------------- 회원 정보 수정 ----------------
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody User updatedUser, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            userService.updateUser(loginUser.getEmail(), updatedUser);
            loginUser.setName(updatedUser.getName());
            loginUser.setEmail(updatedUser.getEmail());
            session.setAttribute("loginUser", loginUser);

            return ResponseEntity.ok(loginUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"error\": \"회원 정보 수정 중 오류가 발생했습니다.\"}");
        }
    }

    // ---------------- 이메일 중복 확인 ----------------
    @GetMapping("/check-email")
    public ResponseEntity<EmailCheckResponse> checkEmail(@RequestParam String email) {
        try {
            boolean exists = userService.isEmailExists(email);
            return ResponseEntity.ok(new EmailCheckResponse(exists));
        } catch (Exception e) {
            // 예외 발생 시 서버 오류로 처리
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new EmailCheckResponse(true)); // 서버 오류는 "이미 사용 중인 이메일"로 처리
        }
    }

    private static class EmailCheckResponse {
        private final boolean exists;

        public EmailCheckResponse(boolean exists) {
            this.exists = exists;
        }

        public boolean isExists() {
            return exists;
        }
    }

    // ---------------- 회원 탈퇴 ----------------
    @DeleteMapping("/withdraw")
    public ResponseEntity<String> withdraw(HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인된 사용자가 없습니다.");
        }

        try {
            userService.deleteUser(loginUser.getUserId());
            session.invalidate(); // 세션 무효화
            return ResponseEntity.ok("탈퇴가 완료되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("탈퇴 처리 중 오류가 발생했습니다.");
        }
    }

    @GetMapping("/purchase-history")
    public ResponseEntity<List<Map<String, Object>>> getPurchaseHistory(HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            System.out.println("DEBUG: 로그인 사용자 없음");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            System.out.println("DEBUG: 사용자 ID - " + loginUser.getUserId());
            List<Map<String, Object>> purchaseHistory = pointsService.getPurchaseHistory(loginUser.getUserId());
            System.out.println("DEBUG: 구매 내역 조회 성공, 개수: " + purchaseHistory.size());
            return ResponseEntity.ok(purchaseHistory);
        } catch (Exception e) {
            System.out.println("DEBUG: 에러 발생 - " + e.getMessage());
            e.printStackTrace(); // 상세한 스택 트레이스 출력
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 환불 API 추가
    @PostMapping("/refund-item/{pointId}")
    public ResponseEntity<Map<String, Object>> refundItem(@PathVariable int pointId, HttpSession session) {
        Map<String, Object> body = new HashMap<>();
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            body.put("success", false);
            body.put("message", "로그인이 필요합니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
        }
        try {
            Map<String, Object> result = pointsService.refundItem(pointId, loginUser.getUserId());
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            body.put("success", false);
            body.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
        } catch (Exception e) {
            body.put("success", false);
            body.put("message", "서버 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
        }
    }

    @PostMapping("/use-item/{pointId}")
    public ResponseEntity<Map<String, Object>> useItem(@PathVariable int pointId, HttpSession session) {
        Map<String, Object> body = new HashMap<>();
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            body.put("success", false);
            body.put("message", "로그인이 필요합니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
        }
        try {
            Map<String, Object> result = pointsService.useItem(pointId, loginUser.getUserId());
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            body.put("success", false);
            body.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
        } catch (Exception e) {
            body.put("success", false);
            body.put("message", "서버 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
        }
    }

    // 총 포인트만 조회하는 API 추가
    @GetMapping("/total-point")
    public ResponseEntity<Map<String, Object>> getTotalPoint(HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            int totalPoints = pointsService.getTotalPoints(loginUser.getUserId());
            Map<String, Object> response = new HashMap<>();
            response.put("totalPoint", totalPoints);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /** 사용 처리: 한 번 호출 = 1개만 pointChange=0으로 기록 */
    @PostMapping("/items/{itemId}/use") // ✅ 최종 경로: /api/users/items/{itemId}/use
    public ResponseEntity<Map<String, Object>> useOne(@PathVariable int itemId, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "로그인이 필요합니다."));
        }

        int userId = loginUser.getUserId();
        try {
            // 1) 사용 1건 생성 (pointChange=0, totalPoint 변화 없음)
            Points used = pointsService.useOne(userId, itemId);

            // 2) 응답 본문 구성
            Map<String, Object> body = new HashMap<>();
            body.put("success", true);
            body.put("itemId", itemId);

            // (선택) 아래 두 줄은 서비스/레포에 보조 메서드가 있을 때만 사용하세요.
            // 없다면 주석 처리해도 프론트는 재조회로 정상 동작합니다.
            // long useCount = pointsService.countUses(userId, itemId); // 사용 누계
            // Integer matchedPointId = pointsService.findPurchasePointIdBySeq(userId, itemId, (int) useCount);

            // body.put("matchedPointId", matchedPointId);   // ✅ 있으면 프론트가 해당 줄만 즉시 '사용완료'로 갱신
            // body.put("gifticonUrl", pointsService.findGifticonUrlByItemId(itemId).orElse(null)); // 선택

            return ResponseEntity.ok(body);

        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    /** 남은 사용 가능 수량 조회(옵션) */
    @GetMapping("/items/{itemId}/remain") // ✅ 최종 경로: /api/users/items/{itemId}/remain
    public ResponseEntity<Map<String, Object>> remain(@PathVariable int itemId, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "로그인이 필요합니다."));
        }
        long remain = pointsService.remainCount(loginUser.getUserId(), itemId);
        return ResponseEntity.ok(Map.of("success", true, "remain", remain));
    }
}