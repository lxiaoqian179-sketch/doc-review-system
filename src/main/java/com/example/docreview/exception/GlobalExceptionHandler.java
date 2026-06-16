package com.example.docreview.exception;

import com.example.docreview.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
// @RestControllerAdvice → 全域攔截所有 Controller 拋出的例外
// 不需要在每個 Controller 裡個別處理錯誤，統一在這裡回傳一致的 JSON 格式
public class GlobalExceptionHandler {

    // 處理「找不到資源」的錯誤
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(400, e.getMessage()));
    }

    // 處理「權限不足」的錯誤（這裡同時解決之前 403 變 401 的問題！）
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException e) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(403, "權限不足，無法存取此資源"));
    }

    // 處理所有其他未預期的錯誤
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(500, "伺服器發生錯誤：" + e.getMessage()));
    }
}