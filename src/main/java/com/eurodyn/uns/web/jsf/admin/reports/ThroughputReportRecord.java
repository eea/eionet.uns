package com.eurodyn.uns.web.jsf.admin.reports;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ThroughputReportRecord implements Serializable {

    private static final long serialVersionUID = -2616219455296489030L;

    private static SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

    private int dailyEmailSuccess = 0;

    private int dailyEmailFailed = 0;

    private int dailyJabberSuccess = 0;

    private int dailyJabberFailed = 0;

    private String day;

    public int getDailyEmailFailed() {
        return dailyEmailFailed;
    }

    public void setDailyEmailFailed(int dailyEmailFailed) {
        this.dailyEmailFailed = dailyEmailFailed;
    }

    public int getDailyEmailSuccess() {
        return dailyEmailSuccess;
    }

    public void setDailyEmailSuccess(int dailyEmailSuccess) {
        this.dailyEmailSuccess = dailyEmailSuccess;
    }

    public int getDailyFailed() {
        return dailyEmailFailed + dailyJabberFailed;
    }

    public int getDailyJabberFailed() {
        return dailyJabberFailed;
    }

    public void setDailyJabberFailed(int dailyJabberFailed) {
        this.dailyJabberFailed = dailyJabberFailed;
    }

    public int getDailyJabberSuccess() {
        return dailyJabberSuccess;
    }

    public void setDailyJabberSuccess(int dailyJabberSuccess) {
        this.dailyJabberSuccess = dailyJabberSuccess;
    }

    public int getDailySuccess() {
        return dailyEmailSuccess + dailyJabberSuccess;
    }

    public String getDay() {
        return day;
    }

    public Date getRealDay() throws Exception {
        return sdf.parse(day);
    }

    public void setDay(String day) {
        this.day = day;
    }

}
