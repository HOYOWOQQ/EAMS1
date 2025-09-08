package com.eams.Repository.fee;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.eams.Entity.fee.PaymentNotice;
import com.eams.Entity.fee.DTO.FindpaymentnoticeDTO;
import com.eams.Entity.fee.DTO.IncomeRowDTO;
import com.eams.Entity.fee.DTO.RefundRowDTO;

import jakarta.persistence.LockModeType;


//DAO角色 提供基本CRUD  
public interface PaymentNoticeRepository extends JpaRepository<PaymentNotice, Integer> {

	Optional<PaymentNotice> findEntityByNoticeNo(String noticeNo);
	
	
	//查詢所有通知單的資料(給前端好用)
	@Query("SELECT new com.eams.Entity.fee.DTO.FindpaymentnoticeDTO(" +
		       "p.id, p.noticeNo, s.id, s.name, c.id, c.name, " +
		       "p.netAmount, p.discount, p.startDate, p.endDate, " +
		       "p.payDate, p.payStatus, p.remark, p.walletUsedAmount,p.createdAt,p.isVoided,p.voidReason) " +
		       "FROM PaymentNotice p " +
		       "JOIN p.studentAccount s " +
		       "JOIN p.course c " +
				"order by p.createdAt desc")
	public List<FindpaymentnoticeDTO> getAllpaymentNotice();
	
	//透過學生id查詢通知單的資料(給前端好用)
	@Query("SELECT new com.eams.Entity.fee.DTO.FindpaymentnoticeDTO(" +
			"p.id, p.noticeNo, s.id, s.name, c.id, c.name, " +
			"p.netAmount, p.discount, p.startDate, p.endDate, " +
			"p.payDate, p.payStatus, p.remark, p.walletUsedAmount,p.createdAt) " +
			"FROM PaymentNotice p " +
			"JOIN p.studentAccount s " +
			"JOIN p.course c " +
			"WHERE p.isVoided = false and  s.id= :id  "+
			"order by p.createdAt desc")
	public List<FindpaymentnoticeDTO> getpaymentNoticeByStudentId(@Param("id") Integer id);
	
	//透過學生姓名查詢通知單的資料(給前端用)
	@Query("SELECT new com.eams.Entity.fee.DTO.FindpaymentnoticeDTO("
			+ "p.noticeNo, s.name, c.name, p.netAmount, p.payDate, p.payStatus, p.remark ,p.walletUsedAmount,p.createdAt) "
			+ "FROM PaymentNotice p "
			+ "JOIN  p.studentAccount s "
			+ "JOIN  p.course c  WHERE s.name LIKE %:name% and p.isVoided = false ")
	public List<FindpaymentnoticeDTO> findDTOByStudentName(@Param("name") String name); 
	
	
	//前端 按修改 會回傳id .findById()取得物件資料  .save(物件)
	
	
	
  //後端 新增 可以用 .save() 新增一筆 通知單 .saveAll()新增一筆 細項
	
	
	//刪除改寫 軟刪除
	@Modifying
	@Query("UPDATE PaymentNotice p SET p.isVoided = true ,p.voidReason = :reason, p.voidDate = CURRENT_DATE WHERE p.id = :id")
	public int softDelete(@Param("id") Integer id,@Param("reason") String reason);
	
	
	@Query("SELECT MAX(p.noticeNo) FROM PaymentNotice p WHERE p.noticeNo LIKE CONCAT(:prefix, '-%')")
	public String findMaxNoticeNoLike(@Param("prefix") String prefix);

	
	
	@Query("SELECT new com.eams.Entity.fee.DTO.FindpaymentnoticeDTO(" +
		       "p.id, p.noticeNo, s.id, s.name, c.id, c.name, " +
		       "p.netAmount, p.discount, p.startDate, p.endDate, " +
		       "p.payDate, p.payStatus, p.remark ,p.walletUsedAmount,p.createdAt) " +
		       "FROM PaymentNotice p " +
		       "JOIN p.studentAccount s " +
		       "JOIN p.course c " +
		       "WHERE  p.noticeNo =  :noticeNo")
	public Optional<FindpaymentnoticeDTO> findByNoticeNo(@Param("noticeNo") String noticeNo);
	
	
	@Query("SELECT new com.eams.Entity.fee.DTO.FindpaymentnoticeDTO("+
	          "p.id, p.noticeNo, s.id, s.name, c.id, c.name, "+ 
			  "p.netAmount, p.discount, p.startDate, p.endDate, "+ 
			  "p.payDate, p.payStatus, p.remark ,p.walletUsedAmount,p.createdAt)"
			  + "FROM PaymentNotice p "
			  + "JOIN p.studentAccount s "
			  +"JOIN p.course c "
			  + "WHERE  p.noticeNo =  :noticeNo and s.id= :studentId")
	public Optional<FindpaymentnoticeDTO> findByNoticeNoAndStudentId(@Param("noticeNo") String noticeNo,@Param("studentId") Integer studentId);
	
	
	@Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM PaymentNotice p WHERE p.id = :id")
    Optional<PaymentNotice> findByIdForUpdate(@Param("id") Integer id);
	
	
	
//===========revenue相關===========//	
	
//	 退費學費總額（依 payDate 範圍）
	@Query("""
		    SELECT COALESCE(SUM(CAST(p.netAmount AS int)),0)
		    FROM PaymentNotice p
		    WHERE p.payStatus = 'refunded'
		      AND (:start IS NULL OR p.voidDate >= :start)
		      AND (:end   IS NULL OR p.voidDate <= :end)
		    """)
		Long sumRefundInRange(@Param("start") LocalDate start,
		                      @Param("end") LocalDate end);
	
	
	
