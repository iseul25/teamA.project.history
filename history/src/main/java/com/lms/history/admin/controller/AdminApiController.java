package com.lms.history.admin.controller;

import com.lms.history.admin.service.AdminService;
import com.lms.history.users.entity.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/users")
public class AdminApiController {

    private final AdminService adminService;

    public AdminApiController(AdminService adminService) {
        this.adminService = adminService;
    }

    // ✅ 전체 유저 목록 반환
    // URL: /api/admin/users (GET)
    @GetMapping
    public ResponseEntity<?> getUsers() {
        return ResponseEntity.ok(adminService.findAllUsers());
    }

    // ✅ 회원 등록 (AJAX 요청 처리용)
    // URL: /api/admin/users/register (POST)
    @PostMapping("/register") // 🚩 수정: 회원 등록을 위한 별도 URL 추가
    public ResponseEntity<Map<String, String>> registerUser(@RequestBody User user) {
        Map<String, String> response = new HashMap<>();
        try {
            user.setUserType("일반유저"); // 기본 유저 타입 설정
            adminService.registerUser(user);
            response.put("message", "회원 등록 성공");
            return ResponseEntity.status(HttpStatus.CREATED).body(response); // 🚩 수정: HTTP 상태 코드를 201 Created로 변경
        } catch (IllegalArgumentException e) { // 🚩 수정: 구체적인 예외 처리
            response.put("message", "회원 등록 실패: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("message", "회원 등록 중 서버 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ✅ 이메일 중복 확인
    // URL: /api/admin/users/check-email (GET)
    @GetMapping("/check-email")
    public ResponseEntity<Map<String, Boolean>> checkEmail(@RequestParam String email) {
        boolean exists = adminService.isEmailDuplicated(email);
        Map<String, Boolean> result = new HashMap<>();
        result.put("exists", exists);
        return ResponseEntity.ok(result);
    }

    // ✅ 회원 삭제 (AJAX 요청 처리용)
    // URL: /api/admin/users/{email} (DELETE)
    @DeleteMapping("/{email}") // 🚩 수정: 경로 변수를 email로 변경하여 명확하게 함
    public ResponseEntity<?> deleteUser(@PathVariable String email) { // 🚩 수정: @PathVariable의 변수명을 email로 통일
        try {
            adminService.deleteByEmail(email);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "회원 삭제 중 오류가 발생했습니다."));
        }
    }
}