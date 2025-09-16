package com.lms.history.users.controller;// UserAttendController.java

import com.lms.history.users.entity.User;
import com.lms.history.users.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

@Controller
public class UserAttendController {

    private final UserService userService;

    public UserAttendController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/attend")
    public String showAttendPage(@SessionAttribute(name = "loginUser", required = false) User loginUser,
                                 Model model,
                                 @RequestParam(required = false) Integer year,
                                 @RequestParam(required = false) Integer month) {
        if (loginUser == null) {
            return "redirect:/login";
        }

        LocalDate today = LocalDate.now();
        LocalDate viewDate;

        // 명시적으로 기본값 설정
        if (year != null && month != null) {
            viewDate = LocalDate.of(year, month, 1);
        } else {
            viewDate = today;
        }

        // 확실하게 값 설정
        int displayYear = viewDate.getYear();
        int displayMonth = viewDate.getMonthValue();
        String formattedYearMonth = String.format("%d-%02d", displayYear, displayMonth);

        List<LocalDate> attendedDates = userService.getAttendedDates(loginUser.getUserId());
        boolean attendedToday = attendedDates != null ? attendedDates.contains(today) : false;
        List<List<Map<String, Object>>> calendarDays = generateCalendarData(viewDate, attendedDates != null ? attendedDates : new ArrayList<>());

        model.addAttribute("loginUser", loginUser);
        model.addAttribute("attendedToday", attendedToday);
        model.addAttribute("year", displayYear);        // int 값 보장
        model.addAttribute("month", displayMonth);      // int 값 보장
        model.addAttribute("yearMonth", formattedYearMonth); // 포맷된 문자열
        model.addAttribute("calendarDays", calendarDays);

        return "attend";
    }

    @PostMapping("/api/attend/check")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> checkAttendance(@SessionAttribute(name = "loginUser", required = false) User loginUser) {
        Map<String, Object> response = new HashMap<>();

        if (loginUser == null) {
            response.put("success", false);
            response.put("message", "로그인이 필요합니다.");
            return ResponseEntity.status(401).body(response);
        }

        try {
            userService.markAttendance(loginUser);
            response.put("success", true);
            response.put("message", "출석 완료!");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(400).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "출석 체크 중 오류가 발생했습니다.");
            return ResponseEntity.status(500).body(response);
        }
    }

    // 🚩 이 메소드 블록 전체를 클래스 내부에, 다른 메소드들과 같은 레벨에 위치시켜야 합니다.
    private List<List<Map<String, Object>>> generateCalendarData(LocalDate date, List<LocalDate> attendedDates) {
        LocalDate firstDayOfMonth = date.withDayOfMonth(1);
        int firstDayOfWeek = firstDayOfMonth.getDayOfWeek().getValue() % 7;
        if (firstDayOfWeek == 0) firstDayOfWeek = 7;

        LocalDate lastDayOfMonth = date.withDayOfMonth(date.lengthOfMonth());
        int totalDays = lastDayOfMonth.getDayOfMonth();

        List<List<Map<String, Object>>> calendar = new ArrayList<>();
        List<Map<String, Object>> week = new ArrayList<>();

        for (int i = 0; i < firstDayOfWeek - 1; i++) {
            week.add(Map.of("day", "", "attended", false, "isToday", false));
        }

        for (int day = 1; day <= totalDays; day++) {
            LocalDate currentDay = date.withDayOfMonth(day);
            boolean isAttended = attendedDates.contains(currentDay);
            boolean isToday = currentDay.isEqual(LocalDate.now());

            Map<String, Object> dayMap = new HashMap<>();
            dayMap.put("day", String.valueOf(day));
            dayMap.put("attended", isAttended);
            dayMap.put("isToday", isToday);

            week.add(dayMap);

            if (week.size() == 7) {
                calendar.add(week);
                week = new ArrayList<>();
            }
        }

        if (!week.isEmpty()) {
            while (week.size() < 7) {
                week.add(Map.of("day", "", "attended", false, "isToday", false));
            }
            calendar.add(week);
        }

        return calendar;
    }
}