	 // 已收學費總額（依 payDate 範圍）
    @Query("""
SELECT COALESCE(SUM(CAST(p.netAmount AS int)),0)
      FROM PaymentNotice p
      WHERE p.payStatus = 'paid'
        AND (:start IS NULL OR p.payDate >= :start)
        AND (:end   IS NULL OR p.payDate <= :end)
    """)
    Long sumPaidInRange(@Param("start") LocalDate start, @Param("end") LocalDate end);

    // 應收（未繳）金額（用 createdAt 範圍；若你想過濾期間的「已開立單據」）
    @Query("""
      SELECT COALESCE(SUM(CAST(p.netAmount AS int)),0)
      FROM PaymentNotice p
      WHERE p.payStatus IN ('unpaid','partial')
        AND (:startCreated IS NULL OR p.createdAt >= :startCreated)
        AND (:endCreated   IS NULL OR p.createdAt <= :endCreated)
    """)
    Long sumReceivableInCreatedRange(@Param("startCreated") LocalDateTime startCreated,
                                     @Param("endCreated") LocalDateTime endCreated);

    // 收入明細（最近 N 筆）
    @Query("""
    		  SELECT new com.eams.Entity.fee.DTO.IncomeRowDTO(
    		    p.id, p.noticeNo, s.id, s.name, c.name, null, p.netAmount, p.payStatus, p.payDate
    		  )
    		  FROM PaymentNotice p
    		  JOIN p.studentAccount s
    		  LEFT JOIN p.course c
    		  WHERE p.payStatus IN ('paid')
    		    AND (:start IS NULL OR p.payDate >= :start)
    		    AND (:end   IS NULL OR p.payDate <=  :end)
    		""")
    		Page<IncomeRowDTO> findIncomeRows(@Param("start") LocalDate start,
    		                                  @Param("end") LocalDate end,
    		                                  Pageable pageable);
    
   
 // 退費明細（最近 N 筆）
    @Query("""
    		  SELECT new com.eams.Entity.fee.DTO.RefundRowDTO(
    		    p.id, p.noticeNo, s.id, s.name, c.name, null, p.netAmount, p.payStatus, p.payDate, p.voidReason
    		  )
    		  FROM PaymentNotice p
    		  JOIN p.studentAccount s
    		  LEFT JOIN p.course c
    		  WHERE p.payStatus = 'refunded'
    		    AND (:start IS NULL OR p.payDate >= :start)
    		    AND (:end   IS NULL OR p.payDate <=  :end)
    		""")
    		Page<RefundRowDTO> findRefundRows(@Param("start") LocalDate start,
    		                                  @Param("end") LocalDate end,
    		                                  Pageable pageable);
    
    
    
    

    // 依「課程類型」做收入分佈（只計 paid）
//    @Query(value = """
//    		  SELECT c.type AS label,
//    		         SUM(TRY_CONVERT(BIGINT, p.net_amount)) AS amount
//    		  FROM payment_notice p
//    		  LEFT JOIN course c ON c.id = p.course_id
//    		  WHERE p.pay_status = 'paid'
//    		    AND (:start IS NULL OR p.pay_date >= :start)
//    		    AND (:end   IS NULL OR p.pay_date <=  :end)
//    		  GROUP BY c.type
//    		  ORDER BY amount DESC
//    		""", nativeQuery = true)
//    @Query("""
//          SELECT c.type, COALESCE(SUM(CAST(p.netAmount AS int)),0)
//          FROM PaymentNotice p
//          LEFT JOIN p.course c
//          WHERE p.payStatus = 'paid'
//            AND (:start IS NULL OR p.payDate >= :start)
//            AND (:end   IS NULL OR p.payDate <= :end)
//          GROUP BY c.type
//          ORDER BY COALESCE(SUM(CAST(p.netAmount AS int)) DESC
//        """)    		List<Object[]> sumPaidByCourseTypeNative(@Param("start") LocalDate start,
//    		                                             @Param("end") LocalDate end);
//    
    
    
    
    @Query("""
      SELECT c.type, COALESCE(SUM(CAST(p.netAmount AS int)),0)
      FROM PaymentNotice p
      LEFT JOIN p.course c
      WHERE p.payStatus = 'paid'
        AND (:start IS NULL OR p.payDate >= :start)
        AND (:end   IS NULL OR p.payDate <= :end)
      GROUP BY c.type
      ORDER BY COALESCE(SUM(CAST(p.netAmount AS int)),0) DESC
    """)
    List<Object[]> sumPaidByCourseType(@Param("start") LocalDate start, @Param("end") LocalDate end);

        
    // 取出期間內所有已收單（給 Service 彙整成月度序列）
    @Query("""
      SELECT p FROM PaymentNotice p
      WHERE p.payStatus='paid'
        AND (:start IS NULL OR p.payDate >= :start)
        AND (:end   IS NULL OR p.payDate <= :end)
    """)
    List<PaymentNotice> findAllPaidInRange(@Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("SELECT p FROM PaymentNotice p " +
    	       "WHERE p.payStatus = 'refunded' " +
    	       "AND (:start IS NULL OR p.payDate >= :start) " +
    	       "AND (:end IS NULL OR p.payDate <= :end)")
    	List<PaymentNotice> findAllRefundedInRange(LocalDate start, LocalDate end);
    
    

}
	
	
	

