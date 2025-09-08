package com.eams.Repository.course;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.eams.Entity.course.Registration;
import com.eams.Entity.course.Enum.RegistrationStatus;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Integer>, JpaSpecificationExecutor<Registration> {
    
    // ===== 現有學生相關查詢 =====
    
    // 查詢學生的報名記錄（現有學生）
    List<Registration> findByStudentIdOrderByRegistrationDateDesc(Integer studentId);
    
    // 查詢學生在特定課程的報名記錄（現有學生）
    Optional<Registration> findByCourseIdAndStudentId(Integer courseId, Integer studentId);
    
    // 檢查學生是否已報名特定課程（現有學生）
    boolean existsByCourseIdAndStudentId(Integer courseId, Integer studentId);
    
    // ===== 新學生申請相關查詢 =====
    
    // 查詢新學生申請記錄（studentId 為 null）
    @Query("SELECT r FROM Registration r WHERE r.studentId IS NULL ORDER BY r.registrationDate DESC")
    List<Registration> findNewStudentApplications();
    
    // 查詢待審核的新學生申請
    @Query("SELECT r FROM Registration r WHERE r.studentId IS NULL AND r.status = 'PENDING' ORDER BY r.registrationDate ASC")
    List<Registration> findPendingNewStudentApplications();
    
    // 根據帳號查詢新學生申請（避免重複申請）
    @Query("SELECT r FROM Registration r WHERE r.studentAccount = :account AND r.studentId IS NULL")
    List<Registration> findNewStudentApplicationsByAccount(@Param("account") String account);
    
    // 檢查帳號是否已有待審核的申請
    @Query("SELECT COUNT(r) > 0 FROM Registration r WHERE r.studentAccount = :account AND r.studentId IS NULL AND r.status = 'PENDING'")
    boolean existsPendingApplicationByAccount(@Param("account") String account);
    
    // 檢查新學生是否已申請過特定課程
    @Query("SELECT COUNT(r) > 0 FROM Registration r WHERE r.courseId = :courseId AND r.studentAccount = :account AND r.studentId IS NULL")
    boolean existsNewStudentApplicationByCourseAndAccount(@Param("courseId") Integer courseId, @Param("account") String account);
    
    // ===== 通用查詢 =====
    
    // 查詢課程的報名記錄（包含現有學生和新學生申請）
    List<Registration> findByCourseIdOrderByRegistrationDateDesc(Integer courseId);
    
    // 查詢特定狀態的報名記錄
    List<Registration> findByStatusOrderByRegistrationDateDesc(RegistrationStatus status);
    
    // 查詢所有待審核的記錄（包含現有學生和新學生申請）
    @Query("SELECT r FROM Registration r WHERE r.status = 'PENDING' ORDER BY r.registrationDate ASC")
    List<Registration> findPendingRegistrations();
    
    // 查詢特定時間範圍的報名記錄
    @Query("SELECT r FROM Registration r WHERE r.registrationDate BETWEEN :startDate AND :endDate ORDER BY r.registrationDate DESC")
    List<Registration> findByRegistrationDateBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // ===== 統計相關查詢 =====
    
    // 統計課程的報名人數（按狀態）
    @Query("SELECT COUNT(r) FROM Registration r WHERE r.courseId = :courseId AND r.status = :status")
    Integer countByCourseIdAndStatus(@Param("courseId") Integer courseId, @Param("status") RegistrationStatus status);
    
    // 查詢課程的已核准報名數量（包含現有學生和新學生）
    @Query("SELECT COUNT(r) FROM Registration r WHERE r.courseId = :courseId AND r.status = 'APPROVED'")
    Integer countApprovedRegistrationsByCourseId(@Param("courseId") Integer courseId);
    
    // 查詢課程的現有學生已核准數量
    @Query("SELECT COUNT(r) FROM Registration r WHERE r.courseId = :courseId AND r.status = 'APPROVED' AND r.studentId IS NOT NULL")
    Integer countApprovedExistingStudentsByCourseId(@Param("courseId") Integer courseId);
    
    // 查詢課程的新學生申請已核准數量
    @Query("SELECT COUNT(r) FROM Registration r WHERE r.courseId = :courseId AND r.status = 'APPROVED' AND r.studentId IS NULL")
    Integer countApprovedNewStudentsByCourseId(@Param("courseId") Integer courseId);
    
    // 查詢學生的報名狀態統計（現有學生）
    @Query("SELECT r.status, COUNT(r) FROM Registration r WHERE r.studentId = :studentId GROUP BY r.status")
    List<Object[]> getRegistrationStatusCountByStudentId(@Param("studentId") Integer studentId);
    
    // ===== 分類統計查詢 =====
    
    // 統計各種報名類型的數量
    @Query("SELECT " +
           "SUM(CASE WHEN r.studentId IS NOT NULL THEN 1 ELSE 0 END) as existingStudents, " +
           "SUM(CASE WHEN r.studentId IS NULL THEN 1 ELSE 0 END) as newStudentApplications " +
           "FROM Registration r WHERE r.courseId = :courseId AND r.status = :status")
    Object[] getRegistrationTypeCountByCourseAndStatus(@Param("courseId") Integer courseId, @Param("status") RegistrationStatus status);
    
    // 查詢所有待審核的新學生申請數量
    @Query("SELECT COUNT(r) FROM Registration r WHERE r.studentId IS NULL AND r.status = 'PENDING'")
    Integer countPendingNewStudentApplications();
    
    // 查詢所有待審核的現有學生報名數量
    @Query("SELECT COUNT(r) FROM Registration r WHERE r.studentId IS NOT NULL AND r.status = 'PENDING'")
    Integer countPendingExistingStudentRegistrations();
    
    
    /**
     * 分頁查詢報名記錄（帶關聯資料）
     */
    @Query("SELECT r FROM Registration r " +
    	       "WHERE (:courseId IS NULL OR r.courseId = :courseId) " +
    	       "AND (:studentId IS NULL OR r.studentId = :studentId) " +
    	       "AND (:status IS NULL OR r.status = :status) " +
    	       "AND (:isNewStudent IS NULL OR " +
    	       "     (:isNewStudent = true AND r.studentId IS NULL) OR " +
    	       "     (:isNewStudent = false AND r.studentId IS NOT NULL))")
    	Page<Registration> findRegistrationsWithDetails(
    	    @Param("courseId") Integer courseId,
    	    @Param("studentId") Integer studentId,
    	    @Param("status") RegistrationStatus status,
    	    @Param("isNewStudent") Boolean isNewStudent,
    	    Pageable pageable);


    	/**
    	 * COUNT 查詢（用於分頁計算總數）
    	 */
    	@Query("SELECT COUNT(r) FROM Registration r " +
    	       "WHERE (:courseId IS NULL OR r.courseId = :courseId) " +
    	       "AND (:studentId IS NULL OR r.studentId = :studentId) " +
    	       "AND (:status IS NULL OR r.status = :status) " +
    	       "AND (:isNewStudent IS NULL OR " +
    	       "     (:isNewStudent = true AND r.studentId IS NULL) OR " +
    	       "     (:isNewStudent = false AND r.studentId IS NOT NULL))")
    	Long countRegistrationsWithDetails(
    	        @Param("courseId") Integer courseId,
    	        @Param("studentId") Integer studentId,
    	        @Param("status") RegistrationStatus status,
    	        @Param("isNewStudent") Boolean isNewStudent);
}