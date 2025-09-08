package com.eams.Controller.score;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.eams.Entity.score.ExamResult;
import com.eams.Entity.score.DTO.ExamPaperDTO;
import com.eams.Service.score.ExamPaperService;
import com.eams.Service.score.ExamResultService;

@Controller
public class ScoreViewController {
	
	@Autowired
	private ExamPaperService epService;
	
	@Autowired
	private ExamResultService erService;
	
	// 成績查詢頁面
	@GetMapping("/SelectExamResult")
	public String showSelectExamResultPage(Model model,
	                                       @RequestParam(name = "examPaper", required = false) String examPaperName) {
	    // ✅ 只抓取考卷名稱清單
	    List<String> examPaperList = epService.getAllPaperNames();

	    List<ExamResult> examResults ;

	    if (examPaperName != null && !examPaperName.isEmpty()) {
	        examResults = erService.findByPaperName(examPaperName); 
	        model.addAttribute("selectedPaper", examPaperName);
	    } else {
	        examResults = erService.findAll();
	    }

	    model.addAttribute("examResults", examResults);
	    model.addAttribute("examPaperList", examPaperList); 

	    return "score/SelectExamResult";
	}
	// 考卷查詢頁面
	@GetMapping("/SelectExamPaper")
	public String showSelectExamPaperPage() {
		return "score/SelectExamPaper";
	}

	// 成績新增頁面
	@GetMapping("/BatchInsertExamresult")
	public String showBatchInsertExamResultPage() {
		return "score/BatchInsertExamresult";
	}

	// 考卷新增頁面
	@GetMapping("/InsertExamPaper")
	public String showInsertExamPaperPage() {
		return "score/InsertExamPaper";
	}

	// 成績新增成功跳轉頁面
	@GetMapping("/batchInsertResult")
	public String showBatchInsertResultSuccessPage() {
		return "score/batchInsertResult";
	}
}
