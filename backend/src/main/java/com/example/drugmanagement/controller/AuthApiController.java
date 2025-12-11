package com.example.drugmanagement.controller;

import com.example.drugmanagement.entity.User;
import com.example.drugmanagement.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthApiController {

    private final UserRepository userRepository;

    public AuthApiController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody RegisterRequest request) {
        Map<String, Object> body = new HashMap<>();
        if (request == null || request.getUsername() == null || request.getUsername().trim().isEmpty()
                || request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            body.put("message", "用户名和密码不能为空");
            return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
        }
        String username = request.getUsername().trim();
        User existing = userRepository.findByUsername(username);
        if (existing != null) {
            body.put("message", "用户名已存在");
            return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(request.getPassword());
        user.setRole("admin");
        user.setStatus(1);
        user.setCreateTime(LocalDateTime.now());
        userRepository.save(user);
        body.put("message", "注册成功");
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest request) {
        Map<String, Object> body = new HashMap<>();
        if (request == null || request.getUsername() == null || request.getPassword() == null) {
            body.put("message", "用户名和密码不能为空");
            return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
        }
        String username = request.getUsername().trim();
        User user = userRepository.findByUsername(username);
        if (user == null || !user.getPassword().equals(request.getPassword()) || user.getStatus() == null || user.getStatus() != 1) {
            body.put("message", "用户名或密码错误，或账号被禁用");
            return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
        }
        body.put("token", "dummy-token");
        body.put("username", user.getUsername());
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    public static class RegisterRequest {
        private String username;
        private String password;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    public static class LoginRequest {
        private String username;
        private String password;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
