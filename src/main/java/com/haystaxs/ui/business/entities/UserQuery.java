package com.haystaxs.ui.business.entities;

import java.sql.Time;
import java.sql.Timestamp;

/**
 * Created by adnan on 11/25/15.
 */
public class UserQuery {
    private String logDatabase;
    private String logUser;
    private Timestamp queryStartTime;
    private Timestamp queryEndTime;
    private float durationSeconds;
    private String sql;
    private String qryType;
    private int totalRows;

    public String getLogDatabase() {
        return logDatabase;
    }

    public void setLogDatabase(String logDatabase) {
        this.logDatabase = logDatabase;
    }

    public String getLogUser() {
        return logUser;
    }

    public void setLogUser(String logUser) {
        this.logUser = logUser;
    }

    public Timestamp getQueryStartTime() {
        return queryStartTime;
    }

    public void setQueryStartTime(Timestamp queryStartTime) {
        this.queryStartTime = queryStartTime;
    }

    public Timestamp getQueryEndTime() {
        return queryEndTime;
    }

    public void setQueryEndTime(Timestamp queryEndTime) {
        this.queryEndTime = queryEndTime;
    }

    public float getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(float durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getQryType() {
        return qryType;
    }

    public void setQryType(String qryType) {
        this.qryType = qryType;
    }

    public int getTotalRows() {
        return totalRows;
    }

    public void setTotalRows(int totalRows) {
        this.totalRows = totalRows;
    }
}
