package com.lms.history;

import com.lms.history.users.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/")
    public String home(HttpServletRequest request, Model model) {
        // 세션이 존재하지 않으면 null을 반환하도록 설정 (새로운 세션 생성 방지)
        HttpSession session = request.getSession(false);

        // 세션이 존재하고, 'loginUser' 속성이 있다면
        if (session != null && session.getAttribute("loginUser") != null) {
            // 세션에서 사용자 정보(User 객체)를 가져옴
            User loginUser = (User) session.getAttribute("loginUser");

            // 가져온 사용자 정보를 'loginUser'라는 이름으로 모델에 추가하여 뷰에 전달
            model.addAttribute("loginUser", loginUser);
        }

        return "home";
    }

    @GetMapping("/faq")
    public String showFaqPage() {
        // "faq"라는 문자열을 반환하여 Spring이 templates/faq.html 파일을 찾도록 지시합니다.
        return "faq";
    }
}