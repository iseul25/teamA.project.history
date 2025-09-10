package com.lms.history.admin.controller;

import com.lms.history.admin.service.AdminService;
import com.lms.history.users.entity.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private static final int PAGE_SIZE = 10;

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // 관리자 페이지 메인
    @GetMapping("/page")
    public String adminPage(HttpSession session, Model model) {
        Object loginUserObject = session.getAttribute("loginUser");
        if (loginUserObject instanceof User loginUser) {
            if ("관리자".equals(loginUser.getUserType())) {
                model.addAttribute("loginUser", loginUser);
                return "admin/adminPage";
            }
        }
        return "redirect:/user/login";
    }

    // 회원 목록
    @GetMapping("/users")
    public String userList(@RequestParam(defaultValue = "1") int page, Model model, HttpSession session) {
        Object loginUserObject = session.getAttribute("loginUser");
        if (!(loginUserObject instanceof User) || !"관리자".equals(((User) loginUserObject).getUserType())) {
            return "redirect:/user/login";
        }

        List<User> users = adminService.findUsersByPage(page, PAGE_SIZE);
        int totalUsers = adminService.countAllUsers();
        int totalPages = (int) Math.ceil((double) totalUsers / PAGE_SIZE);

        model.addAttribute("users", users);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalUsers", totalUsers);

        return "admin/adminUserList";
    }

    /**
     * 이메일 중복 확인을 위한 API 엔드포인트입니다.
     * GET 요청으로 이메일을 받아 중복 여부를 boolean 값으로 반환합니다.
     *
     * @param email 확인할 이메일 주소
     * @return 이메일이 중복되면 true, 아니면 false
     */
    @GetMapping("/user/check-email")
    @ResponseBody
    public ResponseEntity<Boolean> checkEmailDuplicate(@RequestParam String email) {
        boolean isDuplicated = adminService.isEmailDuplicated(email);
        return ResponseEntity.ok(isDuplicated);
    }

    // 회원 등록
    @GetMapping("/user/register")
    public String showAddAdminUserPage(Model model) {
        model.addAttribute("user", new User());
        return "admin/adminAddUser";
    }

    @PostMapping("/user/register")
    public String registerUser(@ModelAttribute("user") User user, Model model) {
        try {
            user.setUserType("일반유저");
            adminService.registerUser(user);
            model.addAttribute("successMessage", "유저 등록이 완료되었습니다.");
            model.addAttribute("user", new User());
        } catch (Exception e) {
            model.addAttribute("errorMessage", "유저 등록에 실패했습니다: " + e.getMessage());
        }
        return "admin/adminAddUser";
    }

    // 회원 삭제
    @GetMapping("/users/delete")
    public String deleteUser(@RequestParam String email, HttpSession session) {
        Object loginUserObject = session.getAttribute("loginUser");
        if (!(loginUserObject instanceof User) || !"관리자".equals(((User) loginUserObject).getUserType())) {
            return "redirect:/user/login";
        }

        Optional<User> userToDelete = adminService.findByEmail(email);
        if (userToDelete.isPresent()) {
            User user = userToDelete.get();
            if ("관리자".equals(user.getUserType())) {
                System.out.println("관리자 계정은 삭제할 수 없습니다: " + email);
            } else {
                adminService.deleteByEmail(email);
            }
        } else {
            System.out.println("삭제하려는 회원이 존재하지 않습니다: " + email);
        }
        return "redirect:/admin/users";
    }

    // 회원 상세 정보 보기
    @GetMapping("/users/details/{email}")
    public String userDetails(@PathVariable String email, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        Object loginUserObject = session.getAttribute("loginUser");
        if (!(loginUserObject instanceof User loginUser) || !"관리자".equals(loginUser.getUserType())) {
            return "redirect:/user/login";
        }

        if (loginUser.getEmail().equals(email)) {
            redirectAttributes.addFlashAttribute("errorMessage", "관리자는 본인의 상세 정보를 조회할 수 없습니다.");
            return "redirect:/admin/users";
        }

        Optional<User> userOptional = adminService.findByEmail(email);
        if (userOptional.isPresent()) {
            model.addAttribute("user", userOptional.get());
            return "admin/adminUserDetail";
        } else {
            return "redirect:/admin/users";
        }
    }

    // 회원 정보 수정 폼으로 이동
    @GetMapping("/users/edit/{email}")
    public String showEditUserPage(@PathVariable String email, Model model, HttpSession session) {
        Object loginUserObject = session.getAttribute("loginUser");
        if (!(loginUserObject instanceof User) || !"관리자".equals(((User) loginUserObject).getUserType())) {
            return "redirect:/user/login";
        }

        Optional<User> userOptional = adminService.findByEmail(email);
        if (userOptional.isPresent()) {
            model.addAttribute("user", userOptional.get());
            return "admin/adminUserDetail";
        } else {
            return "redirect:/admin/users";
        }
    }

    // 회원 정보 수정 처리
    @PostMapping("/users/edit")
    public String editUser(@ModelAttribute("user") User user, HttpSession session, Model model) {
        Object loginUserObject = session.getAttribute("loginUser");
        if (!(loginUserObject instanceof User) || !"관리자".equals(((User) loginUserObject).getUserType())) {
            return "redirect:/user/login";
        }

        try {
            adminService.save(user);
            // 수정이 완료된 후 회원 목록 페이지로 리다이렉트
            return "redirect:/admin/users";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "유저 정보 수정에 실패했습니다: " + e.getMessage());
            model.addAttribute("user", user);
            return "admin/adminUserDetail";
        }
    }
}