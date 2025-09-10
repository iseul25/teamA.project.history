package com.lms.history.users.service;

import com.lms.history.users.entity.User;
import com.lms.history.users.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 회원가입
     */
    public String join(User user) {
        // 중복 회원 검증
        validateDuplicateUser(user);

        // userType을 'USER'로 설정
        user.setUserType("일반유저");

        userRepository.save(user);
        return user.getEmail();
    }

    private void validateDuplicateUser(User user) {
        userRepository.findByEmail(user.getEmail())
                .ifPresent(m -> {
                    throw new IllegalStateException("이미 존재하는 회원입니다.");
                });
    }

    /**
     * 로그인
     */
    public Optional<User> login(String email, String password) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent() && userOptional.get().getPassword().equals(password)) {
            User user = userOptional.get();
            // userType이 null이거나 비어있을 경우 세션 객체에 'USER'로 설정
            if (user.getUserType() == null || user.getUserType().isEmpty()) {
                user.setUserType("USER");
            }
            return Optional.of(user);
        }
        return Optional.empty();
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public void update(User user) {
        userRepository.update(user);
    }

    public void deleteByEmail(String email) {
        userRepository.deleteByEmail(email);
    }

    /**
     * 모든 사용자의 목록을 조회합니다.
     *
     * @return 모든 사용자의 리스트
     */
    public List<User> findAllUsers() {
        return userRepository.findAllUsers();
    }

    public int countAllUsers() {
        return userRepository.countAllUsers();
    }

    /**
     * 페이징 처리된 회원 목록을 반환
     */
    public List<User> findUsersByPage(int page, int size) {
        return userRepository.findUsersByPage(page, size);
    }
}
