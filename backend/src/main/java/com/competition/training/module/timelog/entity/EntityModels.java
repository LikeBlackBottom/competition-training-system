package com.competition.training.module.timelog.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class EntityModels {
    public record AdminUser(Long id, String username, String passwordHash, String name, String role, Integer status, LocalDateTime createdAt, LocalDateTime updatedAt) {}
    public record Team(Long id, String institutionName, String teamName, String trackName, String loginCode, Integer status, LocalDateTime createdAt, LocalDateTime updatedAt) {}
    public record Member(Long id, Long teamId, String name, String role, Integer sortOrder, Integer status, LocalDateTime createdAt, LocalDateTime updatedAt) {}
    public record SkillCategory(Long id, String trackName, String name, Integer sortOrder, Integer status, LocalDateTime createdAt, LocalDateTime updatedAt) {}
    public record SkillTask(Long id, Long categoryId, String name, String description, Integer expectedMinutes, Integer difficultyLevel, java.math.BigDecimal scoreWeight, Integer sortOrder, Integer status, LocalDateTime createdAt, LocalDateTime updatedAt) {}
    public record TimeLog(Long id, Long dailyReportId, Long teamId, Long memberId, Long taskId, LocalDate recordDate, Integer durationMinutes, String progressStatus, String resultDesc, String problemDesc, Boolean needSupport, Integer scoreSelf, Boolean isVoided, String voidReason, LocalDateTime createdAt, LocalDateTime updatedAt) {}
    public record Issue(Long id, Long teamId, Long taskId, Long memberId, String title, String description, String severity, String status, String solution, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime resolvedAt) {}
}
