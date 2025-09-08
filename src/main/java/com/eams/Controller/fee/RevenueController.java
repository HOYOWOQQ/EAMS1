package com.eams.Controller.fee;

import org.apache.catalina.filters.ExpiresFilter.XServletOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.eams.Entity.fee.DTO.ExpenseRowDTO;
import com.eams.Entity.fee.DTO.IncomeRowDTO;
import com.eams.Entity.fee.DTO.RefundRowDTO;
import com.eams.Entity.fee.DTO.RevenueOverviewDTO;
import com.eams.Service.fee.RevenueService;
import com.eams.common.Security.Services.PermissionChecker;


import java.time.LocalDate;
import java.util.Map;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/revenue")
@RequiredArgsConstructor
public class RevenueController {
	
	@Autowired
    private  RevenueService revenueService;
	
	@Autowired
    private  PermissionChecker permissionChecker;

    private boolean canViewAll() {
        // 視你系統而定：主任通常需要三個 view 權限
        return  permissionChecker.hasCurrentUserPermission("finance.report.view");
    	
//    		permissionChecker.hasCurrentUserPermission("tuition.admin.view")
//            && permissionChecker.hasCurrentUserPermission("scholarship.admin.view")
//            && permissionChecker.hasCurrentUserPermission("wallet.admin.view");
    }

    @GetMapping("/overview")
    public ResponseEntity<?> overview(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end
    ){
        if (!canViewAll()) return ResponseEntity.status(403).body(Map.of("success", false, "message", "沒有權限"));
        RevenueOverviewDTO dto = revenueService.getOverview(start, end);
        System.out.println("/overview-api測試!!!!" +dto);
        
        
        return ResponseEntity.ok(Map.of("success", true, "response", dto));
    }

    @GetMapping("/series")
    public ResponseEntity<?> series(
            @RequestParam(defaultValue = "month") String granularity, // month/day
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end
    ){
        if (!canViewAll()) return ResponseEntity.status(403).body(Map.of("success", false, "message", "沒有權限"));
        var list = revenueService.getSeries(start, end, granularity);
        return ResponseEntity.ok(Map.of("success", true, "response", list));
    }

    @GetMapping("/breakdown")
    public ResponseEntity<?> breakdown(
            @RequestParam(defaultValue = "courseType") String dimension,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end
    ){
        if (!canViewAll()) return ResponseEntity.status(403).body(Map.of("success", false, "message", "沒有權限"));
        var list = revenueService.getBreakdown(start, end, dimension);
        return ResponseEntity.ok(Map.of("success", true, "response", list));
    }

    @GetMapping("/income")
    public ResponseEntity<?> income(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end
    ){
        if (!canViewAll()) return ResponseEntity.status(403).body(Map.of("success", false, "message", "沒有權限"));
        Pageable pageable = PageRequest.of(Math.max(page,0), Math.min(size, 100), Sort.by(Sort.Direction.DESC, "payDate"));
        Page<IncomeRowDTO> data = revenueService.getIncomeRows(start, end, pageable);
        return ResponseEntity.ok(Map.of("success", true, "response", data));
    }

    @GetMapping("/expense")
    public ResponseEntity<?> expense(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end
    ){
        if (!canViewAll()) return ResponseEntity.status(403).body(Map.of("success", false, "message", "沒有權限"));
        Pageable pageable = PageRequest.of(Math.max(page,0), Math.min(size, 100), Sort.by(Sort.Direction.DESC, "grantTime"));
        Page<ExpenseRowDTO> data = revenueService.getExpenseRows(start, end, pageable);
        return ResponseEntity.ok(Map.of("success", true, "response", data));
    }
    
    /**
     * 退費紀錄 (for AdminOverview)
     * 支援 page/size 分頁
     */
    @GetMapping("/refund")
    public ResponseEntity<?> getRefunds(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
    	
    	if (!canViewAll()) return ResponseEntity.status(403).body(Map.of("success", false, "message", "沒有權限"));
        Pageable pageable = PageRequest.of(Math.max(page,0), Math.min(size, 100), Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<RefundRowDTO> data = revenueService.getRefundRows(start, end, pageable);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "response", data
        ));
    }
    
}