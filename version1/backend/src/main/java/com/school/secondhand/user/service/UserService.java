package com.school.secondhand.user.service;

import com.school.secondhand.user.dto.*;
import com.school.secondhand.user.entity.User;

import java.util.List;

public interface UserService {

    /**
     * 用户注册
     */
    UserDTO register(RegisterRequestDTO request);

    /**
     * 用户登录
     */
    LoginResponseDTO login(LoginRequestDTO request);

    /**
     * 根据ID获取用户信息
     */
    UserDTO getUserById(Long id);

    /**
     * 更新用户信息
     */
    UserDTO updateUser(Long id, UpdateRequestDTO updateRequestDTO);

    /**
     * 获取所有用户列表
     */
    List<UserDTO> getAllUsers();

    /**
     * 禁用/启用用户
     */
    void updateUserStatus(Long id, Integer status);

    /**
     * 删除用户
     */
    void deleteUser(Long id);

    /**
     * 根据用户名获取用户
     */
    User getUserByUsername(String username);

}