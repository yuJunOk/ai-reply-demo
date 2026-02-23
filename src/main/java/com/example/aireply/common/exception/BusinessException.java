package com.example.aireply.common.exception;

import com.example.aireply.common.web.ResponseCode;
import lombok.Getter;

/**
 * 自定义异常类
 *
 * @author pengYuJun
 */
@Getter
public class BusinessException extends RuntimeException {

    private final ResponseCode code;

    private final String message;

    public BusinessException(ResponseCode responseCode) {
        super(responseCode.getMessage());
        this.code = responseCode;
        this.message = responseCode.getMessage();
    }

    public BusinessException(ResponseCode responseCode, String message) {
        super(responseCode.getMessage());
        this.code = responseCode;
        this.message = message;
    }
}
