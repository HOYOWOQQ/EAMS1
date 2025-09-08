package com.eams.Controller.attendance;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eams.Entity.attendance.DTO.AttendanceDTO;
import com.eams.Entity.attendance.DTO.LeaveRequestDTO;
import com.eams.Service.attendance.AttendanceService;
import com.eams.Service.attendance.LeaveRequestService;
import com.eams.common.ApiResponse;
import com.eams.common.log.util.UserContextUtil;

import jakarta.servlet.http.HttpSession;

//@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/api/leave")
public class LeaveRequestController {

	@Autowired
	private LeaveRequestService leaveRequestService;

	@Autowired
	private AttendanceService attendanceService;
	
	@Autowired
	private UserContextUtil userContextUtil;

	/* ---------------- 共用工具 ---------------- */

    private ResponseEntity<ApiResponse<?>> invalidIfOutOfIntRange(Long uid) {
        if (uid == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("請先登入"));
        }
        if (uid > Integer.MAX_VALUE || uid < Integer.MIN_VALUE) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("使用者ID超出範圍"));
        }
        return null;
    }

    private boolean isStudent(String role) {
        return role != null && role.equalsIgnoreCase("student");
    }

    private boolean isTeacher(String role) {
        return role != null && role.equalsIgnoreCase("teacher");
    }

    /** 取得單筆請假單詳情（學生或老師都可看自己相關資料） */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LeaveRequestDTO>> getOne(@PathVariable Integer id) {
        try {
            LeaveRequestDTO dto = leaveRequestService.getLeaveRequestById(id);
            return ResponseEntity.ok(ApiResponse.success(dto));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    /* ---------------- 學生端 ---------------- */

    /** 學生查詢可請假的缺席記錄 */
    @GetMapping("/absent")
    public ResponseEntity<ApiResponse<List<AttendanceDTO>>> getAbsentAttendances() {
        try {
            Long uid = userContextUtil.getCurrentUserId();
            ResponseEntity<ApiResponse<?>> err = invalidIfOutOfIntRange(uid);
            if (err != null) return (ResponseEntity) err;

            if (!isStudent(userContextUtil.getCurrentUserRole())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error("沒有權限"));
            }

            int studentId = uid.intValue();
            List<AttendanceDTO> absentRecords = attendanceService.getAbsentAttendances(studentId);
            return ResponseEntity.ok(ApiResponse.success(absentRecords));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("查詢失敗：" + e.getMessage()));
        }
    }

    /** 學生新增單筆請假單 */
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<String>> createSingleLeave(@RequestBody LeaveRequestDTO dto) {
        try {
            Long uid = userContextUtil.getCurrentUserId();
            ResponseEntity<ApiResponse<?>> err = invalidIfOutOfIntRange(uid);
            if (err != null) return (ResponseEntity) err;

            if (!isStudent(userContextUtil.getCurrentUserRole())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error("沒有權限"));
            }

            int studentId = uid.intValue();
            leaveRequestService.createSingleLeaveRequest(dto, studentId);
            return ResponseEntity.ok(ApiResponse.success("請假單已送出"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(e.getMessage()));
        }
    }

    /** 學生批量請假 */
    @PostMapping("/createBatch")
    public ResponseEntity<ApiResponse<String>> createBatchLeave(@RequestBody LeaveRequestDTO dto) {
        try {
            Long uid = userContextUtil.getCurrentUserId();
            ResponseEntity<ApiResponse<?>> err = invalidIfOutOfIntRange(uid);
            if (err != null) return (ResponseEntity) err;

            if (!isStudent(userContextUtil.getCurrentUserRole())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error("沒有權限"));
            }

            int studentId = uid.intValue();
            List<com.eams.Entity.attendance.LeaveRequest> requests =
                    leaveRequestService.createBatchLeaveRequest(dto, studentId);
            return ResponseEntity.ok(ApiResponse.success("已成功申請 " + requests.size() + " 筆請假單"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(e.getMessage()));
        }
    }

    /** 學生查詢自己的請假單 */
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<LeaveRequestDTO>>> getMyLeaveRequests() {
        try {
            Long uid = userContextUtil.getCurrentUserId();
            ResponseEntity<ApiResponse<?>> err = invalidIfOutOfIntRange(uid);
            if (err != null) return (ResponseEntity) err;

            if (!isStudent(userContextUtil.getCurrentUserRole())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error("沒有權限"));
            }

            int studentId = uid.intValue();
            List<LeaveRequestDTO> requests = leaveRequestService.getLeaveRequestsByStudent(studentId);
            return ResponseEntity.ok(ApiResponse.success(requests));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("查詢失敗：" + e.getMessage()));
        }
    }

    /** 學生補交附件 */
    @PostMapping("/upload/{id}")
    public ResponseEntity<ApiResponse<String>> uploadAttachment(
            @PathVariable Integer id, @RequestParam String attachmentPath) {
        try {
            Long uid = userContextUtil.getCurrentUserId();
            ResponseEntity<ApiResponse<?>> err = invalidIfOutOfIntRange(uid);
            if (err != null) return (ResponseEntity) err;

            if (!isStudent(userContextUtil.getCurrentUserRole())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error("沒有權限"));
            }

            int studentId = uid.intValue();
            leaveRequestService.updateAttachment(id, attachmentPath, studentId);
            return ResponseEntity.ok(ApiResponse.success("附件更新完成"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(e.getMessage()));
        }
    }

    /* ---------------- 老師端（權限限定只有老師） ---------------- */

    /** 老師查詢待審核請假單（只看自己授課相關） */
    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<List<LeaveRequestDTO>>> getPendingRequests() {
        try {
            Long uid = userContextUtil.getCurrentUserId();
            ResponseEntity<ApiResponse<?>> err = invalidIfOutOfIntRange(uid);
            if (err != null) return (ResponseEntity) err;

            if (!isTeacher(userContextUtil.getCurrentUserRole())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error("沒有權限"));
            }

            int teacherId = uid.intValue();
            List<LeaveRequestDTO> requests = leaveRequestService.getPendingLeaveRequests(teacherId);
            return ResponseEntity.ok(ApiResponse.success(requests));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("查詢失敗：" + e.getMessage()));
        }
    }

    /** 老師審核通過 */
    @PostMapping("/approve/{id}")
    public ResponseEntity<ApiResponse<String>> approve(@PathVariable Integer id) {
        try {
            Long uid = userContextUtil.getCurrentUserId();
            ResponseEntity<ApiResponse<?>> err = invalidIfOutOfIntRange(uid);
            if (err != null) return (ResponseEntity) err;

            if (!isTeacher(userContextUtil.getCurrentUserRole())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error("沒有權限"));
            }

            int teacherId = uid.intValue();
            leaveRequestService.approveLeaveRequest(id, teacherId);
            return ResponseEntity.ok(ApiResponse.success("請假單已審核通過"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(e.getMessage()));
        }
    }

    /** 老師審核不通過 */
    @PostMapping("/reject/{id}")
    public ResponseEntity<ApiResponse<String>> reject(@PathVariable Integer id) {
        try {
            Long uid = userContextUtil.getCurrentUserId();
            ResponseEntity<ApiResponse<?>> err = invalidIfOutOfIntRange(uid);
            if (err != null) return (ResponseEntity) err;

            if (!isTeacher(userContextUtil.getCurrentUserRole())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error("沒有權限"));
            }

            int teacherId = uid.intValue();
            leaveRequestService.rejectLeaveRequest(id, teacherId);
            return ResponseEntity.ok(ApiResponse.success("請假單已退回"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(e.getMessage()));
        }
    }
}