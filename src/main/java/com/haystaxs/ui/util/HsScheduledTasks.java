package com.haystaxs.ui.util;

import com.haystaxs.ui.business.entities.Gpsd;
import com.haystaxs.ui.business.entities.repositories.GpsdRepository;
import com.haystaxs.ui.business.services.HaystaxsLibService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by adnan on 4/17/16.
 */
@Component
public class HsScheduledTasks {
    final static Logger logger = LoggerFactory.getLogger(HsScheduledTasks.class);
    @Autowired
    private HaystaxsLibService haystaxsLibServiceWrapper;
    @Autowired
    private GpsdRepository gpsdRepository;

    public void refreshSchemaAndQueryStats() {
        List<Gpsd> allClusters = gpsdRepository.getAll(1, true);

        for(int i=0; i<allClusters.size(); i++) {
            int clusterID = allClusters.get(i).getClusterId();
            logger.debug("Gonna execute the 'refreshSchemaAndQueryStats' Scheduled task for ClusterID: " + clusterID);
            haystaxsLibServiceWrapper.refeshCluster(clusterID);
        }
    }
}
