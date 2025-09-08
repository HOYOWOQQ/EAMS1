package com.eams.Service.scholarshipGrant;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eams.Entity.member.Member;
import com.eams.Entity.member.Student;
import com.eams.Entity.scholarship.ScholarshipGrant;
import com.eams.Entity.scholarship.ScholarshipGrantDTO;
import com.eams.Entity.scholarship.ScholarshipGrantRequest;
import com.eams.Entity.score.ExamPaper;
import com.eams.Entity.score.ExamResult;
import com.eams.Entity.wallet.StudentWalletTransaction;
import com.eams.Repository.member.MemberRepository;
import com.eams.Repository.member.StudentRepository;
import com.eams.Repository.scholarshipGrant.ScholarshipRepository;
import com.eams.Repository.score.ExamPaperRepository;
import com.eams.Repository.score.ExamResultRepository;
import com.eams.Repository.wallet.StudentWalletTransactionRepository;
import com.eams.Service.wallet.StudentWalletService;
import com.eams.Entity.scholarship.*;

import jakarta.transaction.Transactional;

@Service

public class ScholarshipService {

	@Autowired
	private ScholarshipRepository scholarshipRepository;
	
	@Autowired
    private StudentRepository studentRepository; 
	
	@Autowired
    private MemberRepository memberRepository; 
	
	@Autowired
    private ExamPaperRepository examPaperRepository;
	
	@Autowired
	private ExamResultRepository examResultRepository;



@Autowired 
private StudentWalletService walletService;
	
	public List<ScholarshipGrantDTO> getAllScholarships() {
	    return scholarshipRepository.findAllWithStudentName();
	}
	
	
	public List<ScholarshipGrantDTO> getScholarshipsBystudentId(Integer studentId) {
	    return scholarshipRepository.findByStudentId(studentId);
	}


//老師手動創建成績獎學金
	@Transactional
	public Map<String, Object>  grantScholarship(ScholarshipGrantRequest request, Integer teacherId) {
		Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new RuntimeException("找不到學生"));
		
		ExamResult examResult =  null;
        if (request.getExamResultId() != null) {
        	examResult = examResultRepository.findById(request.getExamResultId())
                    .orElseThrow(() -> new RuntimeException("找不到考試成績"));
        }
        
