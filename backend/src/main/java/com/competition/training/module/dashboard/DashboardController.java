package com.competition.training.module.dashboard;

import com.competition.training.common.ApiResponse;
import com.competition.training.module.timelog.service.TrainingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class DashboardController {
    private final TrainingService service;

    @GetMapping("/api/admin/dashboard")
    public ApiResponse<Map<String, Object>> dashboard() {
        return ApiResponse.success(service.dashboard());
    }
}
