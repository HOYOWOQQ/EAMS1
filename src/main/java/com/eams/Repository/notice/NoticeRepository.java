package com.eams.Repository.notice;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.eams.Entity.notice.Notice;
@Repository
public interface NoticeRepository extends JpaRepository<Notice, Integer> {
	
	List<Notice> findByIsActiveTrueOrderByCreatedAtDesc();
	
	// 根據課程ID查找活躍通知
    List<Notice> findByCourseIdAndIsActiveTrueOrderByCreatedAtDesc(Integer courseId);
    
    // 根據教師ID查找活躍通知
    List<Notice> findByTeacherIdAndIsActiveTrueOrderByCreatedAtDesc(Integer teacherId);
    
    // 根據ID和活躍狀態查找通知
    Optional<Notice> findByNoticeIdAndIsActiveTrue(Integer noticeId);
    
 // 獲取教師的通知（通過課程關聯）
    @Query("SELECT DISTINCT n FROM Notice n " +
           "JOIN Course c ON n.courseId = c.id " +
           "JOIN c.subjects s " +
           "JOIN s.teachers t " +
           "WHERE t.id = :teacherId AND n.isActive = true " +
           "ORDER BY n.createdAt DESC")
    List<Notice> findNoticesByTeacherId(@Param("teacherId") Integer teacherId);
    
    // 獲取學生的通知（通過選課關聯）
    @Query("SELECT DISTINCT n FROM Notice n " +
           "JOIN CourseEnroll ce ON n.courseId = ce.course.id " +
           "WHERE ce.student.id = :studentId AND ce.status = 'enrolled' " +
           "AND n.isActive = true " +
           "ORDER BY n.createdAt DESC")
    List<Notice> findNoticesByStudentId(@Param("studentId") Integer studentId);
    
    // 檢查用戶是否有權限訪問通知
    @Query("SELECT COUNT(n) > 0 FROM Notice n " +
           "JOIN Course c ON n.courseId = c.id " +
           "JOIN c.subjects s " +
           "JOIN s.teachers t " +
           "WHERE n.noticeId = :noticeId AND t.id = :userId AND n.isActive = true")
    boolean canTeacherAccessNotice(@Param("noticeId") Integer noticeId, @Param("userId") Integer userId);
    
    @Query("SELECT COUNT(n) > 0 FROM Notice n " +
           "JOIN CourseEnroll ce ON n.courseId = ce.course.id " +
           "WHERE n.noticeId = :noticeId AND ce.student.id = :userId " +
           "AND ce.status = 'enrolled' AND n.isActive = true")
    boolean canStudentAccessNotice(@Param("noticeId") Integer noticeId, @Param("userId") Integer userId);
    
    // 軟刪除通知
    @Modifying
    @Query("UPDATE Notice n SET n.isActive = false, n.updatedAt = CURRENT_TIMESTAMP " +
           "WHERE n.noticeId = :noticeId")
    int softDeleteNotice(@Param("noticeId") Integer noticeId);
    
    // 獲取未讀通知數量（教師）
    @Query("SELECT COUNT(n) FROM Notice n " +
           "JOIN Course c ON n.courseId = c.id " +
           "JOIN c.subjects s " +
           "JOIN s.teachers t " +
           "LEFT JOIN NoticeReadStatus nrs ON n.noticeId = nrs.noticeId AND nrs.userId = :userId " +
           "WHERE t.id = :userId AND n.isActive = true AND nrs.id IS NULL")
    long getUnreadNoticeCountForTeacher(@Param("userId") Integer userId);
    
    // 獲取未讀通知數量（學生）
    @Query("SELECT COUNT(n) FROM Notice n " +
           "JOIN CourseEnroll ce ON n.courseId = ce.course.id " +
           "LEFT JOIN NoticeReadStatus nrs ON n.noticeId = nrs.noticeId AND nrs.userId = :userId " +
           "WHERE ce.student.id = :userId AND ce.status = 'enrolled' " +
           "AND n.isActive = true AND nrs.id IS NULL")
    long getUnreadNoticeCountForStudent(@Param("userId") Integer userId);
    
    @Query("SELECT COUNT(ce) FROM CourseEnroll ce WHERE ce.course.id = :courseId AND ce.status = 'enrolled'")
    long countStudentsByCourseId(@Param("courseId") Integer courseId);
 // 統計指定通知對應課程的學生總數
    @Query(value = "SELECT COUNT(*) FROM course_enroll ce WHERE ce.course_id = " +
                   "(SELECT n.course_id FROM notice n WHERE n.notice_id = :noticeId AND n.is_active = 1)", 
           nativeQuery = true)
    long countStudentsByNoticeId(@Param("noticeId") Integer noticeId);

    // 查詢指定通知對應課程的所有學生ID
    @Query(value = "SELECT ce.student_id FROM course_enroll ce " +
                   "WHERE ce.course_id = (SELECT n.course_id FROM notice n WHERE n.notice_id = :noticeId AND n.is_active = 1)", 
           nativeQuery = true)
    List<Integer> findStudentIdsByNoticeId(@Param("noticeId") Integer noticeId);
}

