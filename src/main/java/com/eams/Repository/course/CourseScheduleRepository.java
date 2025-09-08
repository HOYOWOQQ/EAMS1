package com.eams.Repository.course;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.eams.Entity.course.CourseSchedule;





@Repository
public interface CourseScheduleRepository  extends JpaRepository<CourseSchedule, Integer>,CourseScheduleCustomRepository {
	
	
	List<CourseSchedule> findByCourse_Id(Integer courseId);
	
	List<CourseSchedule> findByClassroom_Id(Integer classroomId);
	
	@Query("SELECT cs FROM CourseSchedule cs JOIN cs.course c JOIN c.courseEnroll ce WHERE ce.student.id = :studentId")
	List<CourseSchedule> findByStudentId(@Param("studentId") Integer studentId);

	
	List<CourseSchedule> findByTeacher_Id(Integer teacherId);
	
	
	@Query("SELECT cs FROM CourseSchedule cs WHERE cs.lessonDate BETWEEN :startDay AND :endDay")
	List<CourseSchedule> findByWeeK(@Param("startDay") LocalDate start ,@Param("endDay") LocalDate end);
	
	//週次總課程數
	@Query("SELECT COUNT(*)  FROM CourseSchedule  cs WHERE cs.lessonDate BETWEEN :startDay AND :endDay")
	int totalCourses(@Param("startDay") LocalDate start ,@Param("endDay") LocalDate end);
	
	//週次授課教師數
	@Query("SELECT COUNT(DISTINCT t.id) FROM CourseSchedule cs JOIN cs.teacher t  WHERE cs.lessonDate BETWEEN :startDay AND :endDay")
	int activeTeachers(@Param("startDay") LocalDate start ,@Param("endDay") LocalDate end);
	
	//週次使用教室數
	@Query("SELECT COUNT(DISTINCT cr.id) FROM CourseSchedule cs JOIN cs.classroom cr  WHERE cs.lessonDate BETWEEN :startDay AND :endDay")
	int activeRooms(@Param("startDay") LocalDate start ,@Param("endDay") LocalDate end);
	
	//週次總上課時數
	@Query("SELECT SUM(cs.periodEnd - cs.periodStart +1) FROM CourseSchedule cs  WHERE cs.lessonDate BETWEEN :startDay AND :endDay")
	Integer sumUsedPeriods(@Param("startDay") LocalDate start ,@Param("endDay") LocalDate end);
	
	//查課程+條件查詢
	 @Query("SELECT cs FROM CourseSchedule cs " +
	           "WHERE cs.lessonDate >= :startDate " +
	           "AND cs.lessonDate <= :endDate " +
	           "AND (:userId IS NULL OR " +
	           "    (cs.teacher.id = :userId OR " +
	           "     EXISTS (SELECT ce FROM CourseEnroll ce WHERE ce.course.id = cs.course.id AND ce.student.id = :userId))) " +
	           "AND (:courseId IS NULL OR cs.course.id = :courseId) " +
	           "AND (:classroomId IS NULL OR cs.classroom.id = :classroomId) " +
	           "ORDER BY cs.lessonDate ASC, cs.periodStart ASC")
	    List<CourseSchedule> findByDateRangeAndFilters(
	        @Param("startDate") LocalDate startDate,
	        @Param("endDate") LocalDate endDate,
	        @Param("userId") Integer userId,
	        @Param("courseId") Integer courseId,
	        @Param("classroomId") Integer classroomId
	    );


	// 檢查教師時間衝突
	 @Query("SELECT cs FROM CourseSchedule cs " +
	        "WHERE cs.teacher.id = :teacherId " +
	        "AND cs.lessonDate = :lessonDate " +
	        "AND NOT (cs.periodEnd < :periodStart OR cs.periodStart > :periodEnd)")
	 List<CourseSchedule> findTeacherTimeConflicts(
	     @Param("teacherId") Integer teacherId,
	     @Param("lessonDate") LocalDate lessonDate,
	     @Param("periodStart") Integer periodStart,
	     @Param("periodEnd") Integer periodEnd
	 );

	 // 檢查教室時間衝突
	 @Query("SELECT cs FROM CourseSchedule cs " +
	        "WHERE cs.classroom.id = :classroomId " +
	        "AND cs.lessonDate = :lessonDate " +
	        "AND NOT (cs.periodEnd < :periodStart OR cs.periodStart > :periodEnd)")
	 List<CourseSchedule> findClassroomTimeConflicts(
	     @Param("classroomId") Integer classroomId,
	     @Param("lessonDate") LocalDate lessonDate,
	     @Param("periodStart") Integer periodStart,
	     @Param("periodEnd") Integer periodEnd
	 );

	 // 檢查課程時間衝突（避免同一課程重複排課）
	 @Query("SELECT cs FROM CourseSchedule cs " +
	        "WHERE cs.course.id = :courseId " +
	        "AND cs.lessonDate = :lessonDate " +
	        "AND NOT (cs.periodEnd < :periodStart OR cs.periodStart > :periodEnd)")
	 List<CourseSchedule> findCourseTimeConflicts(
	     @Param("courseId") Integer courseId,
	     @Param("lessonDate") LocalDate lessonDate,
	     @Param("periodStart") Integer periodStart,
	     @Param("periodEnd") Integer periodEnd
	 );

	 // 排除自己的檢查（用於更新時）
	 @Query("SELECT cs FROM CourseSchedule cs " +
	        "WHERE cs.teacher.id = :teacherId " +
	        "AND cs.lessonDate = :lessonDate " +
	        "AND cs.id != :excludeId " +
	        "AND NOT (cs.periodEnd < :periodStart OR cs.periodStart > :periodEnd)")
	 List<CourseSchedule> findTeacherTimeConflictsExcluding(
	     @Param("teacherId") Integer teacherId,
	     @Param("lessonDate") LocalDate lessonDate,
	     @Param("periodStart") Integer periodStart,
	     @Param("periodEnd") Integer periodEnd,
	     @Param("excludeId") Integer excludeId
	 );

}
