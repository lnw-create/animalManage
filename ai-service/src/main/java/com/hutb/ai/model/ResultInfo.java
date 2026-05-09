package com.hutb.ai.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一返回（与 pet-service.ResultInfo 同形）
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
public class ResultInfo<T> {
    private String code = "1";
    private String msg;
    private T data;

    public static <T> ResultInfo<T> success(T data) {
        ResultInfo<T> r = new ResultInfo<>();
        r.setData(data);
        return r;
    }

    public static <T> ResultInfo<T> success() {
        return new ResultInfo<>();
    }

    public static <T> ResultInfo<T> fail(String msg) {
        ResultInfo<T> r = new ResultInfo<>();
        r.setCode("0");
        r.setMsg(msg);
        return r;
    }
}
