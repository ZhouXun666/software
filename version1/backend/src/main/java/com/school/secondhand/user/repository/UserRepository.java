package com.school.secondhand.user.repository;

import com.school.secondhand.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 根据用户名查询用户
     */
    Optional<User> findByUsername(String username);

    /**
     * 根据用户名判断用户是否存在
     */
    boolean existsByUsername(String username);

    /**
     * 根据手机号查询用户
     */
    Optional<User> findByPhone(String phone);

    /**
     * 根据邮箱查询用户
     */
    Optional<User> findByEmail(String email);

}