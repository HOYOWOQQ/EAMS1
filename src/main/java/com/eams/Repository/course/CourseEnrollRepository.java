package com.eams.Repository.course;


import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.eams.Entity.course.CourseEnroll;

@Repository
public interface CourseEnrollRepository extends JpaRepository<CourseEnroll, Integer> {

	// 刪除單筆by學生&&課程id
	@Modifying
	@Query("DELETE  FROM CourseEnroll ce WHERE ce.course.id =:courseId AND ce.student.id=:studentId")
	int deleteByCourseIdAndStudentId(@Param("courseId") Integer courseId, @Param("studentId") Integer studentId);

	// 查多少學生註冊
	@Query("SELECT COUNT(DISTINCT ce.student.id) FROM CourseEnroll ce")
	int countDistinctStudent();
	
	@Query("SELECT COUNT(c) FROM Course c")
	int countAllCourse();
	

	// 查詢是否已報名
	@Query("SELECT count(e) FROM CourseEnroll e WHERE e.student.id = :studentId AND e.course.id = :courseId  AND e.status = 'enrolled'")
	int isAlreadyEnrolled(@Param("studentId") Integer studentId ,@Param("courseId") Integer  courseId);
	
	

	// 查註冊數
	@Query("SELECT count(e) FROM CourseEnroll e WHERE e.course.id = :courseId AND e.status = 'enrolled'") 
	int getEnrolledCount(@Param("courseId") Integer courseId);
	
	

	// 查報名截止日
	@Query("SELECT c.registrationEndDate FROM Course c WHERE c.id = :courseId")
    LocalDate getRegistrationEndDate(@Param("courseId") Integer courseId);
	
	boolean existsByCourseIdAndStudentId(Integer courseId, Integer studentId);
	boolean existsByCourse_IdAndStudent_Id(Integer courseId, Integer studentId);
	@Query("select ce.course.id from CourseEnroll ce " +
		       "where ce.student.id = :studentId")
		java.util.List<Integer> findCourseIdsByStudentId(@Param("studentId") Integer studentId);
	
	
	// 查詢特定學生的所有報名紀錄
	@Query("SELECT ce FROM CourseEnroll ce WHERE ce.student.id = :studentId ")
	List<CourseEnroll> findByStudentId(@Param("studentId") Integer studentId);

	// 分页查询
	@Query("SELECT ce FROM CourseEnroll ce WHERE ce.student.id = :studentId ")
	Page<CourseEnroll> findByStudentId(@Param("studentId") Integer studentId, Pageable pageable);

	// 查詢特定學生特定狀態的報名紀錄
	@Query("SELECT ce FROM CourseEnroll ce WHERE ce.student.id = :studentId AND ce.status = :status")
	List<CourseEnroll> findByStudentIdAndStatus(@Param("studentId") Integer studentId, @Param("status") String status);

	// 查詢特定學生已註冊的課程
	@Query("SELECT ce FROM CourseEnroll ce WHERE ce.student.id = :studentId AND ce.status = 'enrolled'")
	List<CourseEnroll> findEnrolledCoursesByStudentId(@Param("studentId") Integer studentId);
}
