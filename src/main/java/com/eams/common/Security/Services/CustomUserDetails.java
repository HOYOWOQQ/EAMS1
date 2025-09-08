package com.eams.common.Security.Services;

import com.eams.Entity.member.Member;
import com.eams.common.Security.entity.MemberRole;
import com.eams.common.Security.entity.Permission;
import com.eams.common.Security.entity.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CustomUserDetails implements UserDetails {
    
    private Integer id;
    private String username;
    private String name;
    private String email;
    private String phone;
    
    @JsonIgnore
    private String password;
    
    private Boolean verified;
    private Boolean status;
    private String role; // 舊版角色欄位，保持相容性
    
    private Collection<? extends GrantedAuthority> authorities;
    private List<String> permissions; // 新增：具體權限列表

    public CustomUserDetails(Integer id, String username, String name, String email, 
                           String phone, String password, Boolean verified, Boolean status,
                           String role, Collection<? extends GrantedAuthority> authorities,
                           List<String> permissions) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.verified = verified;
        this.status = status;
        this.role = role;
        this.authorities = authorities;
        this.permissions = permissions;
    }

    public static CustomUserDetails create(Member member) {
        // 獲取用戶的有效角色
        List<GrantedAuthority> authorities = member.getMemberRole().stream()
                .filter(mr -> mr.getIsActive() && 
                             (mr.getExpiresAt() == null || mr.getExpiresAt().isAfter(LocalDateTime.now())))
                .map(MemberRole::getRole)
                .filter(Objects::nonNull)
                .filter(Role::getIsActive)
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRoleCode()))
                .collect(Collectors.toList());

        // 如果新權限系統沒有角色，使用舊的 role 欄位
        if (authorities.isEmpty() && member.getRole() != null) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + member.getRole().toUpperCase()));
        }

        // 獲取用戶的具體權限
        List<String> permissions = member.getMemberRole().stream()
                .filter(mr -> mr.getIsActive() && 
                             (mr.getExpiresAt() == null || mr.getExpiresAt().isAfter(LocalDateTime.now())))
                .map(MemberRole::getRole)
                .filter(Objects::nonNull)
                .filter(Role::getIsActive)
                .flatMap(role -> role.getRolePermission().stream())
                .filter(rp -> rp.getIsActive() && 
                             (rp.getExpiresAt() == null || rp.getExpiresAt().isAfter(LocalDateTime.now())))
                .map(rp -> rp.getPermission())
                .filter(Objects::nonNull)
                .filter(Permission::getIsActive)
                .map(Permission::getPermissionCode)
                .distinct()
                .collect(Collectors.toList());

        return new CustomUserDetails(
                member.getId(),
                member.getAccount(),
                member.getName(),
                member.getEmail(),
                member.getPhone(),
                member.getPassword(),
                member.getVerified(),
                member.getStatus(),
                member.getRole(), // 保留舊角色欄位
                authorities,
                permissions
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
    	return true;
    }

    // Getters
    public Integer getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public Boolean getVerified() { return verified; }
    public Boolean getStatus() { return status; }
    public String getRole() { return role; }
    public List<String> getPermissions() { return permissions; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomUserDetails that = (CustomUserDetails) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

	public void setId(Integer id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setRole(String role) {
		this.role = role;
	}
    
	public boolean isEmailVerified() {
	    return verified != null ? verified : false;
	}
    
}