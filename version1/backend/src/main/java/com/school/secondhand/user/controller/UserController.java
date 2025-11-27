package com.school.secondhand.user.controller;

import com.school.secondhand.user.dto.*;
import com.school.secondhand.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@RequestBody RegisterRequestDTO request) {
        UserDTO userDTO = userService.register(request);
        return ResponseEntity.ok(userDTO);
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO request) {
        LoginResponseDTO response = userService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * 获取用户信息
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        UserDTO userDTO = userService.getUserById(id);
        return ResponseEntity.ok(userDTO);
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody UpdateRequestDTO updateRequestDTO) {
        UserDTO updatedUserDTO = userService.updateUser(id, updateRequestDTO);
        return ResponseEntity.ok(updatedUserDTO);
    }

    /**
     * 获取所有用户列表（管理员用）
     */
    @GetMapping("/list")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> userDTOs = userService.getAllUsers();
        return ResponseEntity.ok(userDTOs);
    }

    /**
     * 更新用户状态
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<Void> updateUserStatus(@PathVariable Long id, @RequestParam Integer status) {
        userService.updateUserStatus(id, status);
        return ResponseEntity.ok().build();
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

}