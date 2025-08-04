package com.example.start.service.post;

import com.example.start.entity.post.User;
import java.util.List;

public interface UserService {
    User register(User user);
    User findById(Long id);
    User findByUsername(String username);

    // 관리자 페이지에서 사용할 메서드 추가
    List<User> findAll();        // 전체 회원 목록 조회용
    void deleteById(Long id);    // 회원 삭제용
    long count();                // 회원 수 카운트


}