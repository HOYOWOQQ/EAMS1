package com.eams.common.Configuration.entity;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;

import com.eams.Entity.member.Member;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "system_configurations", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "config_category", "config_key" }) })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SystemConfiguration {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "config_category", length = 50, nullable = false)
	private String configCategory;

	@Column(name = "config_key", length = 100, nullable = false)
	private String configKey;

	@Column(name = "config_value", columnDefinition = "NVARCHAR(MAX)")
	private String configValue;

	@Column(name = "data_type", length = 20)
	private String dataType = "string";

	@Column(name = "description", length = 255)
	private String description;

	@Column(name = "is_active")
	private Boolean isActive = true;

	@Column(name = "sort_order")
	private Integer sortOrder = 0;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	@Column(name = "updated_by")
	private Integer updatedBy;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "updated_by", insertable = false, updatable = false)
	@JsonBackReference
	private Member updatedByMember;
}
