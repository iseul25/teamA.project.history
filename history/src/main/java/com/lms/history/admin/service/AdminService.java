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
}