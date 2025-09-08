package com.eams.common.Security.entity;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "permissions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "permission_name", length = 100, nullable = false)
    private String permissionName;

    @Column(name = "permission_code", length = 100, nullable = false, unique = true)
    private String permissionCode;

    @Column(name = "category", length = 50)
    private String category;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "resource_type", length = 50)
    private String resourceType;

    @Column(name = "action_type", length = 50)
    private String actionType;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "is_dangerous")
    private Boolean isDangerous = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "created_by")
    private Integer createdBy;
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "permission", cascade = CascadeType.ALL)
	@JsonManagedReference
	private List<RolePermission> rolePermission = new LinkedList<RolePermission>();


    // 如要補 Member 關聯可以再寫 @ManyToOne
}
