package com.competition.training.module.skill;

import com.competition.training.common.ApiResponse;
import com.competition.training.module.timelog.service.TrainingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/skills")
@RequiredArgsConstructor
public class SkillController {
    private final TrainingService service;

    @GetMapping("/categories")
    public ApiResponse<List<Map<String, Object>>> categories(@RequestParam Map<String, Object> params) {
        return ApiResponse.success(service.categories(params));
    }

    @PostMapping("/categories")
    public ApiResponse<Map<String, Object>> createCategory(@RequestBody Map<String, Object> data) {
        return ApiResponse.success(service.createCategory(data));
    }

    @PutMapping("/categories/{id}")
    public ApiResponse<Map<String, Object>> updateCategory(@PathVariable Long id, @RequestBody Map<String, Object> data) {
        return ApiResponse.success(service.updateCategory(id, data));
    }

    @GetMapping("/tasks")
    public ApiResponse<List<Map<String, Object>>> tasks(@RequestParam Map<String, Object> params) {
        return ApiResponse.success(service.tasks(params));
    }

    @PostMapping("/tasks")
    public ApiResponse<Map<String, Object>> createTask(@RequestBody Map<String, Object> data) {
        return ApiResponse.success(service.createTask(data));
    }

    @PutMapping("/tasks/{id}")
    public ApiResponse<Map<String, Object>> updateTask(@PathVariable Long id, @RequestBody Map<String, Object> data) {
        return ApiResponse.success(service.updateTask(id, data));
    }
}
