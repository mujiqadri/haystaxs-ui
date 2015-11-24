package com.haystaxs.ui.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

/**
 * Created by Adnan on 10/23/2015.
 */
@Component
@EnableAsync
public class AppConfig {
    @Value("#{appProps['gpsd.saveDirectory']}")
    private String gpsdSaveDirectory;
    @Value("#{appProps['querylog.saveDirectory']}")
    private String queryLogSaveDirectory;
    @Value("#{appProps['ftp.host']}")
    private String ftpHost;
    @Value("#{appProps['ftp.user']}")
    private String ftpUser;
    @Value("#{appProps['ftp.pwd']}")
    private String ftpPassword;
    @Value("#{appProps['saveToFtp']}")
    private boolean saveToFtp;
    @Value("#{appProps['repository.haystackSchema']}")
    private String hsSchemaName;

    public String getGpsdSaveDirectory() {
        return gpsdSaveDirectory;
    }

    /*public void setGpsdSaveDirectory(String gpsdSaveDirectory) {
        this.gpsdSaveDirectory = gpsdSaveDirectory;
    }*/

    public String getQueryLogSaveDirectory() {
        return queryLogSaveDirectory;
    }

    /*public void setRunlogSaveDirectory(String queryLogSaveDirectory) {
        this.queryLogSaveDirectory = queryLogSaveDirectory;
    }*/

    public String getFtpHost() {
        return ftpHost;
    }

    public String getFtpUser() {
        return ftpUser;
    }

    public String getFtpPassword() {
        return ftpPassword;
    }

    public boolean isSaveToFtp() {
        return saveToFtp;
    }

    public String getHsSchemaName() {
        return hsSchemaName;
    }
}
