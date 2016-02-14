package com.haystaxs.ui.business.entities;

import java.io.Serializable;
import java.sql.Time;
import java.sql.Timestamp;

/**
 * Created by Adnan on 10/21/2015.
 */
public class Gpsd implements Serializable {
    private int gpsdId;
    private String dbName;
    private String filename;
    private String gpsdDb;
    private Timestamp gpsdDate;
    private String gpsdParams;
    private String gpsdVersion;
    private int noOfLines;
    private Timestamp fileSubmittedOn;
    private String status;
    private Timestamp createdOn;
    public String host;
    public String userName;
    private int port;
    public String password;
    public String dbType;
    public Timestamp lastQueriesRefreshedOn;
    public boolean isActive;
    public Timestamp lastSchemaRefreshedOn;
    public String friendlyName;
    public boolean isDefault;

    public Gpsd(){}

    public int getGpsdId() {
        return gpsdId;
    }

    public void setGpsdId(int gpsdId) {
        this.gpsdId = gpsdId;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Timestamp getFileSubmittedOn() {
        return fileSubmittedOn;
    }

    public void setFileSubmittedOn(Timestamp fileSubmittedOn) {
        this.fileSubmittedOn = fileSubmittedOn;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getNoOfLines() {
        return noOfLines;
    }

    public void setNoOfLines(int noOfLines) {
        this.noOfLines = noOfLines;
    }

    public String getGpsdVersion() {
        return gpsdVersion;
    }

    public void setGpsdVersion(String gpsdVersion) {
        this.gpsdVersion = gpsdVersion;
    }

    public Timestamp getGpsdDate() {
        return gpsdDate;
    }

    public void setGpsdDate(Timestamp gpsdDate) {
        this.gpsdDate = gpsdDate;
    }

    public String getGpsdDb() {
        return gpsdDb;
    }

    public void setGpsdDb(String gpsdDb) {
        this.gpsdDb = gpsdDb;
    }

    public String getGpsdParams() {
        return gpsdParams;
    }

    public void setGpsdParams(String gpsdParams) {
        this.gpsdParams = gpsdParams;
    }

    public Timestamp getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Timestamp createdOn) {
        this.createdOn = createdOn;
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

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDbType() {
        return dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
    }

    public Timestamp getLastQueriesRefreshedOn() {
        return lastQueriesRefreshedOn;
    }

    public void setLastQueriesRefreshedOn(Timestamp lastQueriesRefreshedOn) {
        this.lastQueriesRefreshedOn = lastQueriesRefreshedOn;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public Timestamp getLastSchemaRefreshedOn() {
        return lastSchemaRefreshedOn;
    }

    public void setLastSchemaRefreshedOn(Timestamp lastSchemaRefreshedOn) {
        this.lastSchemaRefreshedOn = lastSchemaRefreshedOn;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public void setFriendlyName(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setIsDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }
}
