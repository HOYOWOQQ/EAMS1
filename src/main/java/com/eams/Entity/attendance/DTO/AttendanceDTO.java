package com.eams.Entity.attendance.DTO;

import java.time.LocalDate;
import com.eams.Entity.attendance.Attendance;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "id") // ✅ 避免新增欄位改變 equals/hashCode 行為
public class AttendanceDTO {
    private Integer id;             // 仍保留（= 出勤主鍵）
    private Integer attendanceId;   // 新增別名，前端更直覺用
    private Integer studentId;      // 方便前端/補點名 fallback
    private Integer courseScheduleId;

    private String studentName;
    private String courseName;
    private LocalDate lessonDate;
    private Integer periodStart;
    private Integer periodEnd;

    private String status;      // 英文
    private String statusText;  // 中文
    private String remark;

    private Integer leaveRequestId;
    private String leaveRequestStatus;
    private String leaveRequestStatusText;
    private Boolean leaveApproved;

    public static AttendanceDTO fromEntity(Attendance entity) {
        if (entity == null) return null;
        AttendanceDTO dto = new AttendanceDTO();

        dto.setId(entity.getId());
        dto.setAttendanceId(entity.getId());

        if (entity.getStudent() != null) {
            dto.setStudentId(entity.getStudent().getId());
            dto.setStudentName(entity.getStudent().getMember() != null
                    ? entity.getStudent().getMember().getName()
                    : "未知學生");
        } else {
            dto.setStudentName("未知學生");
        }

        if (entity.getCourseSchedule() != null) {
            dto.setCourseScheduleId(entity.getCourseSchedule().getId());
            dto.setCourseName(entity.getCourseSchedule().getCourse() != null
                    ? entity.getCourseSchedule().getCourse().getName()
                    : "未知課程");
            dto.setLessonDate(entity.getCourseSchedule().getLessonDate());
            dto.setPeriodStart(entity.getCourseSchedule().getPeriodStart());
            dto.setPeriodEnd(entity.getCourseSchedule().getPeriodEnd());
        }

        dto.setStatus(entity.getStatus());
        dto.setStatusText(switch ((entity.getStatus() == null ? "" : entity.getStatus())) {
            case "ATTEND"   -> "出席";
            case "ABSENT"   -> "缺席";
            case "LATE"     -> "遲到";
            case "LEAVE"    -> "請假";
            case "UNMARKED" -> "未點名";
            default         -> "";
        });
        dto.setRemark(entity.getRemark());

        if (entity.getLeaveRequest() != null) {
            dto.setLeaveRequestId(entity.getLeaveRequest().getId());
            String ls = entity.getLeaveRequest().getStatus();
            dto.setLeaveRequestStatus(ls);
            dto.setLeaveRequestStatusText(
                ls == null ? "" :
                switch (ls) {
                    case "PENDING"  -> "待審核";
                    case "APPROVED" -> "已核准";
                    case "REJECTED" -> "未通過";
                    default         -> "";
                }
            );
            dto.setLeaveApproved("APPROVED".equalsIgnoreCase(ls));
        } else {
            dto.setLeaveApproved(false);
        }

        return dto;
    }
}
