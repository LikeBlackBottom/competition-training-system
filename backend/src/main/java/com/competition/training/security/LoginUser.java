package com.competition.training.security;

public class LoginUser {
    private String role;
    private Long adminId;
    private Long teamId;
    private String username;

    public LoginUser(String role, Long adminId, Long teamId, String username) {
        this.role = role;
        this.adminId = adminId;
        this.teamId = teamId;
        this.username = username;
    }

    public String getRole() { return role; }
    public Long getAdminId() { return adminId; }
    public Long getTeamId() { return teamId; }
    public String getUsername() { return username; }
}
