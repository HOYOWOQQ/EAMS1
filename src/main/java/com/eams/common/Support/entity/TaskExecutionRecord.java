package com.eams.common.Support.entity;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "task_execution_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskExecutionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 任務資訊
    @Column(name = "task_category", length = 30, nullable = false)
    private String taskCategory;

    @Column(name = "task_type", length = 50, nullable = false)
    private String taskType;

    @Column(name = "task_name", length = 200, nullable = false)
    private String taskName;

    // 任務配置
    @Column(name = "task_config", columnDefinition = "NVARCHAR(MAX)")
    private String taskConfig;

    @Column(name = "input_parameters", columnDefinition = "NVARCHAR(MAX)")
    private String inputParameters;

    // 執行資訊
    @Column(name = "execution_mode", length = 20)
    private String executionMode = "MANUAL"; // Enum(MANUAL, SCHEDULED, TRIGGERED, RETRY)

    @Column(name = "status", length = 20)
    private String status = "RUNNING"; // Enum(PENDING, RUNNING, COMPLETED, FAILED, CANCELLED, TIMEOUT)

    @Column(name = "progress_percent")
    private Integer progressPercent = 0;

    // 結果資訊
    @Column(name = "total_records")
    private Long totalRecords = 0L;

    @Column(name = "processed_records")
    private Long processedRecords = 0L;

    @Column(name = "successful_records")
    private Long successfulRecords = 0L;

    @Column(name = "failed_records")
    private Long failedRecords = 0L;

    @Column(name = "skipped_records")
    private Long skippedRecords = 0L;

    // 檔案資訊
    @Column(name = "output_files", columnDefinition = "NVARCHAR(MAX)")
    private String outputFiles;

    @Column(name = "total_file_size")
    private Long totalFileSize;

    @Column(name = "file_location", length = 500)
    private String fileLocation;

    @Column(name = "compression_type", length = 20)
    private String compressionType;

    @Column(name = "encryption_applied")
    private Boolean encryptionApplied = false;

    // 時間和效能
    @Column(name = "scheduled_at")
    private LocalDateTime scheduledAt;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "duration_seconds")
    private Integer durationSeconds;

    @Column(name = "estimated_duration_seconds")
    private Integer estimatedDurationSeconds;

    @Column(name = "estimated_completion")
    private LocalDateTime estimatedCompletion;

    // 結果和錯誤
    @Column(name = "result_summary", columnDefinition = "NVARCHAR(MAX)")
    private String resultSummary;

    @Column(name = "error_message", columnDefinition = "NVARCHAR(MAX)")
    private String errorMessage;

    @Column(name = "warning_messages", columnDefinition = "NVARCHAR(MAX)")
    private String warningMessages;

    @Column(name = "warning_count")
    private Integer warningCount = 0;

    // 通知和存取
    @Column(name = "notification_sent")
    private Boolean notificationSent = false;

    @Column(name = "download_count")
    private Integer downloadCount = 0;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    // 元資料
    @Column(name = "created_by")
    private Integer createdBy;

    @Column(name = "parent_task_id")
    private Long parentTaskId;

    @Column(name = "retry_count")
    private Integer retryCount = 0;

    @Column(name = "max_retry_count")
    private Integer maxRetryCount = 3;

    @Column(name = "next_retry_at")
    private LocalDateTime nextRetryAt;

    // -- 若要自動設定 startedAt 預設為現在時間，可考慮用 @PrePersist 補
}
