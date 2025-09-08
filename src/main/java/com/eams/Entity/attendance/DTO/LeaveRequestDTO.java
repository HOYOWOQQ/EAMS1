package com.eams.Entity.attendance.DTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.eams.Entity.attendance.LeaveRequest;

import lombok.Data;

@Data
public class LeaveRequestDTO {
    private Integer id;
    private String studentName;
    private String courseName;
    private LocalDate lessonDate;
    private Integer periodStart;
    private Integer periodEnd;
    private String leaveType;
    private String reason;
    private String attachmentPath;
    private LocalDateTime submittedAt;
    
    private String status;
    private String statusText;
    
    private Integer attendanceId;
    
    // 用於批量請假
    private List<Integer> attendanceIds;

    public static LeaveRequestDTO fromEntity(LeaveRequest entity) {
        if (entity == null) return null;
        
        LeaveRequestDTO dto = new LeaveRequestDTO();
        dto.setId(entity.getId());
        
        // 學生資訊
        if (entity.getStudent() != null && entity.getStudent().getMember() != null) {
            dto.setStudentName(entity.getStudent().getMember().getName());
        } else {
            dto.setStudentName("未知學生");
        }
        
        // 課程資訊
        if (entity.getCourseSchedule() != null) {
            if (entity.getCourseSchedule().getCourse() != null) {
                dto.setCourseName(entity.getCourseSchedule().getCourse().getName());
            } else {
                dto.setCourseName("未知課程");
            }
            dto.setLessonDate(entity.getCourseSchedule().getLessonDate());
            dto.setPeriodStart(entity.getCourseSchedule().getPeriodStart());
            dto.setPeriodEnd(entity.getCourseSchedule().getPeriodEnd());
        }
        
        dto.setLeaveType(entity.getLeaveType());
        dto.setReason(entity.getReason());
        dto.setAttachmentPath(entity.getAttachmentPath());
        dto.setSubmittedAt(entity.getSubmittedAt());
        dto.setStatus(entity.getStatus());
        dto.setStatusText(getLeaveStatusText(entity.getStatus()));
        
        if (entity.getAttendance() != null) {
            dto.setAttendanceId(entity.getAttendance().getId());
        }
        
        return dto;
    }
    
    public LeaveRequest toEntity() {
        LeaveRequest req = new LeaveRequest();
        req.setReason(this.reason);
        req.setLeaveType(this.leaveType);
        req.setAttachmentPath(this.attachmentPath);
        req.setStatus("PENDING"); // 預設待審核
        return req;
    }
    
    private static String getLeaveStatusText(String status) {
        if (status == null) return "";
        switch (status) {
            case "APPROVED": return "已核准";
            case "REJECTED": return "未通過";
            case "PENDING": return "待審核"; 
            default: return "";
        }
    }
}

