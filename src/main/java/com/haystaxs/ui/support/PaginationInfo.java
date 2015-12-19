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

    public PaginationInfo(int totalNoOfItems, int pageSize, int currentPageNo) {
        this.totalNoOfItems = totalNoOfItems;
        this.pageSize = pageSize;
        this.currentPageNo = currentPageNo;

        totalNoOfPages = ((Double)Math.ceil((double) totalNoOfItems / pageSize)).intValue();
    }

    public int getCurrentPageNo() {
        return currentPageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getTotalNoOfPages() {
        return totalNoOfPages;
    }

    public int getTotalNoOfItems() {
        return totalNoOfItems;
    }

    public List<Integer> getAllPages() {
        List<Integer> result = new ArrayList<Integer>();

        for(int i=1; i<=totalNoOfPages; i++) {
            result.add(i);
        }

        return result;
    }
}
