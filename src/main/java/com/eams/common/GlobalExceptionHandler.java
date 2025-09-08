package com.eams.common;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.persistence.EntityNotFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {
    
	//資料不存在異常
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(EntityNotFoundException e) {
        return ResponseEntity.status(404)
                .body(ApiResponse.error(e.getMessage()));
    }
    
    //處理 @Valid 驗證失敗 (JSON RequestBody)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return ResponseEntity.badRequest().body(ApiResponse.error(msg));
    }
    
    
    //自訂業務邏輯異常
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(ValidationException e) {
        return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
    }
    
    //處理所有其他異常
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ApiResponse<Void>> handleGeneral(Exception e) {
//        return ResponseEntity.status(500)
//                .body(ApiResponse.error("系統內部錯誤"));
//    }
}