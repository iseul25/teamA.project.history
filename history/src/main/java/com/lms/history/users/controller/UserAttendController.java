package com.lms.history.users.controller;// UserAttendController.java

import com.lms.history.users.entity.User;
import com.lms.history.users.service.PointsService;
import com.lms.history.users.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

@Controller
public class UserAttendController {

    private final UserService userService;
    private final PointsService pointsService; // 포인트 서비스 추가

    // 기본 출석 포인트 (설정값으로 변경 가능)
    private static final int DEFAULT_ATTENDANCE_POINTS = 10;

    public UserAttendController(UserService userService, PointsService pointsService) {
        this.userService = userService;
        this.pointsService = pointsService;
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

        // 사용자의 현재 총 포인트 조회 추가
        int totalPoints = pointsService.getTotalPoints(loginUser.getUserId());

        model.addAttribute("loginUser", loginUser);
        model.addAttribute("attendedToday", attendedToday);
        model.addAttribute("year", displayYear);
        model.addAttribute("month", displayMonth);
        model.addAttribute("yearMonth", formattedYearMonth);
        model.addAttribute("calendarDays", calendarDays);
        model.addAttribute("totalPoints", totalPoints); // 총 포인트 추가

        return "attend";
    }

    @PostMapping("/api/attend/check")
    @ResponseBody
    @Transactional
    public ResponseEntity<Map<String, Object>> checkAttendance(@SessionAttribute(name = "loginUser", required = false) User loginUser) {
        Map<String, Object> response = new HashMap<>();

        if (loginUser == null) {
            response.put("success", false);
            response.put("message", "로그인이 필요합니다.");
            return ResponseEntity.status(401).body(response);
        }

        try {
            Integer attendanceId = userService.markAttendanceWithReturn(loginUser, DEFAULT_ATTENDANCE_POINTS);
            int newTotalPoints = pointsService.addAttendancePoint(
                    loginUser.getUserId(),
                    attendanceId,
                    DEFAULT_ATTENDANCE_POINTS
            );

            response.put("success", true);
            response.put("message", String.format("출석 완료! %d 포인트가 적립되었습니다.", DEFAULT_ATTENDANCE_POINTS));
            response.put("pointsEarned", DEFAULT_ATTENDANCE_POINTS);
            response.put("totalPoints", newTotalPoints);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            // 이미 롤백되므로 메시지만 반환
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(400).body(response);
        } catch (Exception e) {
            // 내부에서 어떤 예외가 발생하든,
            // 사용자에게는 일반적인 오류 메시지를 전달
            response.put("success", false);
            response.put("message", "출석 체크 중 오류가 발생했습니다.");
            // 여기서 로그를 남기는 것이 중요합니다.
            // log.error("출석 체크 중 예외 발생", e);

            // 예외를 다시 던져서 트랜잭션 롤백이 자연스럽게 일어나도록 함
            throw new RuntimeException("Transaction rollback due to internal error", e);
        }
    }

    // 포인트 조회 API 추가
    @GetMapping("/api/attend/points")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getCurrentPoints(@SessionAttribute(name = "loginUser", required = false) User loginUser) {
        Map<String, Object> response = new HashMap<>();

        if (loginUser == null) {
            response.put("success", false);
            response.put("message", "로그인이 필요합니다.");
            return ResponseEntity.status(401).body(response);
        }

        try {
            int totalPoints = pointsService.getTotalPoints(loginUser.getUserId());
            response.put("success", true);
            response.put("totalPoints", totalPoints);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "포인트 조회 중 오류가 발생했습니다.");
            return ResponseEntity.status(500).body(response);
        }
    }

    // 출석 포인트 설정 변경을 위한 메서드 (관리자용)
    @PostMapping("/api/attend/points/config")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateAttendancePoints(
            @SessionAttribute(name = "loginUser", required = false) User loginUser,
            @RequestParam int points) {

        Map<String, Object> response = new HashMap<>();

        if (loginUser == null || !"admin".equals(loginUser.getUserType())) {
            response.put("success", false);
            response.put("message", "관리자 권한이 필요합니다.");
            return ResponseEntity.status(403).body(response);
        }

        // 여기서 포인트 설정을 DB에 저장하거나 설정 파일에 저장할 수 있습니다.
        // 현재는 상수로 되어 있지만, 추후 동적 설정으로 변경 가능
        response.put("success", true);
        response.put("message", "출석 포인트가 " + points + "로 설정되었습니다.");

        return ResponseEntity.ok(response);
    }

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