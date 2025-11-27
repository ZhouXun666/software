package com.school.secondhand.user.service.impl;

import com.school.secondhand.user.dto.*;
import com.school.secondhand.user.entity.User;
import com.school.secondhand.user.repository.UserRepository;
import com.school.secondhand.user.service.UserService;
import com.school.secondhand.common.utils.JwtUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public UserDTO register(RegisterRequestDTO request) {
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        // 创建用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setName(request.getName());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setGender(0); //刚注册时性别未知 ‘0’
        user.setRole(0); // 普通用户
        user.setStatus(1); // 启用

        userRepository.save(user);

        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);
        return userDTO;
    }

    @Override
    public LoginResponseDTO login(LoginRequestDTO request) {
        // 查找用户
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("用户名或密码错误"));

        // 验证密码
        if (!user.getPassword().equals(request.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 检查用户状态
        if (user.getStatus() == 0) {
            throw new RuntimeException("用户已被禁用");
        }
        user.setUpdateTime(new Date());
        userRepository.save(user);

        // 生成token
        String token = jwtUtils.generateToken(user.getUsername(),user.getId());

        // 构建响应
        LoginResponseDTO response = new LoginResponseDTO();
        response.setToken(token);

        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);
        response.setUser(userDTO);

        return response;
    }

    @Override
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);
        return userDTO;
    }

    @Override
    public UserDTO updateUser(Long id, UpdateRequestDTO updateRequestDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 更新用户信息
        if (updateRequestDTO.getName() != null) {
            user.setName(updateRequestDTO.getName());
        }
        if(updateRequestDTO.getGender() != null) {
            user.setGender(updateRequestDTO.getGender());
        }
        if (updateRequestDTO.getPhone() != null) {
            user.setPhone(updateRequestDTO.getPhone());
        }
        if (updateRequestDTO.getEmail() != null) {
            user.setEmail(updateRequestDTO.getEmail());
        }

        userRepository.save(user);

        UserDTO updatedUserDTO = new UserDTO();
        BeanUtils.copyProperties(user, updatedUserDTO);
        return updatedUserDTO;
    }

    @Override
    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(user -> {
                    UserDTO userDTO = new UserDTO();
                    BeanUtils.copyProperties(user, userDTO);
                    return userDTO;
                })
                .collect(Collectors.toList());
    }

    @Override
    public void updateUserStatus(Long id, Integer status) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        user.setStatus(status);
        userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
    }
}