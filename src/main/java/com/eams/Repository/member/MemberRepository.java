package com.eams.Repository.member;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.eams.Entity.member.Member;

@Repository
public interface MemberRepository extends JpaRepository<Member, Integer> {

    // ===== 原有的方法 =====
    
    // 根據帳號查詢會員
    Member findMemberByAccount(String account);
    
    // 會員登入驗證 (舊版本，建議改用 Spring Security)
    Member findByAccountAndPassword(String account, String password);

    // 關鍵字搜尋會員
    @Query("FROM Member WHERE name LIKE %:keyword% OR account LIKE %:keyword% OR email LIKE %:keyword%")
    List<Member> search(@Param("keyword") String keyword);
    
    // 根據角色查詢會員
    List<Member> findByRole(String role);
    
    // 根據 Email Token 取得會員 ID
    @Query("SELECT m.id FROM Member m WHERE m.emailToken = :token AND m.tokenExpiry > CURRENT_TIMESTAMP")
    Optional<Integer> getIdByEmailToken(@Param("token") String token);
    
    // 檢查重設密碼 Token 是否有效
    @Query("SELECT COUNT(m) > 0 FROM Member m WHERE m.id = :id AND m.resetToken = :token AND m.resetExpiry > CURRENT_TIMESTAMP")
    boolean checkResetToken(@Param("id") int id, @Param("token") String token);

    // 根據角色和姓名查詢會員
    @Query("SELECT m FROM Member m LEFT JOIN m.student s WHERE " +
            "(:role IS NULL OR m.role = :role) AND " +
            "(:grade IS NULL OR (s.grade = :grade AND m.role = 'student')) AND " +
            "(:status IS NULL OR m.status = :status) AND " +
            "(:verified IS NULL OR m.verified = :verified) AND " +
            "(:name IS NULL OR m.name LIKE %:name%)")
    List<Member> findByCondition(@Param("role") String role,
                                @Param("grade") String grade,
                                @Param("status") Boolean status,
                                @Param("verified") Boolean verified,
                                @Param("name") String name);
    
    Optional<Member> findByEmailTokenAndTokenExpiryAfter(String token, LocalDateTime now);
    
    Optional<Member> findByResetTokenAndResetExpiryAfter(String token, LocalDateTime now);
    
    Optional<Member> findByEmail(String email);
    
    List<Member> findByStatus(boolean status);

    // ===== Spring Security 需要的方法 =====
    
    /**
     * 根據帳號查詢會員 (Spring Security 用)
     * 回傳 Optional 以避免 null 問題
     */
    Optional<Member> findByAccount(String account);
    
    /**
     * 根據帳號和狀態查詢會員
     */
    Optional<Member> findByAccountAndStatusTrue(String account);
    
    /**
     * 根據帳號、狀態和驗證狀態查詢會員
     */
    Optional<Member> findByAccountAndStatusTrueAndVerifiedTrue(String account);

    /**
     * 檢查帳號是否存在
     */
    boolean existsByAccount(String account);
    
    /**
     * 檢查信箱是否存在
     */
    boolean existsByEmail(String email);

    /**
     * 查詢所有啟用的會員
     */
    List<Member> findByStatusTrue();
    
    /**
     * 查詢所有已驗證的會員
     */
    List<Member> findByVerifiedTrue();

    /**
     * 根據角色和狀態查詢會員
     */
    List<Member> findByRoleAndStatusTrue(String role);
    

    // ===== 通知系統需要的方法 =====
    
    /**
     * 根據會員 ID 列表查詢手機號碼
     */
    @Query("SELECT m.phone FROM Member m WHERE m.id IN :memberIds AND m.phone IS NOT NULL AND m.status = true")
    List<String> findPhoneNumbersByIds(@Param("memberIds") List<Integer> memberIds);
    
    /**
     * 根據會員 ID 列表查詢郵箱地址
     */
    @Query("SELECT m.email FROM Member m WHERE m.id IN :memberIds AND m.email IS NOT NULL AND m.status = true")
    List<String> findEmailsByIds(@Param("memberIds") List<Integer> memberIds);
    
    /**
     * 根據角色列表查詢手機號碼
     */
    @Query(value = "SELECT DISTINCT m.phone " +
            "FROM member m " +
            "JOIN member_roles mr ON m.id = mr.member_id " +
            "JOIN roles r ON mr.role_id = r.id " +
            "WHERE r.role_code IN (:roleCodes) " +
            "AND m.phone IS NOT NULL " +
            "AND m.status = 1 " +
            "AND mr.is_active = 1 " +
            "AND r.is_active = 1", nativeQuery = true)
 List<String> findPhoneNumbersByRoleCodes(@Param("roleCodes") List<String> roleCodes);

    
    /**
     * 根據角色列表查詢郵箱地址
     */
    @Query(value = "SELECT DISTINCT m.email " +
            "FROM member m " +
            "JOIN member_roles mr ON m.id = mr.member_id " +
            "JOIN roles r ON mr.role_id = r.id " +
            "WHERE r.role_code IN (:roleCodes) " +
            "AND m.email IS NOT NULL " +
            "AND m.status = 1 " +
            "AND mr.is_active = 1 " +
            "AND r.is_active = 1", nativeQuery = true)
 List<String> findEmailsByRoleCodes(@Param("roleCodes") List<String> roleCodes);

    
    /**
     * 查詢所有啟用會員的手機號碼
     */
    @Query("SELECT m.phone FROM Member m WHERE m.phone IS NOT NULL AND m.status = true")
    List<String> findAllActivePhoneNumbers();
    
    /**
     * 查詢所有啟用會員的郵箱地址
     */
    @Query("SELECT m.email FROM Member m WHERE m.email IS NOT NULL AND m.status = true")
    List<String> findAllActiveEmails();
    
    /**
     * 根據角色代碼查詢會員 ID 列表（用於創建通知讀取狀態）
     */
    @Query(value = "SELECT DISTINCT m.id " +
            "FROM member m " +
            "JOIN member_roles mr ON m.id = mr.member_id " +
            "JOIN roles r ON mr.role_id = r.id " +
            "WHERE r.role_code IN (:roleCodes) " +
            "AND m.status = 1 " +
            "AND mr.is_active = 1 " +
            "AND r.is_active = 1", nativeQuery = true)
 List<Integer> findMemberIdsByRoleCodes(@Param("roleCodes") List<String> roleCodes);

    
    /**
     * 查詢所有啟用會員的 ID 列表
     */
    @Query("SELECT m.id FROM Member m WHERE m.status = true")
    List<Integer> findAllActiveMemberIds();
    
    
     Member findNameById(Integer id);
    
    
}