package com.competition.training.module.timelog.service.impl;

import com.competition.training.common.BusinessException;
import com.competition.training.common.PageResponse;
import com.competition.training.module.timelog.mapper.TrainingMapper;
import com.competition.training.module.timelog.service.TrainingService;
import com.competition.training.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrainingServiceImpl implements TrainingService {
    private static final String DEFAULT_TRACK = "新一代信息技术";
    private static final Set<String> VALID_TRACKS = Set.of(DEFAULT_TRACK, "人工智能");
    private static final ZoneId BUSINESS_ZONE = ZoneId.of("Asia/Shanghai");
    private static final Set<String> VALID_PROGRESS = Set.of("未开始", "进行中", "部分完成", "已完成", "已掌握", "受阻");

    private final TrainingMapper mapper;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Map<String, Object> adminLogin(String username, String password) {
        var admin = mapper.findAdmin(username).orElseThrow(() -> new BusinessException(401, "账号或密码错误"));
        if (!enabled(admin.get("status"))) throw new BusinessException(403, "管理员已停用");
        String hash = str(admin.get("password_hash"));
        if (!passwordEncoder.matches(password, hash)) {
            throw new BusinessException(401, "账号或密码错误");
        }
        Map<String, Object> adminVo = map("id", admin.get("id"), "username", admin.get("username"), "name", admin.get("name"), "role", admin.get("role"));
        return map("token", jwtUtil.createAdminToken(num(admin.get("id")), str(admin.get("username")), str(admin.get("role"))), "admin", adminVo, "user", adminVo);
    }

    @Override
    public Map<String, Object> teamLogin(String loginCode) {
        var team = mapper.findTeamByCode(loginCode.trim()).orElseThrow(() -> new BusinessException(401, "邀请码无效或已过期，请联系指导老师获取"));
        if (!enabled(team.get("status"))) throw new BusinessException(403, "队伍已停用");
        Map<String, Object> teamVo = teamVo(team);
        return map("token", jwtUtil.createTeamToken(num(team.get("id"))), "team", teamVo, "teamInfo", teamInfoVo(team));
    }

    @Override
    public Map<String, Object> formOptions(Long teamId) {
        Map<String, Object> team = mapper.findTeam(teamId).orElseThrow(() -> new BusinessException("队伍不存在"));
        String trackName = normalizeTrackName(str(team.get("track_name")), true);
        List<Map<String, Object>> members = mapper.activeMembers(teamId).stream().map(this::memberOptionVo).toList();
        List<Map<String, Object>> tasks = mapper.activeTasks(trackName).stream().map(this::taskOptionVo).toList();
        Map<Long, List<Map<String, Object>>> byCategory = tasks.stream().collect(Collectors.groupingBy(t -> num(t.get("categoryId")), LinkedHashMap::new, Collectors.toList()));
        List<Map<String, Object>> categories = mapper.activeCategories(trackName).stream().map(c -> {
            Map<String, Object> vo = categoryVo(c);
            vo.put("tasks", byCategory.getOrDefault(num(c.get("id")), List.of()));
            vo.put("skills", byCategory.getOrDefault(num(c.get("id")), List.of()));
            return vo;
        }).toList();
        return map("team", teamVo(team), "teamInfo", teamInfoVo(team), "members", members, "categories", categories, "modules", categories);
    }

    @Override
    @Transactional
    public Map<String, Object> submitTeamTimeLog(Long teamId, Map<String, Object> data) {
        Long memberId = num(first(data, "memberId"));
        Long taskId = num(first(data, "taskId", "skillId"));
        Integer minutes = integer(first(data, "durationMinutes", "hours"));
        if (memberId == null || taskId == null) throw new BusinessException("队员和技能点不能为空");
        if (minutes == null || minutes < 1 || minutes > 180) throw new BusinessException("展示时长必须是 1 到 180 分钟");
        var member = mapper.findMember(memberId).orElseThrow(() -> new BusinessException("队员不存在"));
        if (!Objects.equals(num(member.get("team_id")), teamId)) throw new BusinessException(403, "队员不属于当前队伍");
        if (!enabled(member.get("status"))) throw new BusinessException("队员已停用");
        var task = mapper.findTask(taskId).orElseThrow(() -> new BusinessException("技能点不存在"));
        if (!enabled(task.get("status"))) throw new BusinessException("技能点已停用");
        LocalDate recordDate = LocalDate.now(BUSINESS_ZONE);
        String progress = normalizeProgress(str(first(data, "progressStatus", "completionStatus")));
        Long dailyReportId = mapper.findDailyReportId(teamId, recordDate).orElse(null);
        if (dailyReportId == null) {
            dailyReportId = mapper.insertDailyReport(teamId, recordDate, minutes);
        }
        if (dailyReportId == null) throw new BusinessException("日报创建失败");
        mapper.insertTimeLog(dailyReportId, teamId, memberId, taskId, recordDate, minutes, progress,
                str(first(data, "resultDesc", "todayOutput")), str(first(data, "problemDesc", "issue")),
                bool(first(data, "needSupport", "needHelp")));
        return map("success", true);
    }

    @Override
    public PageResponse<Map<String, Object>> teams(Map<String, Object> params) {
        int page = page(params), size = size(params);
        String keyword = str(first(params, "keyword", "search"));
        Integer status = status(first(params, "status"));
        return new PageResponse<>(mapper.teams(keyword, status, LocalDate.now(BUSINESS_ZONE), size, (page - 1) * size).stream().map(this::teamAdminVo).toList(),
                mapper.teamsCount(keyword, status), page, size);
    }

    @Override
    @Transactional
    public Map<String, Object> createTeam(Map<String, Object> data) {
        normalizeTeam(data, true);
        Long id = mapper.insertTeam(data);
        return teamAdminVo(mapper.findTeam(id).orElseThrow());
    }

    @Override
    @Transactional
    public Map<String, Object> updateTeam(Long id, Map<String, Object> data) {
        normalizeTeam(data, false);
        mapper.updateTeam(id, data);
        return teamAdminVo(mapper.findTeam(id).orElseThrow(() -> new BusinessException("队伍不存在")));
    }

    @Override
    public PageResponse<Map<String, Object>> members(Map<String, Object> params) {
        int page = page(params), size = size(params);
        Long teamId = num(first(params, "teamId"));
        String keyword = str(first(params, "keyword", "search"));
        Integer status = status(first(params, "status"));
        return new PageResponse<>(mapper.members(teamId, keyword, status, size, (page - 1) * size).stream().map(this::memberAdminVo).toList(),
                mapper.membersCount(teamId, keyword, status), page, size);
    }

    @Override
    public Map<String, Object> createMember(Map<String, Object> data) {
        normalizeMember(data, true);
        Long id = mapper.insertMember(data);
        return memberAdminVo(mapper.members(num(data.get("teamId")), null, null, 1000, 0).stream().filter(m -> Objects.equals(num(m.get("id")), id)).findFirst().orElseThrow());
    }

    @Override
    public Map<String, Object> updateMember(Long id, Map<String, Object> data) {
        normalizeMember(data, false);
        mapper.updateMember(id, data);
        Long teamId = num(first(data, "teamId"));
        return memberAdminVo(mapper.members(teamId, null, null, 1000, 0).stream().filter(m -> Objects.equals(num(m.get("id")), id)).findFirst().orElseThrow(() -> new BusinessException("队员不存在")));
    }

    @Override
    public List<Map<String, Object>> categories(Map<String, Object> params) {
        return mapper.categories(status(first(params, "status"))).stream().map(this::categoryVo).toList();
    }

    @Override
    public Map<String, Object> createCategory(Map<String, Object> data) {
        normalizeCategory(data, true);
        Long id = mapper.insertCategory(data);
        return categories(Map.of()).stream().filter(c -> Objects.equals(num(c.get("id")), id)).findFirst().orElseThrow();
    }

    @Override
    public Map<String, Object> updateCategory(Long id, Map<String, Object> data) {
        normalizeCategory(data, false);
        mapper.updateCategory(id, data);
        return categories(Map.of()).stream().filter(c -> Objects.equals(num(c.get("id")), id)).findFirst().orElseThrow(() -> new BusinessException("模块不存在"));
    }

    @Override
    public List<Map<String, Object>> tasks(Map<String, Object> params) {
        return mapper.tasks(num(first(params, "categoryId", "moduleId")), status(first(params, "status"))).stream().map(this::taskVo).toList();
    }

    @Override
    public Map<String, Object> createTask(Map<String, Object> data) {
        normalizeTask(data, true);
        Long id = mapper.insertTask(data);
        return tasks(Map.of()).stream().filter(t -> Objects.equals(num(t.get("id")), id)).findFirst().orElseThrow();
    }

    @Override
    public Map<String, Object> updateTask(Long id, Map<String, Object> data) {
        normalizeTask(data, false);
        mapper.updateTask(id, data);
        return tasks(Map.of()).stream().filter(t -> Objects.equals(num(t.get("id")), id)).findFirst().orElseThrow(() -> new BusinessException("技能点不存在"));
    }

    @Override
    public PageResponse<Map<String, Object>> timeLogs(Map<String, Object> params) {
        int page = page(params), size = size(params);
        Map<String, Object> f = normalizeTimeLogFilter(params);
        return new PageResponse<>(mapper.timeLogs(f, size, (page - 1) * size).stream().map(this::timeLogVo).toList(), mapper.timeLogsCount(f), page, size);
    }

    @Override
    public Map<String, Object> voidTimeLog(Long id, String reason) {
        var row = mapper.one("select * from time_logs where id=?", id).orElseThrow(() -> new BusinessException("记录不存在"));
        if (Boolean.TRUE.equals(row.get("is_voided"))) throw new BusinessException("记录已作废，不能重复作废");
        mapper.voidTimeLog(id, reason);
        return map("success", true);
    }

    @Override
    public PageResponse<Map<String, Object>> issues(Map<String, Object> params) {
        int page = page(params), size = size(params);
        Map<String, Object> f = new HashMap<>(params);
        f.put("teamId", num(first(params, "teamId")));
        f.put("statusText", normalizeIssueStatus(str(first(params, "status"))));
        f.put("severityText", normalizeSeverity(str(first(params, "severity"))));
        return new PageResponse<>(mapper.issues(f, size, (page - 1) * size).stream().map(this::issueVo).toList(), mapper.issuesCount(f), page, size);
    }

    @Override
    public Map<String, Object> createIssue(Map<String, Object> data) {
        normalizeIssue(data);
        Long id = mapper.insertIssue(data);
        return issues(Map.of("pageSize", 1000)).getRecords().stream().filter(i -> Objects.equals(num(i.get("id")), id)).findFirst().orElseThrow();
    }

    @Override
    public Map<String, Object> updateIssue(Long id, Map<String, Object> data) {
        normalizeIssue(data);
        mapper.updateIssue(id, data);
        return issues(Map.of("pageSize", 1000)).getRecords().stream().filter(i -> Objects.equals(num(i.get("id")), id)).findFirst().orElseThrow(() -> new BusinessException("问题不存在"));
    }

    @Override
    public Map<String, Object> dashboard() {
        LocalDate today = LocalDate.now(BUSINESS_ZONE);
        Map<String, Object> summary = mapper.dashboardRows("""
                select (select count(*) from teams) total_teams,
                       (select count(distinct team_id) from time_logs where record_date=? and is_voided=false) submitted_today_teams,
                       (select count(*) from teams) - (select count(distinct team_id) from time_logs where record_date=? and is_voided=false) not_submitted_today_teams,
                       coalesce((select sum(duration_minutes) from time_logs where is_voided=false),0) total_display_minutes,
                       (select count(*) from issues where status in ('待处理','处理中')) pending_issues
                """, java.sql.Date.valueOf(today), java.sql.Date.valueOf(today)).get(0);
        List<Map<String, Object>> ranking = mapper.dashboardRows("""
                select t.team_name,t.institution_name,coalesce(sum(l.duration_minutes),0) display_minutes
                from teams t left join time_logs l on l.team_id=t.id and l.is_voided=false
                group by t.id,t.team_name,t.institution_name order by display_minutes desc limit 10
                """).stream().map(r -> map("teamName", r.get("team_name"), "institutionName", r.get("institution_name"), "name", r.get("team_name"), "displayMinutes", r.get("display_minutes"), "hours", r.get("display_minutes"))).toList();
        List<Map<String, Object>> skill = mapper.dashboardRows("""
                select st.id,st.name skill_name,coalesce(sum(l.duration_minutes),0) display_minutes
                from skill_tasks st left join time_logs l on l.task_id=st.id and l.is_voided=false
                group by st.id,st.name order by display_minutes desc
                """).stream().map(r -> map("id", r.get("id"), "skillName", r.get("skill_name"), "name", r.get("skill_name"), "displayMinutes", r.get("display_minutes"), "value", r.get("display_minutes"), "color", "#00d4ff")).toList();
        List<Map<String, Object>> trend = mapper.dashboardRows("""
                select record_date,coalesce(sum(duration_minutes),0) display_minutes,count(*) records
                from time_logs where is_voided=false and record_date>=cast(? as date) - interval '6 day'
                group by record_date order by record_date
                """, java.sql.Date.valueOf(today)).stream().map(r -> map("date", str(r.get("record_date")), "displayMinutes", r.get("display_minutes"), "hours", r.get("display_minutes"), "records", r.get("records"))).toList();
        List<Map<String, Object>> recent = mapper.timeLogs(new HashMap<>(), 6, 0).stream().map(this::timeLogVo).toList();
        Map<String, Object> s = map("totalTeams", summary.get("total_teams"), "submittedTodayTeams", summary.get("submitted_today_teams"),
                "notSubmittedTodayTeams", summary.get("not_submitted_today_teams"), "totalDisplayMinutes", summary.get("total_display_minutes"), "pendingIssues", summary.get("pending_issues"));
        return map("summary", s, "teamRanking", ranking, "skillDistribution", skill, "dailyTrend", trend, "recentLogs", recent,
                "totalTeams", s.get("totalTeams"), "todaySubmitted", s.get("submittedTodayTeams"), "todayNotSubmitted", s.get("notSubmittedTodayTeams"),
                "totalHours", s.get("totalDisplayMinutes"), "pendingIssues", s.get("pendingIssues"),
                "teamHoursRank", ranking, "skillDist", skill, "recentRecords", recent, "issueSeverityDist", List.of());
    }

    @Override
    public List<Map<String, Object>> exportTimeLogs(Map<String, Object> params) {
        return mapper.timeLogs(normalizeTimeLogFilter(params), 100000, 0).stream().map(this::timeLogVo).toList();
    }

    private Map<String, Object> teamVo(Map<String, Object> t) {
        return map("id", t.get("id"), "institutionName", t.get("institution_name"), "teamName", t.get("team_name"), "trackName", t.get("track_name"));
    }
    private Map<String, Object> teamInfoVo(Map<String, Object> t) {
        return map("id", t.get("id"), "schoolName", t.get("institution_name"), "teamName", t.get("team_name"), "track", t.get("track_name"), "trackName", t.get("track_name"));
    }
    private Map<String, Object> teamAdminVo(Map<String, Object> t) {
        return map("id", t.get("id"), "schoolName", t.get("institution_name"), "institutionName", t.get("institution_name"), "teamName", t.get("team_name"),
                "track", t.get("track_name"), "trackName", t.get("track_name"), "inviteCode", t.get("login_code"), "loginCode", t.get("login_code"),
                "status", enabled(t.get("status")) ? "active" : "inactive", "memberCount", nz(t.get("member_count")), "todaySubmitted", Boolean.TRUE.equals(t.get("today_submitted")),
                "todaySubmitStatus", Boolean.TRUE.equals(t.get("today_submitted")) ? "submitted" : "notSubmitted",
                "totalHours", nz(t.get("total_minutes")), "totalDisplayMinutes", nz(t.get("total_minutes")), "createdAt", fmt(t.get("created_at")));
    }
    private Map<String, Object> memberOptionVo(Map<String, Object> m) { return map("id", m.get("id"), "name", m.get("name"), "role", m.get("role")); }
    private Map<String, Object> memberAdminVo(Map<String, Object> m) {
        return map("id", m.get("id"), "teamId", m.get("team_id"), "name", m.get("name"), "role", m.get("role"),
                "status", enabled(m.get("status")) ? "active" : "inactive", "schoolName", m.get("institution_name"), "institutionName", m.get("institution_name"),
                "teamName", m.get("team_name"), "trackName", m.get("track_name"), "totalHours", nz(m.get("total_minutes")),
                "totalDisplayMinutes", nz(m.get("total_minutes")), "lastSubmit", fmt(m.get("last_submit")), "latestSubmitTime", fmt(m.get("last_submit")),
                "sortOrder", m.get("sort_order"), "createdAt", fmt(m.get("created_at")));
    }
    private Map<String, Object> categoryVo(Map<String, Object> c) {
        return map("id", c.get("id"), "name", c.get("name"), "trackName", c.get("track_name"), "color", "#00d4ff", "sortOrder", c.get("sort_order"), "status", enabled(c.get("status")) ? "active" : "inactive");
    }
    private Map<String, Object> taskOptionVo(Map<String, Object> t) { return map("id", t.get("id"), "categoryId", t.get("category_id"), "moduleId", t.get("category_id"), "name", t.get("name")); }
    private Map<String, Object> taskVo(Map<String, Object> t) {
        return map("id", t.get("id"), "categoryId", t.get("category_id"), "moduleId", t.get("category_id"), "name", t.get("name"), "description", t.get("description"),
                "difficultyLevel", t.get("difficulty_level"), "difficulty", t.get("difficulty_level"), "scoreWeight", t.get("score_weight"), "weight", t.get("score_weight"),
                "expectedMinutes", t.get("expected_minutes"), "sortOrder", t.get("sort_order"), "categoryName", t.get("category_name"), "status", enabled(t.get("status")) ? "active" : "inactive");
    }
    private Map<String, Object> timeLogVo(Map<String, Object> r) {
        Object minutes = r.get("duration_minutes");
        String ps = str(r.get("progress_status"));
        return map("id", r.get("id"), "recordDate", str(r.get("record_date")), "date", str(r.get("record_date")),
                "institutionName", r.get("institution_name"), "teamName", r.get("team_name"), "memberName", r.get("member_name"),
                "categoryName", r.get("category_name"), "moduleName", r.get("category_name"), "taskName", r.get("task_name"), "skillName", r.get("task_name"),
                "durationMinutes", minutes, "hours", minutes, "progressStatus", ps, "completionStatus", toFrontProgress(ps),
                "resultDesc", r.get("result_desc"), "todayOutput", r.get("result_desc"), "problemDesc", r.get("problem_desc"), "issue", r.get("problem_desc"),
                "needSupport", r.get("need_support"), "needHelp", r.get("need_support"), "isVoided", r.get("is_voided"),
                "recordStatus", Boolean.TRUE.equals(r.get("is_voided")) ? "voided" : "normal", "voidReason", r.get("void_reason"), "createdAt", fmt(r.get("created_at")), "submitTime", fmt(r.get("created_at")));
    }
    private Map<String, Object> issueVo(Map<String, Object> i) {
        return map("id", i.get("id"), "teamId", i.get("team_id"), "taskId", i.get("task_id"), "memberId", i.get("member_id"), "title", i.get("title"),
                "description", i.get("description"), "severity", toFrontSeverity(str(i.get("severity"))), "severityText", i.get("severity"),
                "status", toFrontIssueStatus(str(i.get("status"))), "statusText", i.get("status"), "solution", i.get("solution"),
                "institutionName", i.get("institution_name"), "teamName", i.get("team_name"), "taskName", i.get("skill_name"),
                "categoryName", i.get("category_name"), "skillName", i.get("skill_name"), "memberName", i.get("member_name"),
                "ownerName", i.get("member_name"), "assignee", "",
                "submitTime", fmt(i.get("created_at")), "createdAt", fmt(i.get("created_at")), "resolvedAt", fmt(i.get("resolved_at")));
    }

    private void normalizeTeam(Map<String, Object> d, boolean create) {
        d.put("institutionName", clean(first(d, "institutionName", "schoolName")));
        d.put("teamName", clean(first(d, "teamName")));
        d.put("trackName", normalizeTrackName(clean(first(d, "trackName", "track")), create));
        String loginCode = clean(first(d, "loginCode", "inviteCode"));
        d.put("loginCode", loginCode == null ? null : loginCode.toUpperCase(Locale.ROOT));
        Integer normalizedStatus = status(first(d, "status"));
        d.put("status", normalizedStatus == null && create ? 1 : normalizedStatus);
        if (create) {
            if (d.get("institutionName") == null) throw new BusinessException("院校名称不能为空");
            if (d.get("teamName") == null) throw new BusinessException("队伍名称不能为空");
            if (d.get("loginCode") == null) throw new BusinessException("邀请码不能为空");
        }
    }
    private void normalizeMember(Map<String, Object> d, boolean create) {
        d.put("teamId", num(first(d, "teamId")));
        d.put("name", clean(first(d, "name")));
        d.put("role", clean(first(d, "role")));
        d.put("sortOrder", integer(first(d, "sortOrder")));
        Integer normalizedStatus = status(first(d, "status"));
        d.put("status", normalizedStatus == null && create ? 1 : normalizedStatus);
        if (create && d.get("role") == null) d.put("role", "队员");
        if (create && d.get("sortOrder") == null) d.put("sortOrder", 0);
    }
    private void normalizeCategory(Map<String, Object> d, boolean create) {
        d.put("name", clean(first(d, "name")));
        d.put("trackName", normalizeTrackName(clean(first(d, "trackName")), create));
        d.put("sortOrder", integer(first(d, "sortOrder")));
        Integer normalizedStatus = status(first(d, "status"));
        d.put("status", normalizedStatus == null && create ? 1 : normalizedStatus);
        if (create && d.get("name") == null) throw new BusinessException("模块名称不能为空");
        if (create && d.get("trackName") == null) d.put("trackName", DEFAULT_TRACK);
        if (create && d.get("sortOrder") == null) d.put("sortOrder", 0);
    }
    private void normalizeTask(Map<String, Object> d, boolean create) {
        Long categoryId = num(first(d, "categoryId", "moduleId"));
        d.put("categoryId", categoryId);
        d.put("name", clean(first(d, "name")));
        d.put("description", clean(first(d, "description")));
        d.put("expectedMinutes", integer(first(d, "expectedMinutes")));
        d.put("difficultyLevel", integer(first(d, "difficultyLevel", "difficulty")));
        d.put("scoreWeight", decimal(first(d, "scoreWeight", "weight")));
        d.put("sortOrder", integer(first(d, "sortOrder")));
        Integer normalizedStatus = status(first(d, "status"));
        d.put("status", normalizedStatus == null && create ? 1 : normalizedStatus);
        if (create && categoryId != null && d.get("sortOrder") == null) d.put("sortOrder", mapper.nextTaskSortOrder(categoryId));
        if (create && d.get("expectedMinutes") == null) d.put("expectedMinutes", 0);
        if (create && d.get("difficultyLevel") == null) d.put("difficultyLevel", 1);
        if (create && d.get("scoreWeight") == null) d.put("scoreWeight", BigDecimal.valueOf(20));
        if (create && categoryId == null) throw new BusinessException("所属模块不能为空");
        if (create && d.get("name") == null) throw new BusinessException("技能点名称不能为空");
    }
    private void normalizeIssue(Map<String, Object> d) {
        d.put("severity", normalizeSeverity(str(first(d, "severity", "severityText"))));
        d.put("status", normalizeIssueStatus(str(first(d, "status", "statusText"))));
        if ("已解决".equals(d.get("status")) && d.get("resolvedAt") == null) d.put("resolvedAt", LocalDateTime.now());
    }
    private Map<String, Object> normalizeTimeLogFilter(Map<String, Object> p) {
        Map<String, Object> f = new HashMap<>(p);
        f.put("startDate", localDate(first(p, "startDate")));
        f.put("endDate", localDate(first(p, "endDate")));
        f.put("teamId", num(first(p, "teamId")));
        f.put("memberId", num(first(p, "memberId")));
        f.put("categoryId", num(first(p, "categoryId", "moduleId")));
        f.put("taskId", num(first(p, "taskId", "skillId")));
        f.put("needSupport", boolOrNull(first(p, "needSupport", "needHelp")));
        f.put("progressStatus", blank(str(first(p, "progressStatus"))) ? null : normalizeProgress(str(first(p, "progressStatus"))));
        f.put("isVoided", boolOrNull(first(p, "isVoided")));
        return f;
    }

    private String normalizeProgress(String v) {
        if (v == null || v.isBlank()) return "已完成";
        String normalized = switch (v) {
            case "completed" -> "已完成";
            case "partial" -> "部分完成";
            case "blocked" -> "受阻";
            case "notStarted" -> "未开始";
            case "inProgress" -> "进行中";
            case "mastered" -> "已掌握";
            default -> v;
        };
        if (!VALID_PROGRESS.contains(normalized)) throw new BusinessException("完成状态参数错误");
        return normalized;
    }
    private String toFrontProgress(String v) { return switch (v) { case "已完成", "已掌握" -> "completed"; case "受阻" -> "blocked"; case "未开始" -> "notStarted"; default -> "partial"; }; }
    private String normalizeIssueStatus(String v) { if (v == null || v.isBlank()) return null; return switch (v) { case "pending" -> "待处理"; case "processing" -> "处理中"; case "resolved" -> "已解决"; case "closed" -> "已关闭"; default -> v; }; }
    private String toFrontIssueStatus(String v) { return switch (v) { case "待处理" -> "pending"; case "处理中" -> "processing"; case "已解决" -> "resolved"; case "已关闭" -> "closed"; default -> v; }; }
    private String normalizeSeverity(String v) { if (v == null || v.isBlank()) return null; return switch (v) { case "critical" -> "高"; case "high" -> "高"; case "normal" -> "中"; case "low" -> "低"; default -> v; }; }
    private String toFrontSeverity(String v) { return switch (v) { case "高" -> "high"; case "中" -> "normal"; case "低" -> "low"; default -> v; }; }

    private String normalizeTrackName(String v, boolean create) {
        if (v == null || v.isBlank()) return create ? DEFAULT_TRACK : null;
        if (!VALID_TRACKS.contains(v)) throw new BusinessException("赛道只能选择新一代信息技术或人工智能");
        return v;
    }

    private Object first(Map<String, Object> m, String... keys) { for (String k : keys) if (m.containsKey(k) && m.get(k) != null) return m.get(k); return null; }
    private Long num(Object o) { if (o == null || str(o).isBlank()) return null; if (o instanceof Number n) return n.longValue(); return Long.valueOf(o.toString()); }
    private Integer integer(Object o) { Long n = num(o); return n == null ? null : n.intValue(); }
    private BigDecimal decimal(Object o) { if (o == null || str(o).isBlank()) return null; if (o instanceof BigDecimal b) return b; if (o instanceof Number n) return new BigDecimal(n.toString()); return new BigDecimal(o.toString()); }
    private String clean(Object o) { String s = str(o).trim(); return s.isBlank() ? null : s; }
    private boolean enabled(Object o) { return integer(o) == null || integer(o) == 1; }
    private Integer status(Object o) { if (o == null || str(o).isBlank() || "all".equals(o)) return null; if (o instanceof Number n) return n.intValue(); return "active".equals(o) || "启用".equals(o) ? 1 : "inactive".equals(o) || "停用".equals(o) ? 0 : Integer.valueOf(o.toString()); }
    private Boolean bool(Object o) { if (o == null) return false; if (o instanceof Boolean b) return b; return Boolean.valueOf(o.toString()); }
    private Boolean boolOrNull(Object o) { if (o == null || str(o).isBlank() || "all".equals(o)) return null; if (o instanceof Boolean b) return b; return Boolean.valueOf(o.toString()); }
    private String str(Object o) { return o == null ? "" : o.toString(); }
    private boolean blank(String s) { return s == null || s.isBlank() || "all".equals(s); }
    private Object nz(Object o) { return o == null ? 0 : o; }
    private int page(Map<String, Object> p) { Integer v = integer(first(p, "page")); return v == null || v < 1 ? 1 : v; }
    private int size(Map<String, Object> p) { Integer v = integer(first(p, "pageSize")); return v == null || v < 1 ? 10 : Math.min(v, 100); }
    private LocalDate localDate(Object o) { return o == null || str(o).isBlank() ? null : LocalDate.parse(str(o)); }
    private String fmt(Object o) {
        if (o == null) return "";
        if (o instanceof Timestamp t) return t.toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        return o.toString().replace('T', ' ');
    }
    private Map<String, Object> map(Object... kv) {
        Map<String, Object> m = new LinkedHashMap<>();
        for (int i = 0; i < kv.length; i += 2) m.put(String.valueOf(kv[i]), kv[i + 1]);
        return m;
    }
}
