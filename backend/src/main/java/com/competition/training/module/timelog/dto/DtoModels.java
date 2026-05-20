package com.competition.training.module.timelog.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class DtoModels {
    public record AdminLoginDTO(@NotBlank String username, @NotBlank String password) {}
    public record CodeLoginDTO(@NotBlank String loginCode) {}
    public record TeamTimeLogCreateDTO(@NotNull Long memberId, Long taskId, Long skillId, LocalDate recordDate,
                                       @NotNull @Min(1) @Max(180) Integer durationMinutes,
                                       String progressStatus, String completionStatus, String resultDesc,
                                       String todayOutput, String problemDesc, String issue,
                                       @NotNull Boolean needSupport, Boolean needHelp) {}
    public record VoidTimeLogDTO(String voidReason) {}
}
