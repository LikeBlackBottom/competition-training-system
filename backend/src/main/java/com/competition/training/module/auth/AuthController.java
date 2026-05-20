package com.competition.training.module.auth;

import com.competition.training.common.ApiResponse;
import com.competition.training.module.timelog.service.TrainingService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final TrainingService service;

    @PostMapping("/api/admin/auth/login")
    public ApiResponse<Map<String, Object>> adminLogin(@Valid @RequestBody AdminLoginRequest request) {
        return ApiResponse.success(service.adminLogin(request.getUsername(), request.getPassword()));
    }

    @PostMapping("/api/team/auth/code-login")
    public ApiResponse<Map<String, Object>> teamLogin(@Valid @RequestBody CodeLoginRequest request) {
        return ApiResponse.success(service.teamLogin(request.getLoginCode()));
    }

    @Data
    public static class AdminLoginRequest {
        @NotBlank(message = "用户名不能为空")
        private String username;
        @NotBlank(message = "密码不能为空")
        private String password;
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    @Data
    public static class CodeLoginRequest {
        @NotBlank(message = "邀请码不能为空")
        private String loginCode;
        public String getLoginCode() { return loginCode; }
        public void setLoginCode(String loginCode) { this.loginCode = loginCode; }
    }
}
