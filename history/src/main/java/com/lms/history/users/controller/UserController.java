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
public class UserController {
    private static final int PAGE_SIZE = 10;

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 회원 등록
    @GetMapping("/user/add")
    public String addForm(Model model) {
        model.addAttribute("user", new User());
        return "users/addUser";
    }

    @PostMapping("/user/add")
    public String addUser(@ModelAttribute User user,
                          RedirectAttributes message) {
        try {
            userService.join(user);
            return "redirect:/";
        } catch (IllegalStateException e) {
            message.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/user/add";
        }
    }

    @PostMapping("/user/login")
    public String login(@RequestParam("email") String email,
                        @RequestParam("password") String password,
                        HttpServletRequest request,
                        RedirectAttributes redirectAttributes) {

        Optional<User> userOptional = userService.findByEmail(email);

        if (!userOptional.isPresent()) {
            // 아이디가 없는 경우, home으로 리다이렉트하며 notFound 에러 전달
            redirectAttributes.addFlashAttribute("loginError", "notFound");
            return "redirect:/";
        }

        Optional<User> loginResult = userService.login(email, password);

        if (loginResult.isPresent()) {
            HttpSession session = request.getSession();
            session.setAttribute("loginUser", loginResult.get());
            return "redirect:/";
        } else {
            // 아이디는 있지만 비밀번호가 틀린 경우, home으로 리다이렉트하며 invalidPassword 에러 전달
            redirectAttributes.addFlashAttribute("loginError", "invalidPassword");
            return "redirect:/";
        }
    }

    @GetMapping("/user/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession();
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/";
    }

    // 마이페이지
    @GetMapping("/user/mypage")
    public String mypage(HttpSession session, Model model) {
        Object loginUser = session.getAttribute("loginUser");
        if (loginUser != null) {
            model.addAttribute("loginUser", (User) loginUser);
            return "users/myPage";
        } else {
            return "redirect:/user/login";
        }
    }

    // 회원 정보 수정 폼 (GET)
    @GetMapping("/user/edit/{email}")
    public String userEditForm(@PathVariable String email, Model model, HttpSession session) {
        Object loginUserObject = session.getAttribute("loginUser");
        if (!(loginUserObject instanceof User)) {
            return "redirect:/user/login";
        }
        User userToEdit = userService.findByEmail(email).orElseThrow(
                () -> new IllegalArgumentException("Invalid user email "));

        if (!"A".equals(((User) loginUserObject).getUserType()) && !((User) loginUserObject).getEmail().equals(userToEdit.getEmail())) {
            return "redirect:/user/mypage";
        }

        model.addAttribute("user", userToEdit);
        return "users/editUser";
    }

    // 회원 정보 수정을 처리하는 메서드 (POST)
    @PostMapping("/user/edit")
    public String userEdit(@ModelAttribute User user, HttpSession session) {
        Object loginUserObject = session.getAttribute("loginUser");
        if (!(loginUserObject instanceof User)) {
            return "redirect:/user/login";
        }
        userService.update(user);

        if ("A".equals(((User) loginUserObject).getUserType())) {
            return "redirect:/admin/users";
        }
        return "redirect:/user/mypage";
    }

    // 회원 삭제 처리
    @PostMapping("/user/delete")
    public String deleteUser(@RequestParam String email, HttpSession session, RedirectAttributes redirectAttributes) {
        Object loginUserObject = session.getAttribute("loginUser");
        if (!(loginUserObject instanceof User) || !"A".equals(((User) loginUserObject).getUserType())) {
            redirectAttributes.addFlashAttribute("errorMessage", "삭제 권한이 없습니다.");
            return "redirect:/user/login";
        }

        userService.deleteByEmail(email);
        return "redirect:/admin/users";
    }
}
