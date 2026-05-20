package com.competition.training.module.timelog.vo;

import java.util.List;

public class VoModels {
    public record TeamVO(Long id, String institutionName, String schoolName, String teamName, String trackName, String track, String loginCode, String inviteCode, String status) {}
    public record MemberVO(Long id, Long teamId, String name, String role, String status) {}
    public record SkillTaskVO(Long id, Long categoryId, Long moduleId, String name) {}
    public record SkillCategoryVO(Long id, String name, String trackName, String color, List<SkillTaskVO> tasks, List<SkillTaskVO> skills) {}
    public record TimeLogVO(Long id, String recordDate, String teamName, String memberName, String categoryName, String taskName, Integer durationMinutes, Boolean needSupport, Boolean isVoided) {}
}
