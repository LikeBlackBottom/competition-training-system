package com.competition.training.module.timelog.service;

import com.competition.training.common.PageResponse;

import java.util.List;
import java.util.Map;

public interface TrainingService {
    Map<String, Object> adminLogin(String username, String password);
    Map<String, Object> teamLogin(String loginCode);
    Map<String, Object> formOptions(Long teamId);
    Map<String, Object> submitTeamTimeLog(Long teamId, Map<String, Object> data);
    PageResponse<Map<String, Object>> teams(Map<String, Object> params);
    Map<String, Object> createTeam(Map<String, Object> data);
    Map<String, Object> updateTeam(Long id, Map<String, Object> data);
    PageResponse<Map<String, Object>> members(Map<String, Object> params);
    Map<String, Object> createMember(Map<String, Object> data);
    Map<String, Object> updateMember(Long id, Map<String, Object> data);
    List<Map<String, Object>> categories(Map<String, Object> params);
    Map<String, Object> createCategory(Map<String, Object> data);
    Map<String, Object> updateCategory(Long id, Map<String, Object> data);
    List<Map<String, Object>> tasks(Map<String, Object> params);
    Map<String, Object> createTask(Map<String, Object> data);
    Map<String, Object> updateTask(Long id, Map<String, Object> data);
    PageResponse<Map<String, Object>> timeLogs(Map<String, Object> params);
    Map<String, Object> voidTimeLog(Long id, String reason);
    PageResponse<Map<String, Object>> issues(Map<String, Object> params);
    Map<String, Object> createIssue(Map<String, Object> data);
    Map<String, Object> updateIssue(Long id, Map<String, Object> data);
    Map<String, Object> dashboard();
    List<Map<String, Object>> exportTimeLogs(Map<String, Object> params);
}
