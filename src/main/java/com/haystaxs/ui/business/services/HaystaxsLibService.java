package com.haystaxs.ui.business.services;

import com.haystack.service.CatalogService;
import com.haystack.util.ConfigProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

/**
 * Created by adnan on 11/23/15.
 */
@Service
public class HaystaxsLibService {
    final static Logger logger = LoggerFactory.getLogger(HaystaxsLibService.class);

    @Async
    public void createGPSD(int gpsdId, String normalizedUserName, String gpsdFilePath) {
        logger.debug(String.format("createGPSD Started. %d %s %s", gpsdId, normalizedUserName, gpsdFilePath));
        try {
            Thread.sleep(5000);

            /*
            ConfigProperties configProperties = new ConfigProperties();
            configProperties.loadProperties();

            CatalogService cs = new CatalogService(configProperties);
            boolean hadErrors = cs.executeGPSD(gpsdId, normalizedUserName, gpsdFilePath);

            if (hadErrors) {
                logger.debug(String.format("CatalogService.processGPSD(%d, %s, %s) ran with some errors !", gpsdId, normalizedUserName,
                        gpsdFilePath));
            }
            */
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
        logger.debug("createGPSD Completed.");
    }
}
