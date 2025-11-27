package com.school.secondhand.common.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        JwtUtils jwtUtils = new JwtUtils();
        // 1. 从请求头获取 Token
        String token = extractToken(request);

        if (token != null && jwtUtils.validateToken(token)) {
            // 2. 解析 Token 获取用户信息（例如 username）
            String username = jwtUtils.getUsernameFromToken(token);

            // 3. 创建认证对象并存入 SecurityContext
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(username, null, null);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 4. 放行请求
        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}