package com.hutb.animalmanage.exception;

/**
 * 自定义异常类 - 运行时异常
 */
public class CommonException extends RuntimeException {
    public CommonException(String message) {
        super(message);
    }
}
