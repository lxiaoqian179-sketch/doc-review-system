package com.example.docreview.service;

import com.example.docreview.dto.LoginRequest;
import com.example.docreview.dto.LoginResponse;
import com.example.docreview.dto.RegisterRequest;
import com.example.docreview.entity.User;
import com.example.docreview.repository.UserRepository;
import com.example.docreview.security.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("使用者不存在"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("密碼錯誤");
        }

        String token = jwtTokenProvider.generateToken(user.getUsername(), user.getRole().name());
        return new LoginResponse(token, user.getUsername(), user.getRole().name());
    }

    public LoginResponse register(RegisterRequest request) {
        // 1. 檢查帳號是否已存在
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("帳號已被使用");
        }

        // 2. 檢查 email 是否已存在
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email 已被註冊");
        }

        // 3. 建立新使用者，密碼加密，角色固定為 USER
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setRole(User.Role.USER);
        user.setCreatedAt(LocalDateTime.now());

        userRepository.save(user);

        // 4. 註冊完直接登入，回傳 token（提升體驗，不用再打一次 login）
        String token = jwtTokenProvider.generateToken(user.getUsername(), user.getRole().name());
        return new LoginResponse(token, user.getUsername(), user.getRole().name());
    }
}