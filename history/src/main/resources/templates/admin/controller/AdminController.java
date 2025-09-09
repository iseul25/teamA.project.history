package com.lms.history.admin.controller;

import com.lms.history.admin.service.AdminService;
import com.lms.history.users.entity.User;
import com.lms.history.users.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private static final int PAGE_SIZE = 10;

    private final UserService userService;
    private final AdminService adminService; // AdminService 객체를 추가합니다.

    // 생성자를 수정하여 AdminService도 함께 주입받도록 합니다.
    public AdminController(UserService userService, AdminService adminService) {
        this.userService = userService;
        this.adminService = adminService;
    }

    // 관리자 페이지 메인
    @GetMapping("/page")
    public String adminPage(HttpSession session, Model model) {
        Object loginUserObject = session.getAttribute("loginUser");
        if (loginUserObject instanceof User) {
            User loginUser = (User) loginUserObject;
            if ("A".equals(loginUser.getUserType())) { // A는 관리자 권한을 의미
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
        if (!(loginUserObject instanceof User) || !"A".equals(((User) loginUserObject).getUserType())) {
            return "redirect:/user/login";
        }

        List<User> users = userService.findUsersByPage(page, PAGE_SIZE);
        int totalUsers = userService.countAllUsers();
        int totalPages = (int) Math.ceil((double) totalUsers / PAGE_SIZE);

        model.addAttribute("users", users);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalUsers", totalUsers);

        return "admin/adminUserList";
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
            // ⭐ 이 부분을 추가하여 userType을 'U'로 강제 설정합니다.
            user.setUserType("USER");

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
        if (!(loginUserObject instanceof User) || !"A".equals(((User) loginUserObject).getUserType())) {
            return "redirect:/user/login";
        }

        // 삭제할 회원의 userType을 확인
        Optional<User> userToDelete = userService.findByEmail(email);
        if (userToDelete.isPresent()) {
            User user = userToDelete.get();
            // userType이 'A'인 경우 삭제 불가능
            if ("A".equals(user.getUserType())) {
                System.out.println("관리자 계정은 삭제할 수 없습니다: " + email);
            } else {
                userService.deleteByEmail(email);
            }
        } else {
            System.out.println("삭제하려는 회원이 존재하지 않습니다: " + email);
        }

        return "redirect:/admin/users";
    }
}
