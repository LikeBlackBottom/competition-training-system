package com.competition.training.module.team;

import com.competition.training.common.ApiResponse;
import com.competition.training.module.timelog.service.TrainingService;
import com.competition.training.security.LoginUser;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/team")
@RequiredArgsConstructor
public class TeamController {
    private final TrainingService service;

    @GetMapping("/form-options")
    public ApiResponse<Map<String, Object>> formOptions(Authentication authentication) {
        LoginUser user = (LoginUser) authentication.getPrincipal();
        return ApiResponse.success(service.formOptions(user.getTeamId()));
    }

    @PostMapping("/time-logs")
    public ApiResponse<Map<String, Object>> submit(@Valid @RequestBody TeamSubmitRequest request, Authentication authentication) {
        LoginUser user = (LoginUser) authentication.getPrincipal();
        return ApiResponse.success("展示时长记录已提交", service.submitTeamTimeLog(user.getTeamId(), request.toMap()));
    }

    @Data
    public static class TeamSubmitRequest {
        @NotNull(message = "队员不能为空")
        public Long memberId;
        public Long taskId;
        public Long skillId;
        public LocalDate recordDate;
        @NotNull(message = "展示时长不能为空")
        @Min(value = 1, message = "展示时长最少 1 分钟")
        @Max(value = 180, message = "展示时长最多 180 分钟")
        public Integer durationMinutes;
        public String progressStatus;
        public String completionStatus;
        public String resultDesc;
        public String todayOutput;
        public String problemDesc;
        public String issue;
        @NotNull(message = "是否需要协助不能为空")
        public Boolean needSupport;
        public Boolean needHelp;

        Map<String, Object> toMap() {
            Map<String, Object> m = new HashMap<>();
            m.put("memberId", memberId);
            m.put("taskId", taskId);
            m.put("skillId", skillId);
            m.put("recordDate", recordDate == null ? null : recordDate.toString());
            m.put("durationMinutes", durationMinutes);
            m.put("progressStatus", progressStatus);
            m.put("completionStatus", completionStatus);
            m.put("resultDesc", resultDesc);
            m.put("todayOutput", todayOutput);
            m.put("problemDesc", problemDesc);
            m.put("issue", issue);
            m.put("needSupport", needSupport);
            m.put("needHelp", needHelp);
            return m;
        }
    }
}
