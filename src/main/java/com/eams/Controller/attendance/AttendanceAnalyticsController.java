package com.eams.Controller.attendance;

import com.eams.Entity.attendance.DTO.TrendPointDTO;
import com.eams.Service.attendance.AttendanceAnalyticsService;
import com.eams.common.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attendance/analytics")
// 如已做全域 CORS，這段可刪
@CrossOrigin(
    origins = "${app.cors.allowed-origins:http://localhost:5173}",
    allowCredentials = "true",
    maxAge = 3600
)
public class AttendanceAnalyticsController {

    @Autowired
    private AttendanceAnalyticsService analyticsService;

    // （可選）方法權限，若有啟用 @EnableMethodSecurity：
    // @PreAuthorize("hasAnyRole('ADMIN','STAFF','TEACHER')")

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<?>> trend(
            @RequestParam String groupBy,           // month | week | day
            @RequestParam int year,
            @RequestParam(required = false) Integer month,   // groupBy=week/day 需要
            @RequestParam(required = false) Integer courseId // 可選
    ) {
        // 基本參數檢查：先擋掉常見錯誤，避免丟 500
        if (!"month".equals(groupBy) && !"week".equals(groupBy) && !"day".equals(groupBy)) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("groupBy 必須是 month / week / day"));
        }
        if (("week".equals(groupBy) || "day".equals(groupBy)) && month == null) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("groupBy=week/day 時，month 參數必填"));
        }

        List<TrendPointDTO> data = analyticsService.getTrend(groupBy, year, month, courseId);
        return ResponseEntity.ok(ApiResponse.success("OK", data));
    }

    // 把 IllegalArgumentException 攔成 400 JSON（避免前端拿到 HTML 錯誤頁）
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<?>> handleIllegalArg(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getMessage()));
    }
    
    @GetMapping("/courses")
    public ResponseEntity<ApiResponse<?>> getCourses(@RequestParam int year,
                                                     @RequestParam(required = false) Integer month) {
        var data = analyticsService.getCourseOptionsForAnalytics(year, month);
        return ResponseEntity.ok(ApiResponse.success("課程選項載入成功", data));
    }

}