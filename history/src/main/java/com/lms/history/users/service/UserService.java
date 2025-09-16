package com.lms.history.users.service;

import com.lms.history.users.entity.User;
import com.lms.history.users.entity.Attend;
import com.lms.history.users.repository.UserAttendRepository;
import com.lms.history.users.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final UserAttendRepository userAttendRepository;

    public UserService(UserRepository userRepository, UserAttendRepository userAttendRepository) {
        this.userRepository = userRepository;
        this.userAttendRepository = userAttendRepository;
    }

    // ---------------- 회원가입 ----------------
    public User join(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        // 기본 userType 설정 (일반 사용자)
        if (user.getUserType() == null || user.getUserType().isEmpty()) {
            user.setUserType("일반유저"); // U = 일반 사용자, A = 관리자
        }

        // 기본 포인트 설정 (필요시 - int 타입이므로 기본값 0으로 이미 초기화됨)

        return userRepository.save(user);
    }

    // ---------------- 로그인 ----------------
    public Optional<User> login(String email, String password) {
        return userRepository.findByEmailAndPassword(email, password);
    }

    // ---------------- 회원 정보 조회 ----------------
    public Optional<User> findById(int userId) {
        return userRepository.findById(userId);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // ---------------- 마이페이지용 출석 상태 조회 ----------------
    public String getAttendanceStatus(int userId) {
        boolean attendedToday = userAttendRepository.existsByUserIdAndDate(userId, LocalDate.now());
        return attendedToday ? "출석" : "미출석";
    }

    // ---------------- 회원 정보 수정 ----------------
    public User updateUser(String currentEmail, User updatedUser) {
        User user = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        if (!currentEmail.equals(updatedUser.getEmail())) {
            if (userRepository.existsByEmail(updatedUser.getEmail())) {
                throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
            }
        }

        user.setName(updatedUser.getName());
        user.setEmail(updatedUser.getEmail());

        return userRepository.save(user);
    }

    // ---------------- 회원 탈퇴 ----------------
    public void deleteUser(int userId) {
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }
        userRepository.deleteById(userId);
    }

    // ---------------- 이메일 존재 여부 확인 ----------------
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean isEmailExists(String email) {
        return existsByEmail(email);
    }

    // ---------------- 포인트 / 출석 ----------------
    public Integer getTotalPointByUserId(int userId) {
        return userRepository.getTotalPointByUserId(userId);
    }

    public Integer getAttendanceCountByUserId(int userId) {
        return userAttendRepository.getAttendanceCountByUserId(userId);
    }

    /**
     * 출석 처리
     * @param user 로그인한 유저
     * @return 갱신된 유저 정보
     */
    public User markAttendance(User user) {
        if (userAttendRepository.existsByUserIdAndDate(user.getUserId(), LocalDate.now())) {
            throw new IllegalArgumentException("오늘은 이미 출석하셨습니다.");
        }

        // 🚩 수정: Attend 객체 생성 방식 변경 (테이블 스키마에 맞춤)
        Attend attend = new Attend(user.getUserId(), 10);
        userAttendRepository.save(attend);

        // 포인트 갱신
        user.setPoint(user.getPoint() + 10);
        userRepository.save(user);

        return user;
    }

    /**
     * 특정 유저의 출석 기록 전체 조회
     * @param userId 유저 ID
     * @return 출석한 날짜 목록
     */
    public List<LocalDate> getAttendedDates(int userId) {
        return userAttendRepository.findByUserId(userId).stream()
                .map(attend -> attend.getAttendanceDate().toLocalDate())
                .collect(Collectors.toList());
    }
}