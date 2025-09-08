package com.eams.common.Monitoring.entity;

import lombok.*;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "performance_metrics")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PerformanceMetric {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "metric_category", length = 30, nullable = false)
    private String metricCategory;

    @Column(name = "metric_name", length = 100, nullable = false)
    private String metricName;

    @Column(name = "metric_value", precision = 15, scale = 4, nullable = false)
    private BigDecimal metricValue;

    @Column(name = "unit", length = 20)
    private String unit;

    // 閾值
    @Column(name = "threshold_warning", precision = 15, scale = 4)
    private BigDecimal thresholdWarning;

    @Column(name = "threshold_critical", precision = 15, scale = 4)
    private BigDecimal thresholdCritical;

    @Column(name = "status", length = 10)
    private String status = "NORMAL"; // Enum(NORMAL, WARNING, CRITICAL)

    // 額外資訊
    @Column(name = "server_info", length = 200)
    private String serverInfo;

    @Column(name = "additional_data", columnDefinition = "NVARCHAR(MAX)")
    private String additionalData;

    @Column(name = "tags", length = 200)
    private String tags;

    // 聚合資訊
    @Column(name = "aggregation_type", length = 20)
    private String aggregationType = "INSTANT";

    @Column(name = "aggregation_window_start")
    private LocalDateTime aggregationWindowStart;

    @Column(name = "aggregation_window_end")
    private LocalDateTime aggregationWindowEnd;

    @Column(name = "recorded_at")
    private LocalDateTime recordedAt;
}
