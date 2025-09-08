package com.eams.Entity.member;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import com.eams.Entity.attendance.Attendance;
import com.eams.Entity.attendance.LeaveRequest;
import com.eams.Entity.chat.MemberOnlineStatus;
import com.eams.Entity.course.CourseEnroll;
import com.eams.Entity.course.Registration;
import com.eams.Entity.fee.PaymentNotice;
import com.eams.Entity.notice.Comment;
import com.eams.Entity.notice.Notice;
import com.eams.Entity.notice.NoticeReadStatus;
import com.eams.Entity.scholarship.ScholarshipGrant;
import com.eams.Entity.score.ExamAttempt;
import com.eams.Entity.score.ExamResult;
import com.eams.common.Configuration.entity.ContentTemplate;
import com.eams.common.Configuration.entity.SystemConfiguration;
import com.eams.common.Security.entity.MemberRole;
import com.eams.common.Security.entity.Role;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "member")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@Column(name = "account", length = 100, nullable = false)
	private String account;

	@Column(name = "password", length = 255, nullable = false)
	private String password;

	@Column(name = "name", length = 50)
	private String name;

	@Column(name = "role", length = 10)
	private String role;

	@Column(name = "email", length = 100)
	private String email;

	@Column(name = "phone", length = 20)
	private String phone;

	@Column(name = "verified")
	private Boolean verified;

	@Column(name = "status")
	private Boolean status;

	@Column(name = "create_time")
	private LocalDateTime createTime;

	@Column(name = "update_time")
	private LocalDateTime updateTime;

	@Column(name = "email_token", length = 100)
	private String emailToken;

	@Column(name = "token_expiry")
	private LocalDateTime tokenExpiry;

	@Column(name = "reset_token", length = 100)
	private String resetToken;

	@Column(name = "reset_expiry")
	private LocalDateTime resetExpiry;

	@Column(name = "last_pwd_change")
	private LocalDateTime lastPwdChange;

	@OneToOne(fetch = FetchType.LAZY, mappedBy = "member", cascade = CascadeType.ALL)
	@JsonManagedReference
	private Teacher teacher;

	@OneToOne(fetch = FetchType.LAZY, mappedBy = "member", cascade = CascadeType.ALL)
	@JsonManagedReference
	private Student student;
	
	@OneToOne(fetch = FetchType.LAZY, mappedBy = "member", cascade = CascadeType.ALL)
	@JsonManagedReference
	private MemberOnlineStatus memberOnlineStatus;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "teacher", cascade = CascadeType.ALL)
	@JsonManagedReference
	private List<Notice> notice = new LinkedList<Notice>();

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.ALL)
	@JsonManagedReference
	private List<Comment> comment = new LinkedList<Comment>();

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.ALL)
	@JsonManagedReference
	private List<NoticeReadStatus> noticeReadStatus = new LinkedList<NoticeReadStatus>();

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "studentAccount", cascade = CascadeType.ALL)
	@JsonManagedReference
	private List<PaymentNotice> paymentNotice = new LinkedList<PaymentNotice>();

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "updatedByMember", cascade = CascadeType.ALL)
	@JsonManagedReference
	private List<SystemConfiguration> systemConfiguration = new LinkedList<SystemConfiguration>();

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "createdByMember", cascade = CascadeType.ALL)
	@JsonManagedReference
	private List<ContentTemplate> createdByMember = new LinkedList<ContentTemplate>();

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "updatedByMember", cascade = CascadeType.ALL)
	@JsonManagedReference
	private List<ContentTemplate> updatedByMember = new LinkedList<ContentTemplate>();
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "member", cascade = CascadeType.ALL)
	@JsonManagedReference
	private List<MemberRole> memberRole = new LinkedList<MemberRole>();

	 @OneToMany(mappedBy = "studentAccount", cascade = CascadeType.ALL)
	    private List<ScholarshipGrant> scholarships = new ArrayList<>();



	public List<PaymentNotice> getPaymentNotice() {
		return paymentNotice;
	}

	public void setPaymentNotice(List<PaymentNotice> paymentNotice) {
		this.paymentNotice = paymentNotice;
	}

	/**
	 * @return the verified
	 */
	public Boolean getVerified() {
		return verified;
	}

	/**
	 * @param verified the verified to set
	 */
	public void setVerified(Boolean verified) {
		this.verified = verified;
	}

	/**
	 * @return the status
	 */
	public Boolean getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(Boolean status) {
		this.status = status;
	}

	/**
	 * @return the teacher
	 */
	public Teacher getTeacher() {
		return teacher;
	}

	/**
	 * @param teacher the teacher to set
	 */
	public void setTeacher(Teacher teacher) {
		this.teacher = teacher;
	}

	/**
	 * @return the student
	 */
	public Student getStudent() {
		return student;
	}

	/**
	 * @param student the student to set
	 */
	public void setStudent(Student student) {
		this.student = student;
	}

	/**
	 * @return the notice
	 */
	public List<Notice> getNotice() {
		return notice;
	}

	/**
	 * @param notice the notice to set
	 */
	public void setNotice(List<Notice> notice) {
		this.notice = notice;
	}

	/**
	 * @return the comment
	 */
	public List<Comment> getComment() {
		return comment;
	}

	/**
	 * @param comment the comment to set
	 */
	public void setComment(List<Comment> comment) {
		this.comment = comment;
	}

	/**
	 * @return the noticeReadStatus
	 */
	public List<NoticeReadStatus> getNoticeReadStatus() {
		return noticeReadStatus;
	}

	/**
	 * @param noticeReadStatus the noticeReadStatus to set
	 */
	public void setNoticeReadStatus(List<NoticeReadStatus> noticeReadStatus) {
		this.noticeReadStatus = noticeReadStatus;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public boolean isVerified() {
		return verified;
	}

	public void setVerified(boolean verified) {
		this.verified = verified;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public LocalDateTime getCreateTime() {
		return createTime;
	}

	public void setCreateTime(LocalDateTime createTime) {
		this.createTime = createTime;
	}

	public LocalDateTime getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(LocalDateTime updateTime) {
		this.updateTime = updateTime;
	}

	public String getEmailToken() {
		return emailToken;
	}

	public void setEmailToken(String emailToken) {
		this.emailToken = emailToken;
	}

	public LocalDateTime getTokenExpiry() {
		return tokenExpiry;
	}

	public void setTokenExpiry(LocalDateTime tokenExpiry) {
		this.tokenExpiry = tokenExpiry;
	}

	public String getResetToken() {
		return resetToken;
	}

	public void setResetToken(String resetToken) {
		this.resetToken = resetToken;
	}

	public LocalDateTime getResetExpiry() {
		return resetExpiry;
	}

	public void setResetExpiry(LocalDateTime resetExpiry) {
		this.resetExpiry = resetExpiry;
	}

	public LocalDateTime getLastPwdChange() {
		return lastPwdChange;
	}

	public void setLastPwdChange(LocalDateTime lastPwdChange) {
		this.lastPwdChange = lastPwdChange;
	}

	
	public List<SystemConfiguration> getSystemConfiguration() {
		return systemConfiguration;
	}

	public void setSystemConfiguration(List<SystemConfiguration> systemConfiguration) {
		this.systemConfiguration = systemConfiguration;
	}

	public List<ContentTemplate> getCreatedByMember() {
		return createdByMember;
	}

	public void setCreatedByMember(List<ContentTemplate> createdByMember) {
		this.createdByMember = createdByMember;
	}

	public List<ContentTemplate> getUpdatedByMember() {
		return updatedByMember;
	}

	public void setUpdatedByMember(List<ContentTemplate> updatedByMember) {
		this.updatedByMember = updatedByMember;
	}

	public List<MemberRole> getMemberRole() {
		return memberRole;
	}

	public void setMemberRole(List<MemberRole> memberRole) {
		this.memberRole = memberRole;
	}
	
	

}