package com.hutb.volunteerActivity.model.pojo;

import java.util.List;

/**
 *分页查询结果
 */
public class PageInfo<T> {
    private long total;
    private List<T> data;

    public PageInfo(long total, List<T> data) {
        this.total = total;
        this.data = data;
    }
    public PageInfo() {
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }
}