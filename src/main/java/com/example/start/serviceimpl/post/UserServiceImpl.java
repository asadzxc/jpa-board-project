package com.example.start.serviceimpl.post;

import com.example.start.entity.post.User;
import com.example.start.repository.post.UserRepository;
import com.example.start.service.post.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User register(User user) {
        userRepository.findByUsername(user.getUsername())
                .ifPresent(u -> {
                    throw new IllegalStateException("이미 존재하는 사용자명입니다.");
                });

        validatePassword(user.getPassword());

        // 비밀번호 암호화
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    private void validatePassword(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("비밀번호를 입력해주세요.");
        }

        if (password.length() < 8) {
            throw new IllegalArgumentException("비밀번호는 8자 이상이어야 합니다.");
        }

        if (!password.matches(".*[A-Za-z].*")) {
            throw new IllegalArgumentException("비밀번호에는 영문이 1개 이상 포함되어야 합니다.");
        }

        if (!password.matches(".*\\d.*")) {
            throw new IllegalArgumentException("비밀번호에는 숫자가 1개 이상 포함되어야 합니다.");
        }
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public long count() {
        return userRepository.count();
    }
}