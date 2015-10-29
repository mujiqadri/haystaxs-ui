package com.haystaxs.ui.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by Adnan on 10/23/2015.
 */
@Component
public class AppConfig {
    @Value("#{appProps['gpsd.saveDirectory']}")
    private String gpsdSaveDirectory;
    @Value("#{appProps['runlog.saveDirectory']}")
    private String runlogSaveDirectory;
    @Value("#{appProps['ftp.host']}")
    private String ftpHost;
    @Value("#{appProps['ftp.user']}")
    private String ftpUser;
    @Value("#{appProps['ftp.pwd']}")
    private String ftpPassword;
    @Value("#{appProps['saveToFtp']}")
    private boolean saveToFtp;

    public String getGpsdSaveDirectory() {
        return gpsdSaveDirectory;
    }

    /*public void setGpsdSaveDirectory(String gpsdSaveDirectory) {
        this.gpsdSaveDirectory = gpsdSaveDirectory;
    }*/

    public String getRunlogSaveDirectory() {
        return runlogSaveDirectory;
    }

    /*public void setRunlogSaveDirectory(String runlogSaveDirectory) {
        this.runlogSaveDirectory = runlogSaveDirectory;
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
}
