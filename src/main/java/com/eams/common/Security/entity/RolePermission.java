package com.eams.common.Security.entity;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "role_permissions", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "role_id", "permission_id" }) })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RolePermission {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "role_id", nullable = false)
	private Integer roleId;

	@Column(name = "permission_id", nullable = false)
	private Integer permissionId;

	@Column(name = "granted_at")
	private LocalDateTime grantedAt;

	@Column(name = "granted_by")
	private Integer grantedBy;

	@Column(name = "expires_at")
	private LocalDateTime expiresAt;

	@Column(name = "conditions", columnDefinition = "NVARCHAR(MAX)")
	private String conditions;

	@Column(name = "is_active")
	private Boolean isActive = true;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "role_id", insertable = false, updatable = false)
	@JsonBackReference
	private Role role;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "permission_id", insertable = false, updatable = false)
	@JsonBackReference
	private Permission permission;
}
