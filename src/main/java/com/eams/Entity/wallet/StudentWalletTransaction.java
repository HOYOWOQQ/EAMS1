package com.eams.Entity.wallet;


import java.time.LocalDateTime;

import com.eams.Entity.member.Member;
import com.eams.Entity.member.Student;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "student_wallet_transaction")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentWalletTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id",insertable = false, updatable = false)
//    @JsonBackReference
    private  Member studentAccount;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "student_id")
	private Student studentInfo;
	

    @Column(name = "source_type")
    private String sourceType;

    @Column(name = "source_id")
    private Integer sourceId;

    @Column(nullable = false)
    private Integer amount;

    @Column(name = "description")
    private String description;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    
    @Column(name = "balance_after")
    private Integer balanceAfter;
    
    @Column(name = "type")
    private String type; // deposit, used, adjust, refund, etc.
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private Member createdBy;
    
}