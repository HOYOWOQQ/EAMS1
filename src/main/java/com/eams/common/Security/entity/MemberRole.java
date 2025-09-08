package com.eams.common.Security.entity;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;

import com.eams.Entity.member.Member;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(
    name = "member_roles",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"member_id", "role_id"})
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "member_id", nullable = false)
    private Integer memberId;

    @Column(name = "role_id", nullable = false)
    private Integer roleId;

    @Column(name = "assigned_at")
    private LocalDateTime assignedAt;

    @Column(name = "assigned_by")
    private Integer assignedBy;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "conditions", columnDefinition = "NVARCHAR(MAX)")
    private String conditions;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "is_primary")
    private Boolean isPrimary = false;

    @Column(name = "notes", length = 255)
    private String notes;

     @ManyToOne(fetch = FetchType.LAZY)
     @JoinColumn(name = "member_id", insertable = false, updatable = false)
     @JsonBackReference
     private Member member;
    
     @ManyToOne(fetch = FetchType.LAZY)
     @JoinColumn(name = "role_id", insertable = false, updatable = false)
     @JsonBackReference
     private Role role;
}
