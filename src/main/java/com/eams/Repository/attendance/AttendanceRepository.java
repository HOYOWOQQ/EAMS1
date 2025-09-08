package com.eams.Repository.attendance;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.eams.Entity.attendance.Attendance;
import com.eams.Entity.member.Student;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Integer>, JpaSpecificationExecutor<Attendance> {

	List<Attendance> findByStudentId(Integer studentId);

	@Query("SELECT COUNT(a) FROM Attendance a WHERE a.status = :status")
	long countByStatus(@Param("status") String status);

	@Query("SELECT a FROM Attendance a " + "WHERE a.student.id = :studentId "
			+ "AND (:courseId IS NULL OR a.courseSchedule.course.id = :courseId) "
			+ "AND (:lessonDate IS NULL OR a.courseSchedule.lessonDate = :lessonDate)")
	List<Attendance> findByStudentIdAndConditions(@Param("studentId") Integer studentId,
			@Param("courseId") Integer courseId, @Param("lessonDate") LocalDate lessonDate);

	/**
	 * 查詢學生的缺席記錄（且未申請請假）
	 */
	List<Attendance> findByStudent_IdAndStatusAndLeaveRequestIsNull(Integer studentId, String status);

	/**
	 * 根據課程ID查詢所有學生（用於建立出勤記錄）
	 */
	@Query("SELECT ce.student FROM CourseEnroll ce WHERE ce.course.id = :courseId")
	List<Student> findStudentsByCourseId(@Param("courseId") Integer courseId);

	/**
	 * 查詢特定條件的出勤記錄（用於請假時找到對應記錄）
	 */
	@Query("SELECT a FROM Attendance a " + "WHERE a.student.id = :studentId "
			+ "AND a.courseSchedule.course.id = :courseId " + "AND a.courseSchedule.lessonDate = :lessonDate "
			+ "AND a.courseSchedule.periodStart = :periodStart")
	Optional<Attendance> findTargetAttendance(@Param("studentId") Integer studentId,
			@Param("courseId") Integer courseId, @Param("lessonDate") LocalDate lessonDate,
			@Param("periodStart") Integer periodStart);

	/**
	 * 批量查詢多個ID的出勤記錄
	 */
	List<Attendance> findAllById(Iterable<Integer> ids);

	 @Query("""
		        select a
		        from Attendance a
		        where a.courseSchedule.id = :csId
		          and a.courseSchedule.lessonDate = :date
		    """)
		    List<Attendance> findByScheduleAndDate(@Param("csId") Integer csId,
		                                           @Param("date") LocalDate date);

		    @Query("""
		        select a
		        from Attendance a
		        where a.courseSchedule.id = :csId
		          and a.courseSchedule.lessonDate = :date
		          and a.student.id in :studentIds
		    """)
		    List<Attendance> findByScheduleAndDateAndStudentIn(@Param("csId") Integer csId,
		                                                       @Param("date") LocalDate date,
		                                                       @Param("studentIds") Collection<Integer> studentIds);
		    @Query("""
		    		  select a
		    		  from Attendance a
		    		  where a.id in :ids
		    		    and a.courseSchedule.id = :csId
		    		    and a.courseSchedule.lessonDate = :date
		    		""")
		    		List<Attendance> findByIdsAndScheduleAndDate(
		    		    @Param("ids") Collection<Integer> ids,
		    		    @Param("csId") Integer csId,
		    		    @Param("date") LocalDate date
		    		);
	/*
	 * @Query(""" SELECT new com.eams.Entity.attendance.DTO.TrendPointDTO(
	 * CONCAT(MONTH(a.lessonDate), '月'), SUM(CASE WHEN a.status = 'ATTEND' THEN 1
	 * ELSE 0 END), COUNT(a.id) ) FROM Attendance a WHERE YEAR(a.lessonDate) = :year
	 * AND (:courseId IS NULL OR a.courseSchedule.course.id = :courseId) GROUP BY
	 * MONTH(a.lessonDate) ORDER BY MIN(a.lessonDate) """) List<TrendPointDTO>
	 * aggregateByMonth(@Param("year") int year, @Param("courseId") Integer
	 * courseId);
	 * 
	 * @Query(""" SELECT new com.eams.Entity.attendance.DTO.TrendPointDTO(
	 * CONCAT('第', (DATEDIFF(day, DATEFROMPARTS(:year, :month, 1), a.lessonDate) /
	 * 7) + 1, '週'), SUM(CASE WHEN a.status = 'ATTEND' THEN 1 ELSE 0 END),
	 * COUNT(a.id) ) FROM Attendance a WHERE YEAR(a.lessonDate) = :year AND
	 * MONTH(a.lessonDate) = :month AND (:courseId IS NULL OR
	 * a.courseSchedule.course.id = :courseId) GROUP BY (DATEDIFF(day,
	 * DATEFROMPARTS(:year, :month, 1), a.lessonDate) / 7) ORDER BY
	 * MIN(a.lessonDate) """) List<TrendPointDTO> aggregateByWeek(@Param("year") int
	 * year, @Param("month") int month,
	 * 
	 * @Param("courseId") Integer courseId);
	 * 
	 * @Query(""" SELECT new com.eams.Entity.attendance.DTO.TrendPointDTO(
	 * CAST(DAY(a.lessonDate) AS string), SUM(CASE WHEN a.status = 'ATTEND' THEN 1
	 * ELSE 0 END), COUNT(a.id) ) FROM Attendance a WHERE YEAR(a.lessonDate) = :year
	 * AND MONTH(a.lessonDate) = :month AND (:courseId IS NULL OR
	 * a.courseSchedule.course.id = :courseId) GROUP BY DAY(a.lessonDate) ORDER BY
	 * MIN(a.lessonDate) """) List<TrendPointDTO> aggregateByDay(@Param("year") int
	 * year, @Param("month") int month,
	 * 
	 * @Param("courseId") Integer courseId);
	 */
	/**
	 * 依【月】彙總 以 course_schedule 的 lesson_date 當作日期來源
	 */
	@Query(value = """
			SELECT
			  CAST(MONTH(cs.lesson_date) AS INT) AS monthNo,
			  SUM(CASE WHEN a.status = 'ATTEND' THEN 1 ELSE 0 END) AS presentCount,
			  COUNT(*) AS totalCount
			FROM attendance a
			JOIN course_schedule cs ON cs.id = a.course_schedule_id
			WHERE YEAR(cs.lesson_date) = :year
			  AND (:courseId IS NULL OR cs.course_id = :courseId)
			GROUP BY MONTH(cs.lesson_date)
			ORDER BY MONTH(cs.lesson_date)
			""", nativeQuery = true)
	List<Object[]> aggregateByMonthNative(@Param("year") int year, @Param("courseId") Integer courseId);

	/**
	 * 依【週】彙總 以「該月1號起每 7 天為一週」的簡化分週（weekNo=1..6）
	 */
	@Query(value = """
			WITH base AS (
			  SELECT
			    ((DATEDIFF(day, DATEFROMPARTS(:year, :month, 1), cs.lesson_date) / 7) + 1) AS weekNo,
			    a.status,
			    cs.lesson_date
			  FROM attendance a
			  JOIN course_schedule cs ON cs.id = a.course_schedule_id
			  WHERE YEAR(cs.lesson_date) = :year
			    AND MONTH(cs.lesson_date) = :month
			    AND (:courseId IS NULL OR cs.course_id = :courseId)
			)
			SELECT
			  CAST(weekNo AS INT) AS weekNo,
			  SUM(CASE WHEN status = 'ATTEND' THEN 1 ELSE 0 END) AS presentCount,
			  COUNT(*) AS totalCount
			FROM base
			GROUP BY weekNo
			ORDER BY weekNo
			""", nativeQuery = true)
	List<Object[]> aggregateByWeekNative(@Param("year") int year, @Param("month") int month,
			@Param("courseId") Integer courseId);

	/**
	 * 依【日】彙總
	 */
	@Query(value = """
			SELECT
			  CAST(DAY(cs.lesson_date) AS INT) AS dayNo,
			  SUM(CASE WHEN a.status = 'ATTEND' THEN 1 ELSE 0 END) AS presentCount,
			  COUNT(*) AS totalCount
			FROM attendance a
			JOIN course_schedule cs ON cs.id = a.course_schedule_id
			WHERE YEAR(cs.lesson_date) = :year
			  AND MONTH(cs.lesson_date) = :month
			  AND (:courseId IS NULL OR cs.course_id = :courseId)
			GROUP BY DAY(cs.lesson_date)
			ORDER BY DAY(cs.lesson_date)
			""", nativeQuery = true)
	List<Object[]> aggregateByDayNative(@Param("year") int year, @Param("month") int month,
			@Param("courseId") Integer courseId);

	// 依【有出缺勤紀錄】的課程清單（year 必填，month 可選）
	@Query("""
			  select c.id as id, c.name as name
			  from Attendance a
			  join a.courseSchedule cs
			  join cs.course c
			  where function('YEAR', cs.lessonDate) = :year
			    and (:month is null or function('MONTH', cs.lessonDate) = :month)
			  group by c.id, c.name
			  order by c.name
			""")
	List<CourseOptionView> findCoursesForAnalytics(@Param("year") int year, @Param("month") Integer month);

}