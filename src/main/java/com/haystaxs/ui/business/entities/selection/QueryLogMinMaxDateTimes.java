package com.haystaxs.ui.business.entities.selection;

import org.apache.commons.net.ntp.TimeStamp;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

/**
 * Created by adnan on 1/15/16.
 */
public class QueryLogMinMaxDateTimes {
    public Date minDate;
    public Date maxDate;
    public Time minTime;
    public Time maxTime;

    public Date getMinDate() {
        return minDate;
    }

    public void setMinDate(Date minDate) {
        this.minDate = minDate;
    }

    public Date getMaxDate() {
        return maxDate;
    }

    public void setMaxDate(Date maxDate) {
        this.maxDate = maxDate;
    }

    public Time getMinTime() {
        return minTime;
    }

    public void setMinTime(Time minTime) {
        this.minTime = minTime;
    }

    public Time getMaxTime() {
        return maxTime;
    }

    public void setMaxTime(Time maxTime) {
        this.maxTime = maxTime;
    }
}
