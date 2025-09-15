package com.lms.history.admin.controller;

import com.lms.history.admin.service.AdminService;
import com.lms.history.users.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class AdminApiController {

    private final AdminService adminService;

    public AdminApiController(AdminService adminService) {
        this.adminService = adminService;
    }

    // ✅ 이메일 중복 확인
    @GetMapping("/check-email")
    public ResponseEntity<Map<String, Boolean>> checkEmail(@RequestParam String email) {
        boolean exists = adminService.isEmailDuplicated(email);
        Map<String, Boolean> result = new HashMap<>();
        result.put("exists", exists);
        return ResponseEntity.ok(result);
    }

    // ✅ 회원 등록 (AJAX 요청 처리용)
    @PostMapping
    public ResponseEntity<Map<String, String>> registerUser(@RequestBody User user) {
        Map<String, String> response = new HashMap<>();

        try {
            user.setUserType("일반유저"); // 기본 유저 타입 설정
            adminService.registerUser(user);
            response.put("message", "회원 등록 성공");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "회원 등록 실패: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ✅ 전체 유저 목록 반환 (프론트에서 fetch용)
    @GetMapping
    public ResponseEntity<?> getUsers() {
        return ResponseEntity.ok(adminService.findAllUsers()); // 필요시 페이징으로 수정
    }

    // ✅ 회원 삭제 (AJAX 요청 처리용)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable String email) {
        try {
            adminService.deleteByEmail(email);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
