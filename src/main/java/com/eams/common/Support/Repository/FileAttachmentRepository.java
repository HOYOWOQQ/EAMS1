package com.eams.common.Support.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.eams.common.Support.entity.FileAttachment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FileAttachmentRepository extends JpaRepository<FileAttachment, Long> {
    
    // 根據關聯表和ID查找檔案
    List<FileAttachment> findByRelatedTableAndRelatedIdAndIsDeletedFalseOrderByCreatedAtDesc(String relatedTable, Long relatedId);
    
    // 根據分類查找檔案
    List<FileAttachment> findByCategoryAndIsDeletedFalseOrderByCreatedAtDesc(String category);
    
    // 根據上傳者查找檔案
    List<FileAttachment> findByUploadedByAndIsDeletedFalseOrderByCreatedAtDesc(Integer uploadedBy);
    
    // 根據檔案類型查找檔案
    List<FileAttachment> findByFileTypeAndIsDeletedFalseOrderByCreatedAtDesc(String fileType);
    
    // 根據存取層級查找檔案
    List<FileAttachment> findByAccessLevelAndIsDeletedFalseOrderByCreatedAtDesc(String accessLevel);
    
    // 查找公開檔案
    @Query("SELECT fa FROM FileAttachment fa WHERE fa.accessLevel = 'public' AND fa.isDeleted = false ORDER BY fa.createdAt DESC")
    List<FileAttachment> findPublicFiles();
    
    // 根據檔案雜湊查找重複檔案
    List<FileAttachment> findByFileHashAndIsDeletedFalse(String fileHash);
    
    // 查找大檔案
    @Query("SELECT fa FROM FileAttachment fa WHERE fa.fileSize > :sizeThreshold AND fa.isDeleted = false ORDER BY fa.fileSize DESC")
    List<FileAttachment> findLargeFiles(@Param("sizeThreshold") Long sizeThreshold);
    
    // 查找即將過期的檔案
    @Query("SELECT fa FROM FileAttachment fa WHERE fa.expiresAt <= :expirationTime AND fa.expiresAt IS NOT NULL AND fa.isDeleted = false")
    List<FileAttachment> findExpiringFiles(@Param("expirationTime") LocalDateTime expirationTime);
    
    // 分頁查詢檔案
    Page<FileAttachment> findByIsDeletedFalseOrderByCreatedAtDesc(Pageable pageable);
    
    // 根據處理狀態查找檔案
    List<FileAttachment> findByProcessingStatusAndIsDeletedFalseOrderByCreatedAtDesc(String processingStatus);
    
    // 查找需要病毒掃描的檔案
    @Query("SELECT fa FROM FileAttachment fa WHERE fa.isVirusScanned = false AND fa.isDeleted = false ORDER BY fa.createdAt")
    List<FileAttachment> findFilesNeedingVirusScan();
    
    // 根據版本查找檔案
    List<FileAttachment> findByParentFileIdOrderByVersionNumberDesc(Long parentFileId);
    
    // 統計檔案大小
    @Query("SELECT SUM(fa.fileSize) FROM FileAttachment fa WHERE fa.isDeleted = false")
    Long calculateTotalFileSize();
    
    // 統計檔案數量（按分類）
    @Query("SELECT fa.category, COUNT(fa) FROM FileAttachment fa WHERE fa.isDeleted = false GROUP BY fa.category")
    List<Object[]> countFilesByCategory();
    
    // 統計檔案數量（按類型）
    @Query("SELECT fa.fileType, COUNT(fa) FROM FileAttachment fa WHERE fa.isDeleted = false GROUP BY fa.fileType")
    List<Object[]> countFilesByType();
    
    // 模糊查詢檔案名稱
    @Query("SELECT fa FROM FileAttachment fa WHERE fa.originalName LIKE %:keyword% AND fa.isDeleted = false ORDER BY fa.createdAt DESC")
    List<FileAttachment> findByKeywordInFileName(@Param("keyword") String keyword);
    
 // 按 MIME 類型查詢
    List<FileAttachment> findByMimeTypeAndIsDeletedFalseOrderByCreatedAtDesc(String mimeType);
    
    // 按標籤查詢
    @Query("SELECT fa FROM FileAttachment fa WHERE fa.tags LIKE %:tag% AND fa.isDeleted = false ORDER BY fa.createdAt DESC")
    List<FileAttachment> findByTag(@Param("tag") String tag);
    
    // 根據用戶權限查找可訪問的文件
    @Query("SELECT fa FROM FileAttachment fa WHERE fa.isDeleted = false AND " +
           "(fa.accessLevel = 'public' OR fa.uploadedBy = :userId OR fa.allowedUsers LIKE %:userIdStr%)")
    List<FileAttachment> findAccessibleFilesByUser(@Param("userId") Integer userId, @Param("userIdStr") String userIdStr);
    
    // 查找有縮略圖的文件
    List<FileAttachment> findByThumbnailPathIsNotNullAndIsDeletedFalseOrderByCreatedAtDesc();
    
    // 查找特定關聯類型的文件
    List<FileAttachment> findByRelationTypeAndIsDeletedFalseOrderByCreatedAtDesc(String relationType);
}