package com.competition.training.module.timelog;

import com.competition.training.common.ApiResponse;
import com.competition.training.common.PageResponse;
import com.competition.training.module.timelog.service.TrainingService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/time-logs")
@RequiredArgsConstructor
public class TimeLogController {
    private final TrainingService service;

    @GetMapping
    public ApiResponse<PageResponse<Map<String, Object>>> list(@RequestParam Map<String, Object> params) {
        return ApiResponse.success(service.timeLogs(params));
    }

    @PostMapping("/{id}/void")
    public ApiResponse<Map<String, Object>> voidLog(@PathVariable Long id, @RequestBody(required = false) VoidRequest request) {
        String reason = request == null ? "管理员作废" : request.getVoidReason();
        return ApiResponse.success(service.voidTimeLog(id, reason == null || reason.isBlank() ? "管理员作废" : reason));
    }

    @Data
    public static class VoidRequest {
        private String voidReason;
        public String getVoidReason() { return voidReason; }
        public void setVoidReason(String voidReason) { this.voidReason = voidReason; }
    }
}
