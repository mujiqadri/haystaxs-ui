package com.haystaxs.ui.support;

public enum ClusterTypes {
    GREENPLUM("GREENPLUM"),
    REDSHIFT("REDSHIFT");

    private final String clusterType;

    ClusterTypes(String clusterType) {
        this.clusterType = clusterType;
    }

    public String value() {
        return clusterType;
    }
}
