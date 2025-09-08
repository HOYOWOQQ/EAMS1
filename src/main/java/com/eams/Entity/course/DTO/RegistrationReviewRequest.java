package com.eams.Entity.course.DTO;

import com.eams.Entity.course.Enum.RegistrationStatus;

public class RegistrationReviewRequest {
    private RegistrationStatus status;
    private String reviewNote;
    private Integer reviewerId;
    
    // Constructors, Getters and Setters
    public RegistrationStatus getStatus() { return status; }
    public void setStatus(RegistrationStatus status) { this.status = status; }
    
    public String getReviewNote() { return reviewNote; }
    public void setReviewNote(String reviewNote) { this.reviewNote = reviewNote; }
    
    public Integer getReviewerId() { return reviewerId; }
    public void setReviewerId(Integer reviewerId) { this.reviewerId = reviewerId; }
}