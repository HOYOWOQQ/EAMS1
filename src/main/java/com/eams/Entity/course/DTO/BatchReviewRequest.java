package com.eams.Entity.course.DTO;

import java.util.List;

import com.eams.Entity.course.Enum.RegistrationStatus;

public class BatchReviewRequest {
	 private List<Integer> registrationIds;
	 private RegistrationStatus status;
	 private String reviewNote;
	 private Integer reviewerId;
	 
	 // Constructors, Getters and Setters
	 public List<Integer> getRegistrationIds() { return registrationIds; }
	 public void setRegistrationIds(List<Integer> registrationIds) { this.registrationIds = registrationIds; }
	 
	 public RegistrationStatus getStatus() { return status; }
	 public void setStatus(RegistrationStatus status) { this.status = status; }
	 
	 public String getReviewNote() { return reviewNote; }
	 public void setReviewNote(String reviewNote) { this.reviewNote = reviewNote; }
	 
	 public Integer getReviewerId() { return reviewerId; }
	 public void setReviewerId(Integer reviewerId) { this.reviewerId = reviewerId; }
	}
