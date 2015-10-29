package com.haystaxs.ui.business.entities;

import java.sql.Time;
import java.sql.Timestamp;

/**
 * Created by Adnan on 10/23/2015.
 */
public class InternalJobs {
    private int internalJobId;
    private int submittedBy;
    private Timestamp submittedOn;
    private String statusText;
    private Timestamp completedOn;
    private String errorsText;
    private int contextId;
    private Timestamp userNotificationSentOn;

    public InternalJobs() {}

    public int getInternalJobId() {
        return internalJobId;
    }

    public void setInternalJobId(int internalJobId) {
        this.internalJobId = internalJobId;
    }

    public int getSubmittedBy() {
        return submittedBy;
    }

    public void setSubmittedBy(int submittedBy) {
        this.submittedBy = submittedBy;
    }

    public Timestamp getSubmittedOn() {
        return submittedOn;
    }

    public void setSubmittedOn(Timestamp submittedOn) {
        this.submittedOn = submittedOn;
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    public Timestamp getCompletedOn() {
        return completedOn;
    }

    public void setCompletedOn(Timestamp completedOn) {
        this.completedOn = completedOn;
    }

    public String getErrorsText() {
        return errorsText;
    }

    public void setErrorsText(String errorsText) {
        this.errorsText = errorsText;
    }

    public int getContextId() {
        return contextId;
    }

    public void setContextId(int contextId) {
        this.contextId = contextId;
    }

    public Timestamp getUserNotificationSentOn() {
        return userNotificationSentOn;
    }

    public void setUserNotificationSentOn(Timestamp userNotificationSentOn) {
        this.userNotificationSentOn = userNotificationSentOn;
    }
}
