package com.example.drugmanagement.controller;

import com.example.drugmanagement.entity.User;
import com.example.drugmanagement.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;

@Controller
public class AuthController {

    private final UserRepository userRepository;

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/login")
    public String loginForm() {
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {
        User user = userRepository.findByUsername(username);
        if (user == null || !user.getPassword().equals(password) || user.getStatus() == null || user.getStatus() != 1) {
            model.addAttribute("error", "用户名或密码错误，或账号被禁用");
            return "auth/login";
        }
        session.setAttribute("currentUser", user);
        return "redirect:/dashboard";
    }

    @GetMapping("/register")
    public String registerForm() {
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String username,
                           @RequestParam String password,
                           Model model) {
        User existing = userRepository.findByUsername(username);
        if (existing != null) {
            model.addAttribute("error", "用户名已存在");
            return "auth/register";
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setRole("admin");
        user.setStatus(1);
        user.setCreateTime(LocalDateTime.now());
        userRepository.save(user);
        model.addAttribute("msg", "注册成功，请登录");
        return "auth/login";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
