package com.lms.history.admin.controller;

import com.lms.history.admin.service.AdminService;
import com.lms.history.users.entity.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

// ✅ 모든 관리자 API 요청은 /api/admin/users 아래에 둡니다.
@RestController
@RequestMapping("/api/admin/users")
public class AdminApiController {

    private final AdminService adminService;

    public AdminApiController(AdminService adminService) {
        this.adminService = adminService;
    }

    // ✅ 전체 유저 목록 반환: /api/admin/users (GET)
    @GetMapping
    public ResponseEntity<?> getUsers() {
        return ResponseEntity.ok(adminService.findAllUsers());
    }

    // ✅ 회원 등록: /api/admin/users/register (POST)
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerUser(@RequestBody User user) {
        Map<String, String> response = new HashMap<>();
        try {
            user.setUserType("일반유저");
            adminService.registerUser(user);
            response.put("message", "회원 등록 성공");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            response.put("message", "회원 등록 실패: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("message", "회원 등록 중 서버 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ✅ 이메일 중복 확인: /api/admin/users/check-email (GET)
    @GetMapping("/check-email")
    public ResponseEntity<Map<String, Boolean>> checkEmail(@RequestParam String email) {
        boolean exists = adminService.isEmailDuplicated(email);
        Map<String, Boolean> result = new HashMap<>();
        result.put("exists", exists);
        return ResponseEntity.ok(result);
    }
}