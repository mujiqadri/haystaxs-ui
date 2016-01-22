package com.haystaxs.ui.business.services;

import com.haystack.domain.Tables;
import com.haystack.service.CatalogService;
import com.haystack.service.ClusterService;
import com.haystack.util.ConfigProperties;
import com.haystaxs.ui.business.entities.repositories.WorkloadRepository;
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
import java.io.IOException;
import java.util.Map;

/**
 * Created by adnan on 11/23/15.
 */
@Service
public class HaystaxsLibService {
    final static Logger logger = LoggerFactory.getLogger(HaystaxsLibService.class);

    @Autowired
    private WorkloadRepository workloadRepository;
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

    @Async
    public void processWorkload(int workloadId, String normalizedUserName) {
        logger.trace(String.format("processWorkload Started. %d", workloadId));

        String modelJson = "";

        try {
            ConfigProperties configProperties = new ConfigProperties();
            configProperties.loadProperties();

            CatalogService cs = new CatalogService(configProperties);
            modelJson = cs.processWorkload(workloadId);

            if(modelJson != null) {
                workloadRepository.setCompletedOn(workloadId);

                String jsonFileBaseDir = appConfig.getGpsdSaveDirectory() + File.separator + normalizedUserName + File.separator
                        + "workloads" + File.separator;

                try {
                    fileUtil.saveToFile(modelJson.getBytes(), jsonFileBaseDir, Integer.toString(workloadId) + ".json");
                } catch (IOException e) {
                    logger.error("Failed to save workload JSON to file system.", e);
                    return;
                }
            }

            logger.trace("processWorkload Completed.");
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
    }

    public String getGpsdJson(int gpsdId) {
        String result = "{}";

        try {
            ConfigProperties configProperties = new ConfigProperties();
            configProperties.loadProperties();

            CatalogService cs = new CatalogService(configProperties);
            result = cs.getGPSDJson(gpsdId);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }

        return result;
    }

    public Tables getTablesInfoForDbExplorer() {
        Tables result = null;

        try {
            ConfigProperties configProperties = new ConfigProperties();
            configProperties.loadProperties();

            ClusterService cs = new ClusterService(configProperties);
            result = cs.getTablefromGPDBStats(2);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }

        return result;
    }
}
