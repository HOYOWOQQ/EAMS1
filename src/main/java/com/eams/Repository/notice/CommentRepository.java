package com.eams.Repository.notice;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.eams.Entity.notice.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {
    
    // 根據通知ID獲取主留言（非回覆）
    @Query("SELECT c FROM Comment c " +
           "LEFT JOIN FETCH c.user u " +
           "WHERE c.noticeId = :noticeId AND c.parentCommentId IS NULL " +
           "AND c.isActive = true ORDER BY c.createdAt ASC")
    List<Comment> findMainCommentsByNoticeId(@Param("noticeId") Integer noticeId);
    
    // 根據父留言ID獲取回覆
    @Query("SELECT c FROM Comment c " +
           "LEFT JOIN FETCH c.user u " +
           "WHERE c.parentCommentId = :parentCommentId AND c.isActive = true " +
           "ORDER BY c.createdAt ASC")
    List<Comment> findRepliesByParentCommentId(@Param("parentCommentId") Integer parentCommentId);
    
    // 根據ID和活躍狀態查找留言
    @Query("SELECT c FROM Comment c " +
           "LEFT JOIN FETCH c.user u " +
           "WHERE c.commentId = :commentId AND c.isActive = true")
    Optional<Comment> findActiveCommentById(@Param("commentId") Integer commentId);
    
    // 檢查留言是否屬於指定用戶
    @Query("SELECT COUNT(c) > 0 FROM Comment c " +
           "WHERE c.commentId = :commentId AND c.userId = :userId AND c.isActive = true")
    boolean isCommentOwner(@Param("commentId") Integer commentId, @Param("userId") Integer userId);
    
    // 軟刪除留言
    @Modifying
    @Query("UPDATE Comment c SET c.isActive = false, c.updatedAt = CURRENT_TIMESTAMP " +
           "WHERE c.commentId = :commentId")
    int softDeleteComment(@Param("commentId") Integer commentId);
    
    // 批量軟刪除通知的所有留言
    @Modifying
    @Query("UPDATE Comment c SET c.isActive = false, c.updatedAt = CURRENT_TIMESTAMP " +
           "WHERE c.noticeId = :noticeId")
    int softDeleteCommentsByNoticeId(@Param("noticeId") Integer noticeId);
    
    // 更新留言內容
    @Modifying
    @Query("UPDATE Comment c SET c.content = :content, c.updatedAt = CURRENT_TIMESTAMP " +
           "WHERE c.commentId = :commentId AND c.isActive = true")
    int updateCommentContent(@Param("commentId") Integer commentId, @Param("content") String content);
    
    // 獲取通知的留言數量
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.noticeId = :noticeId AND c.isActive = true")
    long getCommentCountByNoticeId(@Param("noticeId") Integer noticeId);
}
