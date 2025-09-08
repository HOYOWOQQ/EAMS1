package com.eams.Service.member;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eams.Entity.member.Member;
import com.eams.Repository.member.MemberRepository;

@Service
public class MemberService {

	@Autowired
	private MemberRepository memberRepository;
	
	public List<Member> getActiveMembersList() {
	    return getAllMembers().stream()
	        .filter(Member::getStatus)
	        .collect(Collectors.toList());
	}

	// 新增
	@Transactional
	public void saveMember(Member member) {
		memberRepository.save(member);
	}

	// 查單筆
	public Member getMemberById(int id) {
		return memberRepository.findById(id).orElse(null);
	}

	// 查全部
	public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

	// 刪除
	@Transactional
    public void deleteMember(Member member) {
        memberRepository.delete(member);
	}
	
	//登入
	public Member login(String account, String password) {
        return memberRepository.findByAccountAndPassword(account, password);
    }

	//根據帳號查詢會員(session用)
	public Member findByAccount(String account) {
		return memberRepository.findMemberByAccount(account);
	}
	
	
	
	//根據條件查詢會員
	public List<Member> findByCondition(String Role, String Grade, Boolean status, Boolean verified, String Name) {
	    return memberRepository.findByCondition(Role, Grade, status, verified, Name);
	}
	
	//關鍵字搜尋會員
    public List<Member> search(String keyword) {
        return memberRepository.search(keyword);
    }
    
    //根據角色查詢會員
    public List<Member> findByRole(String role) {
        return memberRepository.findByRole(role);
    }
    
    //根據 Email Token 取得會員 ID
    public int getIdByEmailToken(String token) {
        Optional<Integer> result = memberRepository.getIdByEmailToken(token);
        return result.orElse(0);
    }

    //檢查重設密碼 Token
    public boolean checkResetToken(int id, String token) {
        return memberRepository.checkResetToken(id, token);
    }
    
    //包裝 Map 給 Controller 用
    public List<Map<String, Object>> getFilteredMembers(String role, String grade, String name) {
        List<Member> members = findByCondition(role, grade, null, null, name);
        List<Map<String, Object>> result = new ArrayList<>();

        for (Member m : members) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", m.getId());
            map.put("account", m.getAccount());
            map.put("role", m.getRole());
            map.put("name", m.getName());
            map.put("grade", m.getStudent() != null ? m.getStudent().getGrade() : null);
            map.put("status", m.getStatus());
            map.put("verified", m.getVerified());

            result.add(map);
        }

