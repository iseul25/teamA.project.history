package com.lms.history.users.service;

import com.lms.history.users.entity.User;
import com.lms.history.users.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

        // userType을 'user'로 설정
        user.setUserType("user");

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
            // userType이 null이거나 비어있을 경우 세션 객체에 'user'로 설정
            if (user.getUserType() == null || user.getUserType().isEmpty()) {
                user.setUserType("user");
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
}