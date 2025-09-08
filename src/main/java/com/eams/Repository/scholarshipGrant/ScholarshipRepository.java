package com.eams.Repository.scholarshipGrant;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.eams.Entity.fee.DTO.ExpenseRowDTO;
import com.eams.Entity.scholarship.ScholarshipGrant;
import com.eams.Entity.scholarship.ScholarshipGrantDTO;

public interface ScholarshipRepository extends JpaRepository<ScholarshipGrant, Integer> {

	 // 查全部，並帶出學生名稱（投影為 DTO）
    @Query("SELECT new com.eams.Entity.scholarship.ScholarshipGrantDTO(" +
           "g.id, g.title, g.amount, g.status, g.grantTime, s.name,ep.name) " +
           "FROM ScholarshipGrant g " +
           "LEFT JOIN g.examResult er " +
           "LEFT JOIN er.examPaper ep " +
           "LEFT JOIN g.studentAccount s order by g.grantTime desc")
    List<ScholarshipGrantDTO> findAllWithStudentName();
	
	
	 // 可選：查某位學生的獎學金
    @Query("SELECT new com.eams.Entity.scholarship.ScholarshipGrantDTO(" +
           "g.id, g.title, g.amount, g.status, g.grantTime, s.name,ep.name, m.name) " +
           "FROM ScholarshipGrant g " +
           "LEFT JOIN g.studentAccount s " +
           "LEFT JOIN g.examResult er " +
           "LEFT JOIN er.examPaper ep " +
           "LEFT JOIN g.createdBy m " +
           "WHERE s.id = :studentId order by g.grantTime desc")
    List<ScholarshipGrantDTO> findByStudentId(Integer studentId);


	boolean existsByStudentInfo_IdAndExamResult_Id(Integer id, Integer examPaperId);


	boolean existsByExamResult_Id(Integer examResultId);
	
	
	//===========revenue相關===========//
	// 期間內「有效支出」總額（排除 revoked）
    @Query("""
      SELECT COALESCE(SUM(CAST(g.amount AS int)),0)
      FROM ScholarshipGrant g
      WHERE (g.status <> 'revoked')
        AND (:start IS NULL OR g.grantTime >= :start)
        AND (:end   IS NULL OR g.grantTime <= :end)
    """)
    Long sumExpenseInRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    // 支出明細
    @Query("""
      SELECT new com.eams.Entity.fee.DTO.ExpenseRowDTO(
        g.id, s.id, s.name,
        ep.name, g.title, g.amount, g.status, g.grantTime
      )
      FROM ScholarshipGrant g
      JOIN g.studentAccount s
      LEFT JOIN g.examResult r
      LEFT JOIN r.examPaper ep
      WHERE (:start IS NULL OR g.grantTime >= :start)
        AND (:end   IS NULL OR g.grantTime <= :end)
    """)
    Page<ExpenseRowDTO> findExpenseRows(@Param("start") LocalDateTime start,
                                        @Param("end") LocalDateTime end,
                                        Pageable pageable);

    // 期間內所有有效支出（服務層做月度序列）
    @Query("""
      SELECT g FROM ScholarshipGrant g
      WHERE (g.status <> 'revoked')
        AND (:start IS NULL OR g.grantTime >= :start)
        AND (:end   IS NULL OR g.grantTime <= :end)
    """)
    List<ScholarshipGrant> findAllEffectiveInRange(@Param("start") LocalDateTime start,
                                                   @Param("end") LocalDateTime end);
	
	
	
}