        return result;
    }
    

    // ========== 密碼管理業務邏輯 ==========

    //更新密碼
    @Transactional
    public boolean updatePassword(int id, String newPwd) {
        Optional<Member> memberOpt = memberRepository.findById(id);
        if (memberOpt.isPresent()) {
            Member member = memberOpt.get();
            member.setPassword(newPwd);
            member.setLastPwdChange(LocalDateTime.now());
            member.setUpdateTime(LocalDateTime.now());
            memberRepository.save(member);
            return true;
        }
        return false;
    }

    // ========== Email 驗證業務邏輯 ==========

    //Email 驗證
    @Transactional
    public boolean verifyEmail(int id, String token) {
        Optional<Member> memberOpt = memberRepository.findById(id);
        if (memberOpt.isPresent()) {
            Member member = memberOpt.get();
            if (token.equals(member.getEmailToken())) {
                member.setVerified(true);
                member.setEmailToken(null);
                member.setTokenExpiry(null);
                member.setUpdateTime(LocalDateTime.now());
                memberRepository.save(member);
                return true;
            }
        }
        return false;
    }

    //設定 Email 驗證 Token
    @Transactional
    public boolean setEmailToken(int id, String token, LocalDateTime expiry) {
        Optional<Member> memberOpt = memberRepository.findById(id);
        if (memberOpt.isPresent()) {
            Member member = memberOpt.get();
            member.setEmailToken(token);
            member.setTokenExpiry(expiry);
            member.setUpdateTime(LocalDateTime.now());
            memberRepository.save(member);
            return true;
        }
        return false;
    }

    // ========== 重設密碼業務邏輯 ==========

    //設定重設密碼 Token
    @Transactional
    public boolean setResetToken(int id, String token, LocalDateTime expiry) {
        Optional<Member> memberOpt = memberRepository.findById(id);
        if (memberOpt.isPresent()) {
            Member member = memberOpt.get();
            member.setResetToken(token);
            member.setResetExpiry(expiry);
            member.setUpdateTime(LocalDateTime.now());
            memberRepository.save(member);
            return true;
        }
        return false;
    }

    //清除重設密碼 Token
    @Transactional
    public boolean clearResetToken(int id) {
        Optional<Member> memberOpt = memberRepository.findById(id);
        if (memberOpt.isPresent()) {
            Member member = memberOpt.get();
            member.setResetToken(null);
            member.setResetExpiry(null);
            member.setUpdateTime(LocalDateTime.now());
            memberRepository.save(member);
            return true;
        }
        return false;
    }

    // ========== 會員狀態管理 ==========

    //設定會員狀態（啟用/停用
    @Transactional
    public boolean setStatus(int id, boolean enable) {
        Optional<Member> memberOpt = memberRepository.findById(id);
        if (memberOpt.isPresent()) {
            Member member = memberOpt.get();
            member.setStatus(enable);
            member.setUpdateTime(LocalDateTime.now());
            memberRepository.save(member);
            return true;
        }
        return false;
    }

    //更新會員基本資料
    @Transactional
    public boolean updateProfile(Member member) {
        try {
            member.setUpdateTime(LocalDateTime.now());
            memberRepository.save(member);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Transactional
    public void updateMember(Member member) {
        member.setUpdateTime(LocalDateTime.now());
        memberRepository.save(member);
    }

    // ========== 額外業務邏輯方法 ==========

    //檢查帳號是否已存在
    public boolean isAccountExists(String account) {
        return memberRepository.findByAccount(account) != null;
    }

    //檢查 Email 是否已存在
    public boolean isEmailExists(String email) {
        return memberRepository.findByEmail(email).isPresent();
    }

    //根據有效的 Email Token 查詢會員
    public Optional<Member> verifyEmailToken(String token) {
        return memberRepository.findByEmailTokenAndTokenExpiryAfter(token, LocalDateTime.now());
    }

    //根據有效的 Reset Token 查詢會員
    public Member findByValidResetToken(String token) {
        return memberRepository.findByResetTokenAndResetExpiryAfter(token, LocalDateTime.now()).orElse(null);
    }
    
    
 // 在你現有的 MemberService.java 中增加這些方法

 // ========== RBAC 相關的新增方法 ==========

 /**
  * 獲取啟用的用戶（分頁） - 用於權限管理頁面
  */
 public List<Member> getActiveMembers() {
     return memberRepository.findByStatus(true);
 }

 /**
  * 獲取用戶基本信息（用於權限分配頁面）
  */
 public List<Map<String, Object>> getMembersForPermissionManagement() {
     List<Member> members = getActiveMembers();
     List<Map<String, Object>> result = new ArrayList<>();

     for (Member m : members) {
         Map<String, Object> map = new HashMap<>();
         map.put("id", m.getId());
         map.put("account", m.getAccount());
         map.put("name", m.getName());
         map.put("role", m.getRole()); // 原始角色字段
         map.put("email", m.getEmail());
         map.put("status", m.getStatus());
         map.put("verified", m.getVerified());
         map.put("createTime", m.getCreateTime());
         
         // 如果有學生信息，加上年級
         if (m.getStudent() != null) {
             map.put("grade", m.getStudent().getGrade());
         }
         
         result.add(map);
     }

     return result;
 }

 /**
  * 搜尋用戶（用於權限管理）
  */
 public List<Map<String, Object>> searchMembersForPermissionManagement(String keyword) {
     List<Member> members;
     
     if (keyword == null || keyword.trim().isEmpty()) {
         members = getActiveMembers();
     } else {
         members = search(keyword); // 使用你現有的搜尋方法
     }
     
     return convertMembersToMapList(members);
 }

 /**
  * 根據角色篩選用戶（用於批量權限分配）
  */
 public List<Map<String, Object>> getMembersByRoleForPermission(String role) {
     List<Member> members = findByRole(role); // 使用你現有的方法
     return convertMembersToMapList(members);
 }

 /**
  * 檢查用戶是否可以被分配角色（業務邏輯檢查）
  */
 public boolean canAssignRole(Integer memberId) {
     Member member = getMemberById(memberId);
     if (member == null) {
         return false;
     }
     
     // 檢查用戶狀態
     if (!member.getStatus()) {
         return false;
     }
     
     // 檢查用戶是否已驗證 Email
     if (!member.getVerified()) {
         return false;
     }
     
     return true;
 }

 /**
  * 獲取用戶統計信息（用於權限管理儀表板）
  */
 public Map<String, Object> getMemberStatistics() {
     List<Member> allMembers = getAllMembers();
     
     Map<String, Object> stats = new HashMap<>();
     stats.put("totalMembers", allMembers.size());
     
     long activeMembers = allMembers.stream()
         .filter(Member::getStatus)
         .count();
     stats.put("activeMembers", activeMembers);
     
     long verifiedMembers = allMembers.stream()
         .filter(Member::getVerified)
         .count();
     stats.put("verifiedMembers", verifiedMembers);
     
     // 按角色統計
     Map<String, Long> roleStats = allMembers.stream()
         .filter(m -> m.getRole() != null)
         .collect(java.util.stream.Collectors.groupingBy(
             Member::getRole,
             java.util.stream.Collectors.counting()
         ));
     stats.put("roleDistribution", roleStats);
     
     // 今日新增用戶
     LocalDateTime todayStart = LocalDateTime.now().toLocalDate().atStartOfDay();
     long todayNewMembers = allMembers.stream()
         .filter(m -> m.getCreateTime() != null && m.getCreateTime().isAfter(todayStart))
         .count();
     stats.put("todayNewMembers", todayNewMembers);
     
     return stats;
 }

 /**
  * 驗證用戶密碼（用於敏感權限操作的二次驗證）
  */
 public boolean validatePassword(Integer memberId, String password) {
     Member member = getMemberById(memberId);
     if (member == null) {
         return false;
     }
     
     // 這裡應該使用加密比較，根據你的密碼加密方式調整
     return password.equals(member.getPassword());
 }

 /**
  * 批量更新用戶狀態（用於權限管理）
  */
 @Transactional
 public Map<String, Integer> batchUpdateStatus(List<Integer> memberIds, boolean enable) {
     int successCount = 0;
     int failCount = 0;
     
     for (Integer memberId : memberIds) {
         if (setStatus(memberId, enable)) { // 使用你現有的方法
             successCount++;
         } else {
             failCount++;
         }
     }
     
     Map<String, Integer> result = new HashMap<>();
     result.put("successCount", successCount);
     result.put("failCount", failCount);
     return result;
 }

 // ========== 私有輔助方法 ==========

 /**
  * 將 Member 列表轉換為 Map 列表（統一格式）
  */
 private List<Map<String, Object>> convertMembersToMapList(List<Member> members) {
     List<Map<String, Object>> result = new ArrayList<>();

     for (Member m : members) {
         Map<String, Object> map = new HashMap<>();
         map.put("id", m.getId());
         map.put("account", m.getAccount());
         map.put("name", m.getName());
         map.put("role", m.getRole());
         map.put("email", m.getEmail());
         map.put("phone", m.getPhone());
         map.put("status", m.getStatus());
         map.put("verified", m.getVerified());
         map.put("createTime", m.getCreateTime());
         map.put("updateTime", m.getUpdateTime());
         
         // 學生信息
         if (m.getStudent() != null) {
             map.put("grade", m.getStudent().getGrade());
             map.put("studentId", m.getStudent().getId());
         }
         
         // 教師信息
         if (m.getTeacher() != null) {
             map.put("teacherId", m.getTeacher().getId());
         }
         
         result.add(map);
     }

     return result;
 }
	
//	// ========== 查詢業務邏輯 ==========
//
//	/**
//	 * 根據帳號查詢會員
//	 */
//	public Member findByAccount(String account) {
//		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
//			MemberDao dao = new MemberDao(session);
//			return dao.findByAccount(account);
//		}
//	}
//
//
//	
//	
//
//	/**
//	 * 根據條件查詢會員
//	 */
//	public List<Member> findByCondition(String filterRole, String filterGrade, String filterName) {
//		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
//			MemberDao dao = new MemberDao(session);
//			return dao.findByCondition(filterRole, filterGrade, filterName);
//		}
//	}
//
//	/**
//	 * 關鍵字搜尋會員
//	 */
//	public List<Member> search(String keyword) {
//		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
//			MemberDao dao = new MemberDao(session);
//			return dao.search(keyword);
//		}
//	}
//
//	/**
//	 * 根據角色查詢會員
//	 */
//	public List<Member> findByRole(String role) {
//		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
//			MemberDao dao = new MemberDao(session);
//			return dao.findByRole(role);
//		}
//	}
//
//	/**
//	 * 根據 Email Token 取得會員 ID
//	 */
//	public int getIdByEmailToken(String token) {
//		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
//			MemberDao dao = new MemberDao(session);
//			return dao.getIdByEmailToken(token);
//		}
//	}
//
//	/**
//	 * 檢查重設密碼 Token
//	 */
//	public boolean checkResetToken(int id, String token) {
//		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
//			MemberDao dao = new MemberDao(session);
//			return dao.checkResetToken(id, token);
//		}
//	}
//
//	// ========== 密碼管理業務邏輯 ==========
//
//	/**
//	 * 更新密碼
//	 */
//	public boolean updatePassword(int id, String newPwd) {
//		Transaction tx = null;
//		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
//			tx = session.beginTransaction();
//
//			MemberDao dao = new MemberDao(session);
//			Member member = dao.findById(id);
//			if (member != null) {
//				member.setPassword(newPwd);
//				member.setLastPwdChange(LocalDateTime.now());
//				member.setUpdateTime(LocalDateTime.now());
//				dao.update(member);
//				tx.commit();
//				return true;
//			}
//			return false;
//		} catch (Exception e) {
//			if (tx != null)
//				tx.rollback();
//			throw e;
//		}
//	}
//
//	// ========== Email 驗證業務邏輯 ==========
//
//	/**
//	 * Email 驗證
//	 */
//	public boolean verifyEmail(int id, String token) {
//		Transaction tx = null;
//		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
//			tx = session.beginTransaction();
//
//			MemberDao dao = new MemberDao(session);
//			Member member = dao.findById(id);
//			if (member != null && token.equals(member.getEmailToken())) {
//				member.setVerified(true);
//				member.setEmailToken(null);
//				member.setTokenExpiry(null);
//				dao.update(member);
//				tx.commit();
//				return true;
//			}
//			return false;
//		} catch (Exception e) {
//			if (tx != null)
//				tx.rollback();
//			throw e;
//		}
//	}
//
//	/**
//	 * 設定 Email 驗證 Token
//	 */
//	public boolean setEmailToken(int id, String token, Timestamp expiry) {
//		Transaction tx = null;
//		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
//			tx = session.beginTransaction();
//
//			MemberDao dao = new MemberDao(session);
//			Member member = dao.findById(id);
//			if (member != null) {
//				member.setEmailToken(token);
//				member.setTokenExpiry(expiry.toLocalDateTime());
//				dao.update(member);
//				tx.commit();
//				return true;
//			}
//			return false;
//		} catch (Exception e) {
//			if (tx != null)
//				tx.rollback();
//			throw e;
//		}
//	}
//
//	// ========== 重設密碼業務邏輯 ==========
//
//	/**
//	 * 設定重設密碼 Token
//	 */
//	public boolean setResetToken(int id, String token, Timestamp expiry) {
//		Transaction tx = null;
//		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
//			tx = session.beginTransaction();
//
//			MemberDao dao = new MemberDao(session);
//			Member member = dao.findById(id);
//			if (member != null) {
//				member.setResetToken(token);
//				member.setResetExpiry(expiry.toLocalDateTime());
//				dao.update(member);
//				tx.commit();
//				return true;
//			}
//			return false;
//		} catch (Exception e) {
//			if (tx != null)
//				tx.rollback();
//			throw e;
//		}
//	}
//
//	/**
//	 * 清除重設密碼 Token
//	 */
//	public boolean clearResetToken(int id) {
//		Transaction tx = null;
//		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
//			tx = session.beginTransaction();
//
//			MemberDao dao = new MemberDao(session);
//			Member member = dao.findById(id);
//			if (member != null) {
//				member.setResetToken(null);
//				member.setResetExpiry(null);
//				dao.update(member);
//				tx.commit();
//				return true;
//			}
//			return false;
//		} catch (Exception e) {
//			if (tx != null)
//				tx.rollback();
//			throw e;
//		}
//	}
//
//	// ========== 會員狀態管理 ==========
//
//	/**
//	 * 設定會員狀態（啟用/停用）
//	 */
//	public boolean setStatus(int id, boolean enable) {
//		Transaction tx = null;
//		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
//			tx = session.beginTransaction();
//
//			MemberDao dao = new MemberDao(session);
//			Member member = dao.findById(id);
//			if (member != null) {
//				member.setStatus(enable);
//				dao.update(member);
//				tx.commit();
//				return true;
//			}
//			return false;
//		} catch (Exception e) {
//			if (tx != null)
//				tx.rollback();
//			throw e;
//		}
//	}
//
//	/**
//	 * 更新會員基本資料
//	 */
//	public boolean updateProfile(Member member) {
//		Transaction tx = null;
//		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
//			tx = session.beginTransaction();
//
//			MemberDao dao = new MemberDao(session);
//			dao.update(member);
//			tx.commit();
//			return true;
//		} catch (Exception e) {
//			if (tx != null)
//				tx.rollback();
//			throw e;
//		}
//	}

}
