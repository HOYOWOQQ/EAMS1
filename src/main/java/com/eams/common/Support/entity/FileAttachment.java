package com.eams.common.Support.entity;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "file_attachments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileAttachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 檔案基本資訊
    @Column(name = "original_name", length = 255, nullable = false)
    private String originalName;

    @Column(name = "stored_name", length = 255, nullable = false)
    private String storedName;

    @Column(name = "file_path", length = 500, nullable = false)
    private String filePath;

    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    @Column(name = "file_type", length = 50)
    private String fileType;

    @Column(name = "mime_type", length = 100)
    private String mimeType;

    @Column(name = "file_hash", length = 64)
    private String fileHash;

    // 關聯資訊
    @Column(name = "related_table", length = 50)
    private String relatedTable;

    @Column(name = "related_id")
    private Long relatedId;

    @Column(name = "relation_type", length = 30)
    private String relationType;

    // 分類和描述
    @Column(name = "category", length = 50)
    private String category;

    @Column(name = "title", length = 200)
    private String title;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "tags", length = 200)
    private String tags;

    // 存取控制
    @Column(name = "access_level", length = 20)
    private String accessLevel = "private";

    @Column(name = "allowed_roles", length = 200)
    private String allowedRoles;

    @Column(name = "allowed_users", columnDefinition = "NVARCHAR(MAX)")
    private String allowedUsers;

    // 處理狀態
    @Column(name = "processing_status", length = 20)
    private String processingStatus = "completed";

    @Column(name = "thumbnail_path", length = 500)
    private String thumbnailPath;

    @Column(name = "metadata", columnDefinition = "NVARCHAR(MAX)")
    private String metadata;

    // 統計和版本
    @Column(name = "download_count")
    private Integer downloadCount = 0;

    @Column(name = "view_count")
    private Integer viewCount = 0;

    @Column(name = "version_number")
    private Integer versionNumber = 1;

    @Column(name = "parent_file_id")
    private Long parentFileId;

    // 時效和狀態
    @Column(name = "last_accessed_at")
    private LocalDateTime lastAccessedAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    @Column(name = "is_virus_scanned")
    private Boolean isVirusScanned = false;

    @Column(name = "virus_scan_result", length = 20)
    private String virusScanResult;

    // 審計欄位
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "uploaded_by")
    private Integer uploadedBy;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "updated_by")
    private Integer updatedBy;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "deleted_by")
    private Integer deletedBy;

 
    @OneToMany(mappedBy = "imageFile", fetch = FetchType.LAZY)
    private List<Notification> notificationsAsImage;

}
