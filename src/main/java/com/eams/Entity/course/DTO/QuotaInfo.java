package com.eams.Entity.course.DTO;

public class QuotaInfo {
	private int quota;
	private int enrolled;
	private String deadline;

	public QuotaInfo(int quota, int enrolled, String deadline) {
		this.quota = quota;
		this.enrolled = enrolled;
		this.deadline = deadline;
	}

	public int getQuota() {
		return quota;
	}

	public void setQuota(int quota) {
		this.quota = quota;
	}

	public int getEnrolled() {
		return enrolled;
	}

	public void setEnrolled(int enrolled) {
		this.enrolled = enrolled;
	}

	public String getDeadline() {
		return deadline;
	}

	public void setDeadline(String deadline) {
		this.deadline = deadline;
	}
	
	
}