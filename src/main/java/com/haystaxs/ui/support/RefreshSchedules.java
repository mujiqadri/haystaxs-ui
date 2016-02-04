package com.haystaxs.ui.support;

public enum RefreshSchedules {
    TWELVE_HOURLY("12Hour"),
    TWENTYFOUR_HOURLY("24Hour"),
    WEEKLY("Weekly"),
    BI_WEEKLY("bi-weekly"),
    MONTHLY("Monthly");

    private final String refreshSchedule;

    RefreshSchedules(String refreshSchedule) {
        this.refreshSchedule = refreshSchedule;
    }

    public String value() {
        return refreshSchedule;
    }
}
