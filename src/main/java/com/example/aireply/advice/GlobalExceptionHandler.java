package com.example.aireply.advice;

import com.example.aireply.common.exception.BusinessException;
import com.example.aireply.common.web.ResponseCode;
import com.example.aireply.common.web.ResponseEntity;
import com.example.aireply.component.notification.EmailSender;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author pengYuJun
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @Resource
    private EmailSender emailSender;

    /**
     * 业务异常处理
     * @param e 业务异常
     * @return
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Object> businessExceptionHandler(BusinessException e) {
        log.error("businessException", e);
        return ResponseEntity.fail(e.getCode(), e.getMessage());
    }

    /**
     * 运行异常处理
     * @param e 运行异常
     * @return
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity runtimeExceptionHandler(Exception e) {
        log.error("runtimeException", e);
        emailSender.sendBugReport(e);
        return ResponseEntity.fail(ResponseCode.ERROR, e.getMessage());
    }
}
