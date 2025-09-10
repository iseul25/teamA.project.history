package com.lms.history.admin.service;

import com.lms.history.admin.repository.AdminRepository;
import com.lms.history.users.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AdminService {

    private final AdminRepository adminRepository;

    public AdminService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    /**
     * 유저 정보 저장 또는 업데이트 (수정된 메서드)
     */
    public void save(User user) {
        adminRepository.save(user);
    }

    /**
     * 이메일이 데이터베이스에 중복되는지 확인하는 메서드입니다.
     *
     * @param email 확인할 이메일 주소
     * @return 중복이면 true, 아니면 false
     */
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
}
