package com.eams.Entity.course.DTO;

import com.eams.Entity.course.CourseSchedule;

public class CourseScheduleDTO {
    private Integer id;
    private Integer courseId;
    private Integer classroomId;
    private Integer teacherId;
    private java.time.LocalDate lessonDate;
    private Integer periodStart;
    private Integer periodEnd;
    private String status;
    private String remark;
    private Integer subjectId;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getCourseId() {
		return courseId;
	}
	public void setCourseId(Integer courseId) {
		this.courseId = courseId;
	}
	public Integer getClassroomId() {
		return classroomId;
	}
	public void setClassroomId(Integer classroomId) {
		this.classroomId = classroomId;
	}
	public Integer getTeacherId() {
		return teacherId;
	}
	public void setTeacherId(Integer teacherId) {
		this.teacherId = teacherId;
	}
	public java.time.LocalDate getLessonDate() {
		return lessonDate;
	}
	public void setLessonDate(java.time.LocalDate lessonDate) {
		this.lessonDate = lessonDate;
	}
	public Integer getPeriodStart() {
		return periodStart;
	}
	public void setPeriodStart(Integer periodStart) {
		this.periodStart = periodStart;
	}
	public Integer getPeriodEnd() {
		return periodEnd;
	}
	public void setPeriodEnd(Integer periodEnd) {
		this.periodEnd = periodEnd;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public Integer getSubjectId() {
		return subjectId;
	}
	public void setSubjectId(Integer subjectId) {
		this.subjectId = subjectId;
	}
	@Override
	public String toString() {
		return "CourseScheduleDTO [id=" + id + ", courseId=" + courseId + ", classroomId=" + classroomId
				+ ", teacherId=" + teacherId + ", lessonDate=" + lessonDate + ", periodStart=" + periodStart
				+ ", periodEnd=" + periodEnd + ", status=" + status + ", remark=" + remark + ", subjectId=" + subjectId
				+ "]";
	}
	
    private String displayName;
    
	
	public static CourseScheduleDTO fromEntity(CourseSchedule entity) {
	    if (entity == null) return null;
	    CourseScheduleDTO dto = new CourseScheduleDTO();
	    dto.setId(entity.getId());
	    dto.setCourseId(entity.getCourse().getId());
	    dto.setClassroomId(entity.getClassroom().getId());
	    dto.setTeacherId(entity.getTeacher().getId());
	    dto.setLessonDate(entity.getLessonDate());
	    dto.setPeriodStart(entity.getPeriodStart());
	    dto.setPeriodEnd(entity.getPeriodEnd());
	    dto.setStatus(entity.getStatus());
	    dto.setRemark(entity.getRemark());
	    dto.setSubjectId(entity.getSubject().getId());
	    
	    dto.setDisplayName(buildDisplayName(entity));
	    return dto;
	}
	
	  private static String buildDisplayName(CourseSchedule entity) {
	        return String.format("課程%d-教室%d-%s-第%d-%d節", 
	            entity.getCourse().getId(),
	            entity.getClassroom().getId(), 
	            entity.getLessonDate(),
	            entity.getPeriodStart(),
	            entity.getPeriodEnd());
	    }
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	  
    
}
