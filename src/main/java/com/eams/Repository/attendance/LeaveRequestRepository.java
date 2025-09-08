package com.eams.Repository.attendance;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.eams.Entity.attendance.LeaveRequest;

@Repository
public interface LeaveRequestRepository
        extends JpaRepository<LeaveRequest, Integer>, LeaveRequestCustomRepository {
    
    // 查詢學生的請假單，按提交時間降序
    List<LeaveRequest> findByStudentIdOrderBySubmittedAtDesc(Integer studentId);
    
    // 根據狀態查詢請假單，支持分頁
    Page<LeaveRequest> findByStatus(String status, Pageable pageable);
    
    // 根據狀態查詢請假單，按提交時間升序
    List<LeaveRequest> findByStatusOrderBySubmittedAtAsc(String status);
    
    // 查詢學生的請假單（使用下劃線命名）
    @Query("SELECT lr FROM LeaveRequest lr WHERE lr.student.id = :studentId ORDER BY lr.submittedAt DESC")
    List<LeaveRequest> findByStudent_IdOrderBySubmittedAtDesc(@Param("studentId") Integer studentId);
    
    boolean existsByAttendanceIdAndStatus(Integer attendanceId, String status);
}