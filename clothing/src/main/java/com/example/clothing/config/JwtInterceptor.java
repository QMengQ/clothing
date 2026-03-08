package com.example.clothing.config;

import com.example.clothing.entity.User;
import com.example.clothing.repository.UserRepository;
import com.example.clothing.util.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        // 放行预检请求
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // 放行登录和注册接口
        String requestURI = request.getRequestURI();
        System.out.println("Request URI: " + requestURI);
        if (requestURI.equals("/user/login") || requestURI.equals("/user/register")) {
            return true;
        }

        String token = request.getHeader("Authorization");

        if (token == null || token.isEmpty()) {
            response.setStatus(401);
            return false;
        }

        // 处理带有Bearer前缀的token
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        try {
            String username = jwtUtils.parseToken(token);
            User user = userRepository.findByUsername(username);

            if (user == null) {
                response.setStatus(401);
                return false;
            }

            request.setAttribute("userId", user.getId());
            request.setAttribute("role", user.getRole());

        } catch (Exception e) {
            response.setStatus(401);
            return false;
        }

        return true;
    }
}