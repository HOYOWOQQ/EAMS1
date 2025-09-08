package com.eams.Controller.scholarship;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eams.Entity.scholarship.GrantPerExamResult;
import com.eams.Entity.scholarship.GrantTop3BatchRequest;
import com.eams.Entity.scholarship.GrantTop3BatchResponse;
import com.eams.Entity.scholarship.ScholarshipGrantDTO;
import com.eams.Entity.scholarship.ScholarshipGrantRequest;
import com.eams.Service.scholarshipGrant.ScholarshipService;
import com.eams.common.Security.Services.CustomUserDetails;
import com.eams.common.Security.Services.PermissionChecker;


@RestController
@RequestMapping("/api")
public class ScholarshipController {
	
	@Autowired
	private  ScholarshipService scholarshipService;
	
	@Autowired
	private PermissionChecker permissionChecker;
	
	
	@PostMapping("/test-raw")
	public String testRawBody(@RequestBody String rawBody) {
	    System.out.println(rawBody); // 看看印出來的是什麼
	    return rawBody;
	}
	
//	手動新增獎學金的api
	@PostMapping("/scholarship")
	public ResponseEntity<?> savescholarshipsByid( @RequestBody ScholarshipGrantRequest request,
            @AuthenticationPrincipal CustomUserDetails me) {
		if(!permissionChecker.hasCurrentUserPermission("grade.manage")){
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("success", false, "message", "沒有權限"));
			
		}
		System.out.println("✅ Raw request: " + request);

	    // 逐一列印欄位
	    System.out.println("studentId = " + request.getStudentId());
	    System.out.println("examResultId = " + request.getExamResultId());
	    System.out.println("title = " + request.getTitle());
	    System.out.println("amount = " + request.getAmount());

		
		System.out.println("測試" +me.getId()+","+
				"學生id"+request.getStudentId()+","+"考卷id"+request.getExamResultId());
		
		Map<String, Object> response =scholarshipService.grantScholarship(request,me.getId());
		return ResponseEntity.ok(Map.of("success", true, "response", response));
	}
	

	@GetMapping("/findscholarships")  //for 學生api
	public ResponseEntity<?> getScholarships(@AuthenticationPrincipal CustomUserDetails me) {
		 List<ScholarshipGrantDTO> scholarshipsBystudentId = scholarshipService.getScholarshipsBystudentId(me.getId());
		 return ResponseEntity.ok(Map.of("success", true, "response", scholarshipsBystudentId));
	}
	
	@PostMapping("/grant-top3")
	public ResponseEntity<?> grantTop3Scholarships(@RequestParam Integer examPaperId,
	                                               @AuthenticationPrincipal CustomUserDetails teacher) {
	    if (!permissionChecker.hasCurrentUserPermission("grade.manage")) {
	        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("success", false, "message", "無權限"));
	    }

	    int grantedCount = scholarshipService.grantTop3Scholarships(examPaperId, teacher.getId());
	    return ResponseEntity.ok(Map.of("success", true, "grantedCount", grantedCount));
	}
	
	
	@PostMapping("/grant-top3/batch")
	public ResponseEntity<?> grantTop3ScholarshipsBatch(
	        @RequestBody GrantTop3BatchRequest req,
	        @AuthenticationPrincipal CustomUserDetails teacher) {

	    if (!permissionChecker.hasCurrentUserPermission("grade.manage")) {
	        return ResponseEntity.status(HttpStatus.FORBIDDEN)
	                .body(Map.of("success", false, "message", "無權限"));
	    }
	    if (req.getExamPaperIds() == null || req.getExamPaperIds().isEmpty()) {
	        return ResponseEntity.badRequest()
	                .body(Map.of("success", false, "message", "examPaperIds 不可為空"));
	    }

	    boolean dryRun = Boolean.TRUE.equals(req.getDryRun());
	    String titlePrefix = (req.getTitlePrefix() == null || req.getTitlePrefix().isBlank())
	            ? "獎學金" : req.getTitlePrefix();

	    List<GrantPerExamResult> results =
	            scholarshipService.grantTop3ScholarshipsBatch(req.getExamPaperIds(), teacher.getId(), dryRun, titlePrefix);

	    return ResponseEntity.ok(new GrantTop3BatchResponse(true, results));
	}
	
	
	

}
