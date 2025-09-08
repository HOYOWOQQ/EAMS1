package com.eams.common.Configuration.entity;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import com.eams.Entity.member.Member;
import com.eams.common.Support.entity.Notification;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "content_templates", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "template_category", "template_key" }) })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContentTemplate {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "template_category", length = 50, nullable = false)
	private String templateCategory;

	@Column(name = "template_key", length = 100, nullable = false)
	private String templateKey;

	@Column(name = "template_name", length = 200, nullable = false)
	private String templateName;

	@Column(name = "subject", length = 200)
	private String subject;

	@Column(name = "content", columnDefinition = "NVARCHAR(MAX)", nullable = false)
	private String content;

	@Column(name = "variables", columnDefinition = "NVARCHAR(MAX)")
	private String variables;

	@Column(name = "metadata", columnDefinition = "NVARCHAR(MAX)")
	private String metadata;

	@Column(name = "is_active")
	private Boolean isActive = true;

	@Column(name = "usage_count")
	private Integer usageCount = 0;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	@Column(name = "created_by")
	private Integer createdBy;

	@Column(name = "updated_by")
	private Integer updatedBy;

	@OneToMany(mappedBy = "template", fetch = FetchType.LAZY)
	private List<Notification> notifications;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "created_by", insertable = false, updatable = false)
	@JsonBackReference
	private Member createdByMember;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "updated_by", insertable = false, updatable = false)
	@JsonBackReference
	private Member updatedByMember;
}
