package com.haystaxs.ui.business.services;

import com.haystack.service.CatalogService;
import com.haystack.util.ConfigProperties;
import com.haystaxs.ui.util.AppConfig;
import com.haystaxs.ui.util.FileUtil;
import com.sun.javafx.collections.MappingChange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Map;

/**
 * Created by adnan on 11/23/15.
 */
@Service
public class HaystaxsLibService {
    final static Logger logger = LoggerFactory.getLogger(HaystaxsLibService.class);

    @Autowired
    private FileUtil fileUtil;
    @Autowired
    private AppConfig appConfig;

    @Async
    public void createGPSD(int gpsdId, String normalizedUserName, String gpsdFilePath) {
        logger.trace(String.format("createGPSD Started. %d %s %s", gpsdId, normalizedUserName, gpsdFilePath));
        try {
            ConfigProperties configProperties = new ConfigProperties();
            configProperties.loadProperties();

            CatalogService cs = new CatalogService(configProperties);
            String gpsdJson = cs.executeGPSD(gpsdId, normalizedUserName, gpsdFilePath);

            String baseDir = appConfig.getGpsdSaveDirectory() + File.separator + gpsdId;

            fileUtil.saveToFile(gpsdJson.getBytes(), baseDir, gpsdId + ".json");
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
        logger.trace("createGPSD Completed.");
    }

    @Async
    public void analyzeQueryLogs(Map<Integer, String> queryLogFiles) {
        for (Integer key : queryLogFiles.keySet()) {
            createQueryLog(key, queryLogFiles.get(key));
        }
    }

    private void createQueryLog(int queryLogId, String queryLogBaseDir) {
        logger.trace(String.format("createQueryLog Started. %d %s", queryLogId, queryLogBaseDir));
        try {
            ConfigProperties configProperties = new ConfigProperties();
            configProperties.loadProperties();

            CatalogService cs = new CatalogService(configProperties);
            boolean hadErrors = cs.processQueryLog(queryLogId, queryLogBaseDir);

            if(hadErrors) {
                logger.debug(String.format("CatalogService.processQueryLog(%d, %s) ran with some errors !", queryLogId, queryLogBaseDir));
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
        logger.trace("createQueryLog Completed.");
    }

    public String processWorkload(int workloadId) {
        logger.trace(String.format("processWorkload Started. %d", workloadId));

        String resultModelJson = "";

        try {
            ConfigProperties configProperties = new ConfigProperties();
            configProperties.loadProperties();

            CatalogService cs = new CatalogService(configProperties);
            resultModelJson = cs.processWorkload(workloadId);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
        logger.trace("processWorkload Completed.");

        return resultModelJson;
    }
}
