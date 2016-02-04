package com.haystaxs.ui.business.entities;

import java.sql.Timestamp;

/**
 * Created by adnan on 2/4/16.
 */
public class Cluster {
    public int userId;
    public int clusterId;
    public String clusterName;
    public String host;
    public String userName;
    public String password;
    public String schemaRefreshSchedule;
    public String queryRefreshSchedule;
    public Timestamp createdOn;
    public String clusterType;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getClusterId() {
        return clusterId;
    }

    public void setClusterId(int clusterId) {
        this.clusterId = clusterId;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSchemaRefreshSchedule() {
        return schemaRefreshSchedule;
    }

    public void setSchemaRefreshSchedule(String schemaRefreshSchedule) {
        this.schemaRefreshSchedule = schemaRefreshSchedule;
    }

    public String getQueryRefreshSchedule() {
        return queryRefreshSchedule;
    }

    public void setQueryRefreshSchedule(String queryRefreshSchedule) {
        this.queryRefreshSchedule = queryRefreshSchedule;
    }

    public Timestamp getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Timestamp createdOn) {
        this.createdOn = createdOn;
    }

    public String getClusterType() {
        return clusterType;
    }

    public void setClusterType(String clusterType) {
        this.clusterType = clusterType;
    }
}
