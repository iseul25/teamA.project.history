package com.lms.history.users.controller;

import com.lms.history.users.entity.User;
import com.lms.history.users.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // ---------------- 회원 등록 ----------------
    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("user", new User());
        return "users/addUser";
    }

    @PostMapping("/add")
    public String addUser(@ModelAttribute User user, RedirectAttributes redirectAttributes) {
        try {
            userService.join(user);
            return "redirect:/";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/user/add";
        }
    }

    // ---------------- 로그인 / 로그아웃 ----------------
    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpServletRequest request,
                        RedirectAttributes redirectAttributes) {
        Optional<User> loginResult = userService.login(email, password);

        if (loginResult.isPresent()) {
            HttpSession session = request.getSession();
            session.setAttribute("loginUser", loginResult.get());
            return "redirect:/";
        } else {
            redirectAttributes.addFlashAttribute("loginError", "아이디 또는 비밀번호를 확인해주세요.");
            return "redirect:/";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) session.invalidate();
        return "redirect:/";
    }

    // ---------------- 마이페이지 ----------------
    @GetMapping("/myPage")
    public String myPage(HttpSession session, Model model) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/user/login";

        // 세션 유저 최신 정보 조회
        User user = userService.findByEmail(loginUser.getEmail()).orElse(loginUser);
        model.addAttribute("user", user);

        // Attend 엔티티 기준 출석 상태 조회
        String attendanceStatus = userService.getAttendanceStatus(user.getUserId());
        model.addAttribute("attendanceStatus", attendanceStatus);

        return "users/myPage";
    }

    // ---------------- 마이페이지 회원 정보 수정 ----------------
    @PostMapping("/myPage/update")
    public String updateMyPage(@ModelAttribute User updatedUser, HttpSession session, RedirectAttributes redirectAttributes) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/user/login";

        try {
            userService.updateUser(loginUser.getEmail(), updatedUser);
            session.setAttribute("loginUser", updatedUser);
            redirectAttributes.addFlashAttribute("message", "회원 정보가 성공적으로 수정되었습니다.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/user/myPage?edit=true";
        }

        return "redirect:/user/myPage";
    }

    // ---------------- 이메일 중복 확인 ----------------
    @GetMapping("/check-email")
    @ResponseBody
    public String checkEmail(@RequestParam String email) {
        return userService.existsByEmail(email) ? "duplicate" : "available";
    }

    // ---------------- 회원 정보 수정 폼 ----------------
    @GetMapping("/edit/{email}")
    public String userEditForm(@PathVariable String email, Model model, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/user/login";

        User userToEdit = userService.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        if (!"A".equals(loginUser.getUserType()) && !loginUser.getEmail().equals(userToEdit.getEmail())) {
            return "redirect:/user/myPage";
        }

        model.addAttribute("user", userToEdit);
        return "users/editUser";
    }
}
