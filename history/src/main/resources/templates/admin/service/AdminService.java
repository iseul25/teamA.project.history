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

    // 생성자 주입 방식으로 의존성 추가
    public AdminService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    /**
     * 관리자가 일반 유저를 등록하는 메소드입니다.
     * 비밀번호를 데이터베이스에 저장하기 전에 안전하게 인코딩합니다.
     *
     * @param user 등록할 유저 정보를 담은 User 객체
     * @return 등록된 User 객체
     */
    public void registerUser(User user) {
        // UserRepository 대신 AdminRepository를 사용하여 유저 정보를 데이터베이스에 저장
        adminRepository.save(user);
    }

    public Optional<User> findByEmail(String email) {
        return adminRepository.findByEmail(email);
    }

    /**
     * 모든 사용자의 목록을 조회합니다.
     *
     * @return 모든 사용자의 리스트
     */
    public List<User> findAllUsers() {
        return adminRepository.findAllUsers();
    }

    public int countAllUsers() {
        return adminRepository.countAllUsers();
    }

    /**
     * 페이징 처리된 회원 목록을 반환
     */
    public List<User> findUsersByPage(int page, int size) {
        return adminRepository.findUsersByPage(page, size);
    }
}
