package com.hutb.commonUtils.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * 全局异常处理器
 * 处理所有类型的异常
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理已知的业务异常CommonException
     */
    @ExceptionHandler(CommonException.class)
    public ResponseEntity<Map<String, Object>> handleCommonException(CommonException ex, WebRequest request) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 400);
        response.put("message", ex.getMessage());
        response.put("success", false);
        response.put("timestamp", System.currentTimeMillis());

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * 处理所有其他类型的异常（包括NullPointerException等）
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleAllExceptions(Exception ex, WebRequest request) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 500);
        response.put("message", "服务器内部错误: " + ex.getMessage());
        response.put("success", false);
        response.put("timestamp", System.currentTimeMillis());
        response.put("exceptionType", ex.getClass().getSimpleName());

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
