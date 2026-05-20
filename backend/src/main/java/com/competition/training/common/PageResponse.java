package com.competition.training.common;

import java.util.List;

public class PageResponse<T> {
    private List<T> records;
    private long total;
    private int page;
    private int pageSize;

    public PageResponse() {}

    public PageResponse(List<T> records, long total, int page, int pageSize) {
        this.records = records;
        this.total = total;
        this.page = page;
        this.pageSize = pageSize;
    }

    public List<T> getRecords() { return records; }
    public long getTotal() { return total; }
    public int getPage() { return page; }
    public int getPageSize() { return pageSize; }
    public void setRecords(List<T> records) { this.records = records; }
    public void setTotal(long total) { this.total = total; }
    public void setPage(int page) { this.page = page; }
    public void setPageSize(int pageSize) { this.pageSize = pageSize; }
}
