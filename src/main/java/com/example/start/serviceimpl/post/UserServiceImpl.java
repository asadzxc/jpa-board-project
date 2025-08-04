package com.example.start.serviceimpl.post;

import com.example.start.entity.post.User;
import com.example.start.repository.post.UserRepository;
import com.example.start.service.post.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public User register(User user) {
        userRepository.findByUsername(user.getUsername())
                .ifPresent(u -> {
                    throw new IllegalStateException("이미 존재하는 사용자명입니다.");
                });
        return userRepository.save(user);
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


    // 관리자 페이지용 메서드
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