package com.example.start.interceptor;

import com.example.start.entity.post.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

public class LoginCheckInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        HttpSession session = request.getSession(false);

        User loginUser = null;
        if (session != null) {
            loginUser = (User) session.getAttribute("loginUser");
        }

        // 로그인 안 되어 있으면 로그인 페이지로 이동
        if (loginUser == null) {
            response.sendRedirect("/login");
            return false;
        }

        return true;
    }
}