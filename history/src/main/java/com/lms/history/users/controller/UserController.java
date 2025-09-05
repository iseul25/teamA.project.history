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
            return "redirect:/"; // 가입 성공 시 메인 화면으로 리다이렉트
        }catch (IllegalStateException e){
            message.addFlashAttribute("errorMessage",e.getMessage());
            return "redirect:/user/add"; // 가입 실패 시 현재 페이지로 리다이렉트
        }
    }

    // login
    @GetMapping("/user/login")
    public String loginForm(){
        return "users/login";
    }

    @PostMapping("/user/login")
    public String login(@RequestParam("email") String email,
                        @RequestParam("password") String password,
                        HttpServletRequest request,
                        RedirectAttributes redirectAttributes) {
        Optional<User> loginResult = userService.login(email, password);

        if (loginResult.isPresent()) {
            HttpSession session = request.getSession();
            session.setAttribute("loginUser", loginResult.get());
            return "redirect:/";
        }else {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "아이디 또는 비밀번호가 맞지 않습니다.");
            return "redirect:/user/login"; // 경로 수정: /users/login -> /user/login
        }
    }

    @GetMapping("/user/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession();
        if(session != null){
            session.invalidate();
        }
        return "redirect:/";
    }

    @GetMapping("/user/list")
    public String userList(Model model){
        System.out.println("UserController userList");
        model.addAttribute("users",userService.findAll());
        return "users/userList";
    }

    @GetMapping("/user/edit/{email}") // 경로 변수 수정: {id} -> {email}
    public String userEditForm(@PathVariable String email, Model model){
        User user = (User) userService.findByEmail(email).orElseThrow(
                ()->new IllegalArgumentException("Invalid user email ")); // 오타 수정: Invaild -> Invalid
        model.addAttribute("user",user);
        System.out.println("user : "+user);
        return "users/editUser";
    }

    @PostMapping("/user/edit")
    public String userEdit(@ModelAttribute User user){
        userService.update(user);
        return "redirect:/user/list";
    }

    @PostMapping("/user/delete")
    public String deleteUser(@RequestParam String email) {
        userService.deleteByEmail(email);
        return "redirect:/user/list";
    }
}
