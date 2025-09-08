package com.eams.Repository.notice;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eams.Entity.notice.NoticeReadStatus;

public interface NoticeReadStatusRepository extends JpaRepository<NoticeReadStatus, Integer> {
	
	// 檢查用戶是否已讀通知
    boolean existsByNoticeIdAndUserId(Integer noticeId, Integer userId);
    
    // 刪除特定通知的所有讀取記錄
    void deleteByNoticeId(Integer noticeId);
    
    // 刪除特定用戶的所有讀取記錄
    void deleteByUserId(Integer userId);
    
 // 新增：根據通知ID查詢所有已讀記錄，按讀取時間降序排列
    List<NoticeReadStatus> findByNoticeIdOrderByReadTimeDesc(Integer noticeId);
    
    // 新增：統計指定通知的已讀人數
    long countByNoticeId(Integer noticeId);
}

