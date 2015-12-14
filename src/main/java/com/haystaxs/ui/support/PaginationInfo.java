package com.haystaxs.ui.support;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by adnan on 12/13/15.
 */
public class PaginationInfo {
    private int currentPageNo;
    private int pageSize;
    private int totalNoOfPages;
    private int totalNoOfItems;

    public int getCurrentPageNo() {
        return currentPageNo;
    }

    public void setCurrentPageNo(int currentPageNo) {
        this.currentPageNo = currentPageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalNoOfPages() {
        return totalNoOfPages;
    }

    public void setTotalNoOfPages(int totalNoOfPages) {
        this.totalNoOfPages = totalNoOfPages;
    }

    public int getTotalNoOfItems() {
        return totalNoOfItems;
    }

    public void setTotalNoOfItems(int totalNoOfItems) {
        this.totalNoOfItems = totalNoOfItems;
    }

    public List<Integer> getAllPages() {
        List<Integer> result = new ArrayList<Integer>();

        for(int i=1; i<=totalNoOfPages; i++) {
            result.add(i);
        }

        return result;
    }
}
