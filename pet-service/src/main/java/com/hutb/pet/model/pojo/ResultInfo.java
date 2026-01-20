package com.hutb.pet.model.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一结果返回
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
public class ResultInfo<T> {
    private String code = "1";
    private String msg;
    private T data;

    public static <T> ResultInfo<T> success(T data) {
        ResultInfo<T> result = new ResultInfo<>();
        result.setData(data);
        return result;
    }

    public static <T> ResultInfo<T> success() {
        return new ResultInfo<>();
    }

    public static <T> ResultInfo<T> fail(String msg) {
        ResultInfo<T> result = new ResultInfo<>();
        result.setCode("0");
        result.setMsg(msg);
        return result;
    }
}
