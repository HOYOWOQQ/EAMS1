package com.eams.Repository.score;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import com.eams.Entity.score.ExamPaper;

public interface ExamPaperRepository extends JpaRepository<ExamPaper, Integer> {

	// 
    List<ExamPaper> findByCourse_IdOrderByExamDateDesc(int courseId);

    List<ExamPaper> findByExamTypeOrderByExamDateDesc(String examType);

    // 
    List<ExamPaper> findByCourse_IdAndExamTypeOrderByExamDateDesc(int courseId, String examType);

    List<ExamPaper> findByExamDateBetweenOrderByExamDateAsc(Date startDate, Date endDate);

    @Query("SELECT DISTINCT ep.name FROM ExamPaper ep ORDER BY ep.name")
    List<String> findAllDistinctExamPaperNames();

    @Query("SELECT DISTINCT ep.name FROM ExamPaper ep JOIN ep.examResults er ORDER BY ep.name")
    List<String> findExamPaperNamesWithResults();

    @Query("SELECT ep.name, COUNT(er) FROM ExamPaper ep JOIN ep.examResults er GROUP BY ep.name ORDER BY ep.name")
    List<Object[]> findExamPaperNameWithResultCount();

    @Query("SELECT e FROM ExamPaper e JOIN FETCH e.course")
    List<ExamPaper> findAllWithCourse();
    
    int deleteByCourse_Id(int courseId);

    int deleteByExamType(String examType);

    int deleteByExamDateBetween(Date start, Date end);
    
    @Query("SELECT DISTINCT e.name FROM ExamPaper e")
    List<String> findAllDistinctExamPaperNames2();
    
    // 學生是否能存取某張考卷：該考卷所屬 course 學生有選修
    @Query(value = """
        SELECT CASE WHEN COUNT(*)>0 THEN 1 ELSE 0 END
          FROM exam_paper ep
          JOIN course_enroll ce ON ce.course_id = ep.course_id
         WHERE ep.id = :paperId
           AND ce.student_id = :studentId
        """, nativeQuery = true)
    boolean existsPaperForStudent(@Param("paperId") Integer paperId,
                                  @Param("studentId") Integer studentId);
    
    List<ExamPaper> findByCourse_IdIn(Collection<Integer> courseIds);
    // 老師是否能存取：該考卷所屬的科目在 course_subject 內，且該 subject 綁定到這位 teacher
    @Query(value = """
        SELECT CASE WHEN COUNT(*)>0 THEN 1 ELSE 0 END
          FROM exam_paper ep
          JOIN course_subject cs ON cs.course_id = ep.course_id
         WHERE ep.id = :paperId
           AND cs.subject_id IN (
                SELECT ct.subject_id
                  FROM course_teacher ct
                 WHERE ct.teacher_id = :teacherId
           )
        """, nativeQuery = true)
    boolean existsPaperForTeacher(@Param("paperId") Integer paperId,
                                  @Param("teacherId") Integer teacherId);
}
