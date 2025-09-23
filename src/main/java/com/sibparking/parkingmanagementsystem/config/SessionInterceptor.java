package com.sibparking.parkingmanagementsystem.config;

import com.sibparking.parkingmanagementsystem.service.UserLoginService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class SessionInterceptor implements HandlerInterceptor {

    @Autowired
    private UserLoginService userLoginService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Skip login and logout endpoints
        String path = request.getRequestURI();
        if (path.startsWith("/auth/login") || path.startsWith("/auth/logout")) {
            return true;
        }

        // Get token from Authorization header
        String token = request.getHeader("Authorization");
        if (token == null || !userLoginService.validateSession(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Session expired or invalid. Please login again.");
            return false;
        }

        // Session is valid → continue to controller
        return true;
    }
}