        Member teacher =memberRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("找不到老師"));
        		
        
		
		 ScholarshipGrant grant = new ScholarshipGrant();
	        grant.setStudentInfo(student);
	        grant.setExamResult(examResult);
	        grant.setTitle(request.getTitle());
	        grant.setAmount(request.getAmount());
	        grant.setStatus("granted");
	        grant.setGrantTime(LocalDateTime.now());
	        grant.setCreatedBy(teacher);
	        grant.setCreatedAt(LocalDateTime.now());
	        grant.setUpdatedAt(LocalDateTime.now());
	        
	        
	     // 2. 自動新增 student_wallet_transaction
	        
	        walletService.deposit(request.getStudentId(), Integer.parseInt(grant.getAmount()), "scholarship", grant.getId(), "獎學金折抵 - " + grant.getTitle(),teacherId);
	       

	        scholarshipRepository.save(grant);
	        return Map.of(
                    "message", "獎學金已創建，已入電子錢包",
                    "grant", grant
                    
                    
            );
    
	}

	// ✅ 單張考卷：發放前三名
	@Transactional
	public int grantTop3Scholarships(Integer examPaperId, Integer operatorId) {
		List<ExamResult> top3 = examResultRepository.findTop3ByExamPaperIdOrderByScoreDesc(examPaperId);
		Member teacher =memberRepository.findById(operatorId)
				.orElseThrow(() -> new RuntimeException("找不到老師"));
		
		System.out.println(top3);
	    int count = 0;
	    for (ExamResult result : top3) {
	    	
	    	Integer studentId = result.getStudent().getId();
	    	Integer examResultId = result.getId();
	    	
	    	System.out.println("學生id !!!!"  + studentId);
	        boolean alreadyGranted = scholarshipRepository.existsByStudentInfo_IdAndExamResult_Id(studentId, examResultId);
	        System.out.println("👉 studentId: " + studentId + ", examPaperId: " + examPaperId + ", alreadyGranted: " + alreadyGranted);
	        if (!alreadyGranted) {
	        	String titlePrefix="獎學金";
	            ScholarshipGrant grant = new ScholarshipGrant();
	            grant.setStudentInfo(result.getStudent());
	            grant.setExamResult(result);
	            grant.setTitle(buildTitle(result, titlePrefix));
	            grant.setAmount(calcAmountByRank(result));
	            grant.setStatus("granted");
	            grant.setGrantTime(LocalDateTime.now());
	            grant.setCreatedBy(teacher);
	            scholarshipRepository.save(grant);
	            
	            walletService.deposit(studentId, Integer.parseInt(grant.getAmount()), "scholarship", grant.getId(), grant.getTitle(),operatorId);
	            
	          
	            count++;
	        }
	    }
	    return count;
	}


	
	// ✅ 多張考卷：外層批次（每張分開交易，彼此不影響）
    public List<GrantPerExamResult> grantTop3ScholarshipsBatch(
            List<Integer> examPaperIds, Integer teacherId, boolean dryRun, String titlePrefix) {
        List<GrantPerExamResult> results = new ArrayList<>();
        for (Integer paperId : examPaperIds) {
            try {
                GrantPerExamResult r = grantOneExamTop3(paperId, teacherId, dryRun, titlePrefix);
                results.add(r);
            } catch (Exception e) {
                results.add(new GrantPerExamResult(paperId, 0, 0, e.getMessage()));
            }
        }
        return results;
    }

    @Transactional
    protected GrantPerExamResult grantOneExamTop3(
            Integer examPaperId, Integer teacherId, boolean dryRun, String titlePrefix) {
        List<ExamResult> top3 = examResultRepository.findTop3ByExamPaperIdOrderByScoreDesc(examPaperId);
        Member teacher =memberRepository.findById(teacherId)
				.orElseThrow(() -> new RuntimeException("找不到老師"));
        int granted = 0, skipped = 0;

        for (ExamResult r : top3) {
        	
        	 Integer studentId = r.getStudent().getId();
             Integer examResultId = r.getId();
        	
            boolean exists = scholarshipRepository.existsByStudentInfo_IdAndExamResult_Id(studentId,examResultId);
            if (exists) { skipped++; continue; }

            if (!dryRun) {
                ScholarshipGrant g = new ScholarshipGrant();
                g.setStudentInfo(r.getStudent());
                g.setExamResult(r);
                g.setTitle(buildTitle(r, titlePrefix));        // 例：期中考 數學 - 第1名 
                g.setAmount(calcAmountByRank(r));
                g.setStatus("granted");
                g.setGrantTime(LocalDateTime.now());
                g.setCreatedBy(teacher);
              
                scholarshipRepository.save(g);
                walletService.deposit(studentId, Integer.parseInt(g.getAmount()), "scholarship", g.getId(), g.getTitle(),teacherId);
            }
            granted++;
        }
        return new GrantPerExamResult(examPaperId, granted, skipped, null);
    }

    /** 產生標題（可用 examPaper 名稱 + 名次） */
    private String buildTitle(ExamResult r, String prefix) {
        String paperName = r.getExamPaper() != null ? r.getExamPaper().getName() : ("考卷#" + r.getExamPaper().getId());
        Integer rank = r.getRankInClass();
        String rankText = (rank != null) ? ("第" + rank + "名") : "前三名";
        return paperName + " - " + rankText + " " + prefix;
    }

    /** 依名次給金額（請照你的規則調整；建議欄位改 DECIMAL） */
    private String calcAmountByRank(ExamResult r) {
        Integer rank = r.getRankInClass();
        if (rank == null) return "0";
        return switch (rank) {
            case 1 -> "1000";
            case 2 -> "500";
            case 3 -> "300";
            default -> "0";
        };
    }
	
	
	
    public ScholarshipGrant manualGrant(Integer studentId, Integer amount,String sourceType, Integer sourceId, String remark, Integer createdBy) {
        ScholarshipGrant grant = new ScholarshipGrant();
        
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("找不到學生"));
        Member teacher =memberRepository.findById(createdBy)
				.orElseThrow(() -> new RuntimeException("找不到老師"));
        
        grant.setStudentInfo(student);
        grant.setAmount(Integer.toString(amount));
//        grant.setDes(remark);
        grant.setCreatedBy(teacher);
        grant.setStatus("granted");
        grant.setCreatedAt(LocalDateTime.now());
        grant.setTitle(remark);
        
        
        return scholarshipRepository.save(grant);
    }
	
	
	

}






