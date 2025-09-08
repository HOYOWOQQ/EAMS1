package com.eams.Repository.score;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.eams.Entity.score.ExamResult;

public interface ExamResultRepository extends JpaRepository<ExamResult, Integer> {
	

   

    // 依學生姓名與考卷名稱更新（需配合 Service 做更新）
    @Query("SELECT er FROM ExamResult er WHERE er.student.member.name = :stuName AND er.examPaper.name = :paperName")
    ExamResult findByStudentNameAndExamPaperName(@Param("stuName") String stuName,
                                                 @Param("paperName") String paperName);

    // 刪除單筆 by name + paper
    void deleteByStudent_Member_NameAndExamPaper_Name(String studentName, String paperName);

    // 查某學生所有考試結果
    List<ExamResult> findByStudent_Member_Name(String studentName);

    // 依考卷名稱查詢
    List<ExamResult> findByExamPaper_Name(String paperName);

    // 取得所有教師姓名
    @Query("SELECT m.name FROM Teacher t JOIN t.member m WHERE m.role = 'teacher'")
    List<String> findAllTeacherNames();

    // 取得所有考卷名稱
    @Query("SELECT ep.name FROM ExamPaper ep")
    List<String> findAllExamPaperNames();

    // 依老師名字查 ID
    @Query("SELECT m.id FROM Member m WHERE m.name = :name")
    Integer findMemberIdByName(@Param("name") String name);

    // 依考卷名字查 ID
    @Query("SELECT p.id FROM ExamPaper p WHERE p.name = :name")
    Integer findExamPaperIdByName(@Param("name") String name);
    
    //根據學生ID刪除
    //void deleteByStudent_IdAndExamPaper_Name(int studentId, String paperName);
    
        
    @Modifying
    @Query("DELETE FROM ExamResult er WHERE er.student.id = :studentId AND er.examPaper.id = :examPaperId")
    int deleteByStudentIdAndPaperId(@Param("studentId") int studentId, @Param("examPaperId") int examPaperId);
//    //刪除根據學生ID和考卷ID
//    void deleteByStudent_IdAndExamPaper_Id(int studentId, int examPaperId);
    
    @Modifying
    @Query("UPDATE ExamResult er SET er.score = :score, er.rankInClass = :rank, er.reviewedTime = :time WHERE er.student.member.name = :stuName AND er.examPaper.name = :paperName")
    int updateScoreByStudentAndPaper(String stuName, String paperName, int score, int rank, Timestamp time);
    
    
    Optional<ExamResult> findByStudent_IdAndExamPaper_Id(Integer studentId, Integer paperId);
    // 取某張考卷的所有成績，分數高→低
    List<ExamResult> findByExamPaper_IdOrderByScoreDesc(Integer paperId);
    // 重算排名（同張考卷，以成績由高到低）
    @Modifying
    @Query(value = """
        ;WITH Scores AS (
          SELECT id,
                 DENSE_RANK() OVER (PARTITION BY exam_paper_id ORDER BY score DESC) AS rnk
          FROM exam_result WHERE exam_paper_id = :paperId
        )
        UPDATE er SET er.rank_in_class = s.rnk
        FROM exam_result er
        JOIN Scores s ON s.id = er.id
    """, nativeQuery = true)
    void recalcRank(@Param("paperId") Integer paperId);
    
    @Query("select r from ExamResult r where r.student.id = :studentId order by r.createTime desc")
    List<ExamResult> findByStudent_IdOrderByCreateTimeDesc(@Param("studentId") Integer studentId);

    @Query("select r from ExamResult r where r.student.member.name = :name order by r.createTime desc")
    List<ExamResult> findByStudent_Member_NameOrderByCreateTimeDesc(@Param("name") String name);
    
    @Query("select r.examPaper.id from ExamResult r where r.id = :resultId")
    Optional<Integer> findPaperIdByResultId(@Param("resultId") Integer resultId);
    
    ///獎學金相關
    @Query("SELECT r FROM ExamResult r WHERE r.examPaper.id = :examPaperId and r.rankInClass<=3 ORDER BY r.score DESC")
    List<ExamResult> findTop3ByExamPaperIdOrderByScoreDesc(@Param("examPaperId") Integer examPaperId);

   
    boolean existsByStudentIdAndExamPaperId(Integer studentId, Integer examPaperId);
}
