package com.competition.training.module.timelog.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class TrainingMapper {
    private final JdbcTemplate jdbc;
    private final NamedParameterJdbcTemplate named;

    public Optional<Map<String, Object>> findAdmin(String username) {
        return one("select * from admin_users where username = ?", username);
    }

    public Optional<Map<String, Object>> findTeamByCode(String code) {
        return one("select * from teams where upper(login_code) = upper(?)", code);
    }

    public Optional<Map<String, Object>> findTeam(Long id) {
        return one("select * from teams where id = ?", id);
    }

    public Optional<Map<String, Object>> findMember(Long id) {
        return one("select * from members where id = ?", id);
    }

    public Optional<Map<String, Object>> findTask(Long id) {
        return one("select * from skill_tasks where id = ?", id);
    }

    public List<Map<String, Object>> activeMembers(Long teamId) {
        return jdbc.queryForList("select id,name,role from members where team_id=? and status=1 order by sort_order,id", teamId);
    }

    public List<Map<String, Object>> activeCategories(String trackName) {
        return jdbc.queryForList("select * from skill_categories where status=1 and track_name=? order by sort_order,id", trackName);
    }

    public List<Map<String, Object>> activeTasks(String trackName) {
        return jdbc.queryForList("""
                select st.* from skill_tasks st
                join skill_categories sc on sc.id=st.category_id
                where st.status=1 and sc.status=1 and sc.track_name=?
                order by st.sort_order,st.id
                """, trackName);
    }

    public Optional<Long> findDailyReportId(Long teamId, LocalDate recordDate) {
        return one("select id from daily_reports where team_id=? and report_date=?", teamId, Date.valueOf(recordDate))
                .map(row -> ((Number) row.get("id")).longValue());
    }

    public Long insertDailyReport(Long teamId, LocalDate recordDate, Integer durationMinutes) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update((PreparedStatementCreator) con -> {
            PreparedStatement ps = con.prepareStatement("""
                    insert into daily_reports(team_id,report_date,total_training_minutes,speech_training_minutes,summary,problems,next_plan,submitted_by,created_at,updated_at)
                    values (?,?,?,?,?,?,?,?,current_timestamp,current_timestamp)
                    """, new String[]{"id"});
            ps.setLong(1, teamId);
            ps.setDate(2, Date.valueOf(recordDate));
            ps.setInt(3, durationMinutes == null ? 0 : durationMinutes);
            ps.setInt(4, 0);
            ps.setString(5, "自动生成日报");
            ps.setString(6, null);
            ps.setString(7, null);
            ps.setString(8, "team");
            return ps;
        }, keyHolder);
        Number key = keyHolder.getKey();
        return key == null ? null : key.longValue();
    }

    public Long insertTimeLog(Long dailyReportId, Long teamId, Long memberId, Long taskId, LocalDate recordDate, Integer durationMinutes,
                              String progressStatus, String resultDesc, String problemDesc, Boolean needSupport) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update((PreparedStatementCreator) con -> {
            PreparedStatement ps = con.prepareStatement("""
                    insert into time_logs(daily_report_id,team_id,member_id,task_id,record_date,duration_minutes,progress_status,result_desc,problem_desc,need_support,is_voided,void_reason,created_at,updated_at)
                    values (?,?,?,?,?,?,?,?,?,?,false,null,current_timestamp,current_timestamp)
                    """, new String[]{"id"});
            ps.setLong(1, dailyReportId);
            ps.setLong(2, teamId);
            ps.setLong(3, memberId);
            ps.setLong(4, taskId);
            ps.setDate(5, Date.valueOf(recordDate));
            ps.setInt(6, durationMinutes);
            ps.setString(7, progressStatus);
            ps.setString(8, resultDesc);
            ps.setString(9, problemDesc);
            ps.setBoolean(10, Boolean.TRUE.equals(needSupport));
            return ps;
        }, keyHolder);
        Number key = keyHolder.getKey();
        return key == null ? null : key.longValue();
    }

    public List<Map<String, Object>> teams(String keyword, Integer status, LocalDate today, int limit, int offset) {
        var p = params(keyword, status, limit, offset);
        p.addValue("today", Date.valueOf(today));
        return named.queryForList("""
                select t.*,
                       (select count(*) from members m where m.team_id=t.id) member_count,
                       exists(select 1 from time_logs l where l.team_id=t.id and l.record_date=cast(:today as date) and l.is_voided=false) today_submitted,
                       coalesce((select sum(duration_minutes) from time_logs l where l.team_id=t.id and l.is_voided=false),0) total_minutes
                from teams t
                where (cast(:keyword as text) is null or t.institution_name like cast(:kw as text) or t.team_name like cast(:kw as text) or t.track_name like cast(:kw as text) or t.login_code like cast(:kw as text))
                  and (cast(:status as smallint) is null or t.status=cast(:status as smallint))
                order by t.id desc limit :limit offset :offset
                """, p);
    }

    public long teamsCount(String keyword, Integer status) {
        return count("""
                select count(*) from teams t
                where (cast(:keyword as text) is null or t.institution_name like cast(:kw as text) or t.team_name like cast(:kw as text) or t.track_name like cast(:kw as text) or t.login_code like cast(:kw as text))
                  and (cast(:status as smallint) is null or t.status=cast(:status as smallint))
                """, params(keyword, status, 0, 0));
    }

    public Long insertTeam(Map<String, Object> data) {
        return insert("""
                insert into teams(institution_name,team_name,track_name,login_code,status)
                values (?,?,?,?,?)
                """, data.get("institutionName"), data.get("teamName"), data.get("trackName"), data.get("loginCode"), data.get("status"));
    }

    public void updateTeam(Long id, Map<String, Object> data) {
        jdbc.update("""
                update teams set institution_name=coalesce(nullif(cast(? as text),''),institution_name), team_name=coalesce(nullif(cast(? as text),''),team_name),
                track_name=coalesce(nullif(cast(? as text),''),track_name), login_code=coalesce(nullif(cast(? as text),''),login_code), status=coalesce(?,status), updated_at=current_timestamp where id=?
                """, data.get("institutionName"), data.get("teamName"), data.get("trackName"), data.get("loginCode"), data.get("status"), id);
    }

    public List<Map<String, Object>> members(Long teamId, String keyword, Integer status, int limit, int offset) {
        var p = params(keyword, status, limit, offset).addValue("teamId", teamId);
        return named.queryForList("""
                select m.*,t.institution_name,t.team_name,t.track_name,
                       coalesce((select sum(duration_minutes) from time_logs l where l.member_id=m.id and l.team_id=m.team_id and l.is_voided=false),0) total_minutes,
                       (select max(created_at) from time_logs l where l.member_id=m.id and l.team_id=m.team_id) last_submit
                from members m join teams t on t.id=m.team_id
                where (cast(:teamId as bigint) is null or m.team_id=cast(:teamId as bigint))
                  and (cast(:keyword as text) is null or m.name ilike cast(:kw as text) or t.institution_name ilike cast(:kw as text) or t.team_name ilike cast(:kw as text) or m.role ilike cast(:kw as text))
                  and (cast(:status as smallint) is null or m.status=cast(:status as smallint))
                order by m.team_id,m.sort_order,m.id limit :limit offset :offset
                """, p);
    }

    public long membersCount(Long teamId, String keyword, Integer status) {
        return count("""
                select count(*) from members m join teams t on t.id=m.team_id
                where (cast(:teamId as bigint) is null or m.team_id=cast(:teamId as bigint))
                  and (cast(:keyword as text) is null or m.name ilike cast(:kw as text) or t.institution_name ilike cast(:kw as text) or t.team_name ilike cast(:kw as text) or m.role ilike cast(:kw as text))
                  and (cast(:status as smallint) is null or m.status=cast(:status as smallint))
                """, params(keyword, status, 0, 0).addValue("teamId", teamId));
    }

    public Long insertMember(Map<String, Object> data) {
        return insert("insert into members(team_id,name,role,sort_order,status) values (?,?,?,?,?)",
                data.get("teamId"), data.get("name"), data.get("role"), data.get("sortOrder"), data.get("status"));
    }

    public void updateMember(Long id, Map<String, Object> data) {
        jdbc.update("update members set team_id=coalesce(?,team_id), name=coalesce(nullif(cast(? as text),''),name), role=coalesce(nullif(cast(? as text),''),role), sort_order=coalesce(?,sort_order), status=coalesce(?,status), updated_at=current_timestamp where id=?",
                data.get("teamId"), data.get("name"), data.get("role"), data.get("sortOrder"), data.get("status"), id);
    }

    public List<Map<String, Object>> categories(Integer status) {
        return named.queryForList("select * from skill_categories where (cast(:status as smallint) is null or status=cast(:status as smallint)) order by sort_order,id",
                new MapSqlParameterSource("status", status));
    }

    public Long insertCategory(Map<String, Object> data) {
        return insert("insert into skill_categories(track_name,name,sort_order,status) values (?,?,?,?)",
                data.get("trackName"), data.get("name"), data.get("sortOrder"), data.get("status"));
    }

    public void updateCategory(Long id, Map<String, Object> data) {
        jdbc.update("update skill_categories set track_name=coalesce(nullif(cast(? as text),''),track_name), name=coalesce(nullif(cast(? as text),''),name), sort_order=coalesce(?,sort_order), status=coalesce(?,status), updated_at=current_timestamp where id=?",
                data.get("trackName"), data.get("name"), data.get("sortOrder"), data.get("status"), id);
    }

    public List<Map<String, Object>> tasks(Long categoryId, Integer status) {
        return named.queryForList("""
                select st.*,sc.name category_name from skill_tasks st join skill_categories sc on sc.id=st.category_id
                where (cast(:categoryId as bigint) is null or st.category_id=cast(:categoryId as bigint))
                  and (cast(:status as smallint) is null or st.status=cast(:status as smallint))
                order by st.category_id,st.sort_order,st.id
                """, new MapSqlParameterSource("categoryId", categoryId).addValue("status", status));
    }

    public Long insertTask(Map<String, Object> data) {
        return insert("insert into skill_tasks(category_id,name,description,expected_minutes,difficulty_level,score_weight,sort_order,status) values (?,?,?,?,?,?,?,?)",
                data.get("categoryId"), data.get("name"), data.get("description"), data.get("expectedMinutes"),
                data.get("difficultyLevel"), data.get("scoreWeight"), data.get("sortOrder"), data.get("status"));
    }

    public Integer nextTaskSortOrder(Long categoryId) {
        Integer v = jdbc.queryForObject("select coalesce(max(sort_order),0)+1 from skill_tasks where category_id=?", Integer.class, categoryId);
        return v == null ? 1 : v;
    }

    public void updateTask(Long id, Map<String, Object> data) {
        jdbc.update("""
                update skill_tasks set category_id=coalesce(?,category_id), name=coalesce(nullif(cast(? as text),''),name), description=coalesce(nullif(cast(? as text),''),description),
                expected_minutes=coalesce(?,expected_minutes), difficulty_level=coalesce(?,difficulty_level),
                score_weight=coalesce(?,score_weight), sort_order=coalesce(?,sort_order), status=coalesce(?,status), updated_at=current_timestamp where id=?
                """, data.get("categoryId"), data.get("name"), data.get("description"), data.get("expectedMinutes"),
                data.get("difficultyLevel"), data.get("scoreWeight"), data.get("sortOrder"), data.get("status"), id);
    }

    public List<Map<String, Object>> timeLogs(Map<String, Object> f, int limit, int offset) {
        var p = filterParams(f).addValue("limit", limit).addValue("offset", offset);
        return named.queryForList(timeLogSql("select l.*,t.institution_name,t.team_name,m.name member_name,sc.name category_name,st.name task_name ", true) + " limit :limit offset :offset", p);
    }

    public long timeLogsCount(Map<String, Object> f) {
        return count(timeLogSql("select count(*) ", false), filterParams(f));
    }

    public void voidTimeLog(Long id, String reason) {
        jdbc.update("update time_logs set is_voided=true, void_reason=?, updated_at=current_timestamp where id=? and is_voided=false", reason, id);
    }

    public List<Map<String, Object>> issues(Map<String, Object> f, int limit, int offset) {
        var p = filterParams(f).addValue("limit", limit).addValue("offset", offset);
        return named.queryForList("""
                select i.*,t.institution_name,t.team_name,coalesce(sc.name,'') category_name,coalesce(st.name,'') skill_name,coalesce(m.name,'') member_name
                from issues i join teams t on t.id=i.team_id
                left join skill_tasks st on st.id=i.task_id left join skill_categories sc on sc.id=st.category_id
                left join members m on m.id=i.member_id and m.team_id=i.team_id
                where (cast(:teamId as bigint) is null or i.team_id=cast(:teamId as bigint))
                  and (cast(:statusText as text) is null or i.status=cast(:statusText as text))
                  and (cast(:severityText as text) is null or i.severity=cast(:severityText as text))
                order by i.created_at desc limit :limit offset :offset
                """, p);
    }

    public long issuesCount(Map<String, Object> f) {
        return count("""
                select count(*) from issues i
                where (cast(:teamId as bigint) is null or i.team_id=cast(:teamId as bigint))
                  and (cast(:statusText as text) is null or i.status=cast(:statusText as text))
                  and (cast(:severityText as text) is null or i.severity=cast(:severityText as text))
                """, filterParams(f));
    }

    public Long insertIssue(Map<String, Object> d) {
        return insert("insert into issues(team_id,task_id,member_id,title,description,severity,status,solution,resolved_at) values (?,?,?,?,?,?,?,?,?)",
                d.get("teamId"), d.get("taskId"), d.get("memberId"), d.get("title"), d.get("description"),
                d.get("severity"), d.get("status"), d.get("solution"), d.get("resolvedAt"));
    }

    public void updateIssue(Long id, Map<String, Object> d) {
        jdbc.update("update issues set team_id=coalesce(?,team_id), task_id=?, member_id=?, title=coalesce(?,title), description=coalesce(?,description), severity=coalesce(?,severity), status=coalesce(?,status), solution=?, resolved_at=? where id=?",
                d.get("teamId"), d.get("taskId"), d.get("memberId"), d.get("title"), d.get("description"),
                d.get("severity"), d.get("status"), d.get("solution"), d.get("resolvedAt"), id);
    }

    public List<Map<String, Object>> dashboardRows(String sql) {
        return jdbc.queryForList(sql);
    }

    public List<Map<String, Object>> dashboardRows(String sql, Object... args) {
        return jdbc.queryForList(sql, args);
    }

    public Optional<Map<String, Object>> one(String sql, Object... args) {
        List<Map<String, Object>> rows = jdbc.queryForList(sql, args);
        return rows.stream().findFirst();
    }

    private Long insert(String sql, Object... args) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update((PreparedStatementCreator) con -> {
            PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"});
            for (int i = 0; i < args.length; i++) ps.setObject(i + 1, args[i]);
            return ps;
        }, keyHolder);
        Number key = keyHolder.getKey();
        return key == null ? null : key.longValue();
    }

    private long count(String sql, MapSqlParameterSource p) {
        Long v = named.queryForObject(sql, p, Long.class);
        return v == null ? 0 : v;
    }

    private MapSqlParameterSource params(String keyword, Integer status, int limit, int offset) {
        return new MapSqlParameterSource("keyword", blank(keyword) ? null : keyword)
                .addValue("kw", blank(keyword) ? null : "%" + keyword + "%").addValue("status", status).addValue("limit", limit).addValue("offset", offset);
    }

    private String timeLogSql(String select, boolean orderBy) {
        String sql = select + """
                from time_logs l join teams t on t.id=l.team_id join members m on m.id=l.member_id and m.team_id=l.team_id
                join skill_tasks st on st.id=l.task_id join skill_categories sc on sc.id=st.category_id
                where (cast(:startDate as date) is null or l.record_date >= cast(:startDate as date))
                  and (cast(:endDate as date) is null or l.record_date <= cast(:endDate as date))
                  and (cast(:teamId as bigint) is null or l.team_id=cast(:teamId as bigint))
                  and (cast(:memberId as bigint) is null or l.member_id=cast(:memberId as bigint))
                  and (cast(:categoryId as bigint) is null or st.category_id=cast(:categoryId as bigint))
                  and (cast(:taskId as bigint) is null or l.task_id=cast(:taskId as bigint))
                  and (cast(:needSupport as boolean) is null or l.need_support=cast(:needSupport as boolean))
                  and (cast(:progressStatus as text) is null or l.progress_status=cast(:progressStatus as text))
                  and (cast(:isVoided as boolean) is null or l.is_voided=cast(:isVoided as boolean))
                """;
        return orderBy ? sql + " order by l.record_date desc,l.created_at desc " : sql;
    }

    private MapSqlParameterSource filterParams(Map<String, Object> f) {
        return new MapSqlParameterSource()
                .addValue("startDate", f.get("startDate")).addValue("endDate", f.get("endDate"))
                .addValue("teamId", f.get("teamId")).addValue("memberId", f.get("memberId"))
                .addValue("categoryId", f.get("categoryId")).addValue("taskId", f.get("taskId"))
                .addValue("needSupport", f.get("needSupport")).addValue("progressStatus", f.get("progressStatus"))
                .addValue("isVoided", f.get("isVoided")).addValue("statusText", f.get("statusText"))
                .addValue("severityText", f.get("severityText"));
    }

    private boolean blank(String s) {
        return s == null || s.isBlank();
    }
}
