package com.eams.Repository.wallet;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.eams.Entity.member.Student;
import com.eams.Entity.wallet.StudentWalletTransaction;
import com.eams.Entity.wallet.WalletTransactionDTO;

import jakarta.persistence.LockModeType;

import java.time.LocalDateTime;


public interface StudentWalletTransactionRepository extends JpaRepository<StudentWalletTransaction, Integer> {
    
	
	List<StudentWalletTransaction> findByStudentInfo_Id(Integer studentId);
	
	// 1) 餘額：以流水加總（正負互抵）
	@Query("SELECT COALESCE(SUM(swt.amount), 0) FROM StudentWalletTransaction swt WHERE swt.studentInfo.id = :studentId")
	Integer getWalletBalanceByStudentId(@Param("studentId") Integer studentId);
	
	@Query("SELECT COALESCE(SUM(swt.amount), 0) FROM StudentWalletTransaction swt")
	Integer getWalletBalanceByALl();
	
	
	boolean existsByStudentInfo_IdAndSourceTypeAndSourceIdAndDescription(
            Integer studentId, String sourceType, Integer sourceId, String description);
	
	
	@Query("select new com.eams.Entity.wallet.WalletTransactionDTO( t.studentInfo.id,"
			+ "t.amount,"
			+ "t.description,"
			+ "t.sourceType,"
			+ "t.sourceId,"
			+ "t.createdAt,"
			+ "m.name,"
			+ "t.balanceAfter,"
			+ "t.type,"
			+ "op.name, "
			+ "op.id) "
			+ "from StudentWalletTransaction t "
			+ "Left join t.studentInfo ts " 
			+ "Left join ts.member m " 
			+ "left join t.createdBy op "
			+ "where t.studentInfo.id = :sid order by t.createdAt desc")
		List<WalletTransactionDTO> findWalletTxByStudent(@Param("sid") Integer studentId);
	
	@Query("select new com.eams.Entity.wallet.WalletTransactionDTO( "
			+ "t.studentInfo.id,"
			+ "t.amount,"
			+ "t.description,"
			+ "t.sourceType,"
			+ "t.sourceId,"
			+ "t.createdAt,"
			+ "m.name,"
			+ "t.balanceAfter,"
			+ "t.type,"
			+ "op.name, "
			+ "op.id ) "
			+ "from StudentWalletTransaction t	"
			+ "Left join t.studentInfo ts " 
			+ "Left join ts.member m " 
			+ "left join t.createdBy op "
			+ "order by t.createdAt desc")
		List<WalletTransactionDTO> findWalletTxAll();
	

	
//	<=============錢包後台使用==============>
	// 按 type 分組統計加總
    @Query("""
        SELECT t.type, COALESCE(SUM(t.amount), 0)
        FROM StudentWalletTransaction t
        WHERE (:start IS NULL OR t.createdAt >= :start)
          AND (:end IS NULL OR t.createdAt <= :end)
        GROUP BY t.type
    """)
    List<Object[]> sumByType(@Param("start") LocalDateTime start,
                             @Param("end") LocalDateTime end);

    // 平台總餘額（所有交易金額加總）
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM StudentWalletTransaction t")
    Long totalBalanceAll();

    
//    /** 取得該生「最新一筆交易」，並加上悲觀寫鎖，避免同時加值造成競爭 */
//    @Lock(LockModeType.PESSIMISTIC_WRITE)
//    @Query("""
//        SELECT t FROM StudentWalletTransaction t
//        WHERE t.studentInfo.id = :sid
//        ORDER BY t.createdAt DESC, t.id DESC
//    """)
//    Optional<StudentWalletTransaction> findLastTxForUpdateOrderByCreatedAtDescIdDesc(@Param("sid") Integer studentId);

    //冪等檢查（不含 description，避免因文字不同而重覆入帳）
	boolean existsByStudentInfo_IdAndSourceTypeAndSourceIdAndType(Integer studentId, String string, Integer sourceId,
			String string2);

	/** 取得該生「最新一筆交易」，並加上悲觀寫鎖，避免同時加值造成競爭 */
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	Optional<StudentWalletTransaction> findFirstByStudentInfo_IdOrderByCreatedAtDescIdDesc(Integer studentId);
    
    
    
}
