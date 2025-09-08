package com.eams.Repository.course;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.eams.Entity.course.Subject;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Integer> {

	List<Subject> findByCourses_Id(Integer courseId);

	Optional<Subject> findByName(String name);

	// 查詢特定課程的所有科目
	@Query("SELECT s FROM Subject s JOIN s.courses c WHERE c.id = :courseId")
	List<Subject> findSubjectsByCourseId(@Param("courseId") Integer courseId);

	// 查詢不屬於特定課程的科目
	@Query("SELECT s FROM Subject s WHERE s.id NOT IN (SELECT DISTINCT subject.id FROM Subject subject JOIN subject.courses c WHERE c.id = :courseId)")
	List<Subject> findSubjectsNotInCourse(@Param("courseId") Integer courseId);
	
	
	@Query("SELECT s FROM Subject s WHERE s.name = :name")
	Subject findSubjectByNameOrThrow(@Param("name") String name);

}
