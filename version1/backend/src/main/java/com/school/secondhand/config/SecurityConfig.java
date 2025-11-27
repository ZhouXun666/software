package com.school.secondhand.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;

import com.school.secondhand.common.utils.JwtAuthenticationFilter;
@Configuration  // 必须添加此注解！
@EnableWebSecurity
public class SecurityConfig{
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList("*"));  // 允许所有来源（生产环境应限制）
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
    // 在 SecurityConfig 中启用 CORS
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))  // 自定义 CORS
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // 1. 允许 POST 请求访问 /api/user/login
                        .requestMatchers(new AntPathRequestMatcher("/api/users/register", "POST")).permitAll()
                        // 2. 允许所有方法访问 /register
                        .requestMatchers(new AntPathRequestMatcher("/api/users/login")).permitAll()
                        .anyRequest().authenticated())
                // 添加 JWT 过滤器
                .addFilterBefore(new JwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
                ;
        //调试信息
        http
                .exceptionHandling(exception -> exception
                .authenticationEntryPoint((request, response, ex) -> {
                    System.out.println("拒绝访问: " + request.getRequestURI()); // 打印被拦截的路径
                    response.sendError(403, "Forbidden");
                })
        );
        return http.build();
    }
}