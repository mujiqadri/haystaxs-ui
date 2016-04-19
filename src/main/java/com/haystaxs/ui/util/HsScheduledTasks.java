package com.haystaxs.ui.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Created by adnan on 4/17/16.
 */
@Component
public class HsScheduledTasks {
    final static Logger logger = LoggerFactory.getLogger(HsScheduledTasks.class);

    public void refreshSchemaStats() {
        logger.debug("Gonna execute the Scheduled task");
    }
}
