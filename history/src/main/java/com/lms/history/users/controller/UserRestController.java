package com.lms.history.users.controller;

import com.lms.history.users.entity.User;
import com.lms.history.users.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserRestController {

    private final UserService userService;

    public UserRestController(UserService userService) {
        this.userService = userService;
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

        public boolean isLoggedIn() { return isLoggedIn; }
        public String getUsername() { return username; }
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

            String attendanceStatus = userService.getAttendanceStatus(user.getUserId());

            MyPageResponse response = new MyPageResponse(
                    user.getName(),
                    user.getEmail(),
                    attendanceStatus,
                    user.getPoint()
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null); // 서버 오류 시 빈 응답을 반환
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

        public String getName() { return name; }
        public String getEmail() { return email; }
        public String getAttendance() { return attendance; }
        public int getPoint() { return point; }
    }

    // ---------------- 회원 정보 수정 ----------------
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody User updatedUser, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            // 🚩 수정: userService의 updateUser 메서드를 호출하여 데이터베이스를 업데이트합니다.
            //        업데이트된 사용자 정보를 반환받아 세션에 저장합니다.
            User resultUser = userService.updateUser(loginUser.getEmail(), updatedUser);

            // 🚩 수정: 세션에 저장된 사용자 정보를 최신 정보로 업데이트합니다.
            session.setAttribute("loginUser", resultUser);

            return ResponseEntity.ok(resultUser);
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
        public EmailCheckResponse(boolean exists) { this.exists = exists; }
        public boolean isExists() { return exists; }
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
}