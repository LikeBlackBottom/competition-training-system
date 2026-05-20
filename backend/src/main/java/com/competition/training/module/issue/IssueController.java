package com.competition.training.module.issue;

import com.competition.training.common.ApiResponse;
import com.competition.training.common.PageResponse;
import com.competition.training.module.timelog.service.TrainingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/issues")
@RequiredArgsConstructor
public class IssueController {
    private final TrainingService service;

    @GetMapping
    public ApiResponse<PageResponse<Map<String, Object>>> list(@RequestParam Map<String, Object> params) {
        return ApiResponse.success(service.issues(params));
    }

    @PostMapping
    public ApiResponse<Map<String, Object>> create(@RequestBody Map<String, Object> data) {
        return ApiResponse.success(service.createIssue(data));
    }

    @PutMapping("/{id}")
    public ApiResponse<Map<String, Object>> update(@PathVariable Long id, @RequestBody Map<String, Object> data) {
        return ApiResponse.success(service.updateIssue(id, data));
    }
}
