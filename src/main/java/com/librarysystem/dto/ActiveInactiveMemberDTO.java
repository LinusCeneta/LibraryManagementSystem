package com.librarysystem.dto;

public class ActiveInactiveMemberDTO {
    private String status; // "Active" or "Inactive"
    private long memberCount;

    public ActiveInactiveMemberDTO(String status, long memberCount) {
        this.status = status;
        this.memberCount = memberCount;
    }

    // Getters
    public String getStatus() {
        return status;
    }

    public long getMemberCount() {
        return memberCount;
    }

    // Setters (optional)
}
