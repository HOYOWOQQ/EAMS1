package com.eams.common.Security.entity;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import com.eams.Entity.member.Member;
import com.eams.Entity.notice.Notice;
import com.eams.common.Configuration.entity.ContentTemplate;
import com.eams.common.Support.entity.Notification;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "role_name", length = 50, nullable = false, unique = true)
    private String roleName;

    @Column(name = "role_code", length = 50, nullable = false, unique = true)
    private String roleCode;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "level_priority")
    private Integer levelPriority = 0;

    @Column(name = "is_system_role")
    private Boolean isSystemRole = false;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "max_users")
    private Integer maxUsers;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by")
    private Integer createdBy;

    @Column(name = "updated_by")
    private Integer updatedBy;
    
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "role", cascade = CascadeType.ALL)
	@JsonManagedReference
	private List<RolePermission> rolePermission = new LinkedList<RolePermission>();
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "role", cascade = CascadeType.ALL)
	@JsonManagedReference
	private List<MemberRole> memberRole = new LinkedList<MemberRole>();
    
    @ManyToMany(mappedBy = "targetRoleList", fetch = FetchType.LAZY)
    private List<Notification> targetNotifications;

}
