package com.haystaxs.ui.business.entities;

import java.sql.Timestamp;

/**
 * Created by adnan on 11/17/15.
 */
public class QueryLogDate {
    private int queryLogId;
    private Timestamp logDate;

    public int getQueryLogId() {
        return queryLogId;
    }

    public void setQueryLogId(int queryLogId) {
        this.queryLogId = queryLogId;
    }

    public Timestamp getLogDate() {
        return logDate;
    }

    public void setLogDate(Timestamp logDate) {
        this.logDate = logDate;
    }
}
