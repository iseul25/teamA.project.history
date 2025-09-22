package com.lms.history.admin.service;

import com.lms.history.admin.repository.AdminRepository;
import com.lms.history.users.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.scheduling.annotation.Scheduled; // 💡 스케줄링을 위한 import 추가

import java.util.List;
import java.util.Optional;

@Service
public class AdminService {

    private final AdminRepository adminRepository;

    public AdminService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    public void save(User user) {
        adminRepository.save(user);
    }

    public boolean isEmailDuplicated(String email) {
        return adminRepository.existsByEmail(email);
    }

    public void registerUser(User user) {
        adminRepository.save(user);
    }

    public void deleteByEmail(String email) {
        adminRepository.deleteByEmail(email);
    }

    public Optional<User> findByEmail(String email) {
        return adminRepository.findByEmail(email);
    }

    public List<User> findAllUsers() {
        return adminRepository.findAllUsers();
    }

    public int countAllUsers() {
        return adminRepository.countAllUsers();
    }

    public List<User> findUsersByPage(int page, int size) {
        return adminRepository.findUsersByPage(page, size);
    }

    // 💡 추가된 부분

    /**
     * 매일 자정(00:00:00)에 모든 유저의 출석 상태를 'N'으로 초기화합니다.
     */
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void resetDailyAttendance() {
        adminRepository.resetAllAttendance();
        System.out.println("일일 출석 초기화 완료: 모든 유저의 출석 상태가 'N'으로 초기화되었습니다.");
    }

    /**
     * 유저의 출석 상태를 'Y'로 업데이트하고 포인트를 부여합니다.
     * @param email 출석할 유저의 이메일
     * @param point 출석으로 받을 포인트
     */
    @Transactional
    public void markAttendance(String email, int point) {
        Optional<User> userOptional = adminRepository.findByEmail(email);
        userOptional.ifPresent(user -> {
            if ("N".equals(user.getAttend())) { // 오늘 아직 출석하지 않았을 경우에만
                user.setAttend("Y");
                user.setPoint(user.getPoint() + point);
                adminRepository.save(user);
                System.out.println(user.getEmail() + " 출석 완료. 포인트 " + point + " 적립.");
            }
        });
    }
}