package com.eams.Service.attendance;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eams.Entity.attendance.Attendance;
import com.eams.Entity.attendance.LeaveRequest;
import com.eams.Entity.attendance.DTO.LeaveRequestDTO;
import com.eams.Repository.attendance.AttendanceRepository;
import com.eams.Repository.attendance.LeaveRequestRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class LeaveRequestService {

    @Autowired
    private AttendanceRepository attendanceRepository;
    
    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    /**
     * 學生新增單筆請假單
     */
    public LeaveRequest createSingleLeaveRequest(LeaveRequestDTO dto, Integer studentId) {
        Attendance attendance = attendanceRepository.findById(dto.getAttendanceId())
            .orElseThrow(() -> new RuntimeException("找不到對應的出勤記錄"));
            
        // 檢查是否已有請假單
        if (attendance.getLeaveRequest() != null) {
            throw new RuntimeException("此出勤記錄已有請假單，無法重複申請");
        }

        LeaveRequest request = dto.toEntity();
        request.setStudent(attendance.getStudent());
        request.setCourseSchedule(attendance.getCourseSchedule());
        request.setAttendance(attendance);
        request.setSubmittedAt(LocalDateTime.now());
        
        return leaveRequestRepository.save(request);
    }

    /**
     * 學生批量請假（多筆缺席記錄一次申請）
     */
    public List<LeaveRequest> createBatchLeaveRequest(LeaveRequestDTO dto, Integer studentId) {
        if (dto.getAttendanceIds() == null || dto.getAttendanceIds().isEmpty()) {
            throw new RuntimeException("未指定要請假的出勤記錄");
        }
        
        List<Attendance> attendances = attendanceRepository.findAllById(dto.getAttendanceIds());
        List<LeaveRequest> requests = new ArrayList<>();
        
        for (Attendance attendance : attendances) {
            // 檢查是否已有請假單
            if (attendance.getLeaveRequest() != null) {
                continue; // 跳過已有請假單的記錄
            }
            
            LeaveRequest request = new LeaveRequest();
            request.setStudent(attendance.getStudent());
            request.setCourseSchedule(attendance.getCourseSchedule());
            request.setAttendance(attendance);
            request.setLeaveType(dto.getLeaveType());
            request.setReason(dto.getReason());
            request.setAttachmentPath(dto.getAttachmentPath());
            request.setStatus("PENDING");
            request.setSubmittedAt(LocalDateTime.now());
            
            requests.add(request);
        }
        
        return leaveRequestRepository.saveAll(requests);
    }

    /**
     * 查詢學生的請假單
     */
    public List<LeaveRequestDTO> getLeaveRequestsByStudent(Integer studentId) {
        List<LeaveRequest> requests = leaveRequestRepository.findByStudent_IdOrderBySubmittedAtDesc(studentId);
        return requests.stream()
            .map(LeaveRequestDTO::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * 查詢待審核的請假單（給老師用）
     */
    public List<LeaveRequestDTO> getPendingLeaveRequests(Integer teacherId) {
        // 這裡可以根據老師ID查詢其授課的請假單
        List<LeaveRequest> requests = leaveRequestRepository.findByStatusOrderBySubmittedAtAsc("PENDING");
        return requests.stream()
            .map(LeaveRequestDTO::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * 查單一請假單
     */
    public LeaveRequestDTO getLeaveRequestById(Integer id) {
        LeaveRequest request = leaveRequestRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("找不到請假單"));
        return LeaveRequestDTO.fromEntity(request);
    }

    /**
     * 學生補附件
     */
    public void updateAttachment(Integer id, String attachmentPath, Integer studentId) {
        LeaveRequest request = leaveRequestRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("找不到請假單"));
       
        if ("APPROVED".equals(request.getStatus())) {
            throw new RuntimeException("通過的請假單不可以補附件");
        }
        
        request.setAttachmentPath(attachmentPath);
        request.setSubmittedAt(LocalDateTime.now());
        request.setStatus("PENDING");
        leaveRequestRepository.save(request);
    }

    /**
     * 老師審核通過
     */
    public void approveLeaveRequest(Integer id, Integer teacherId) {
        LeaveRequest request = leaveRequestRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("找不到請假單"));
       reviewLeaveRequest(request, true);
    }

    /**
     * 老師審核不通過
     */
    public void rejectLeaveRequest(Integer id, Integer teacherId) {
        LeaveRequest request = leaveRequestRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("找不到請假單"));   
        reviewLeaveRequest(request, false);
    }

    /**
     * 內部方法：處理審核結果
     */
    private void reviewLeaveRequest(LeaveRequest request, boolean isApproved) {
        if ("APPROVED".equals(request.getStatus())) {
            throw new RuntimeException("通過的假單不能再審核審核");
        }
        
        request.setStatus(isApproved ? "APPROVED" : "REJECTED");
        leaveRequestRepository.save(request);

        // 如果通過，更新出勤狀態
        if (isApproved && request.getAttendance() != null) {
            Attendance attendance = request.getAttendance();
            attendance.setStatus(Attendance.STATUS_LEAVE);
            attendanceRepository.save(attendance);
        }
    }
}
