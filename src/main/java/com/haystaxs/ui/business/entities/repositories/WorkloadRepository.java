package com.haystaxs.ui.business.entities.repositories;

import com.haystaxs.ui.business.entities.Gpsd;
import com.haystaxs.ui.business.entities.Workload;
import com.haystaxs.ui.business.entities.repositories.rowmappers.GpsdRowMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Adnan on 10/21/2015.
 */
@Repository
public class WorkloadRepository extends RepositoryBase {
    final static Logger logger = LoggerFactory.getLogger(WorkloadRepository.class);

    public WorkloadRepository() {
        logger.trace(logger.getName() + " instantiated.");
    }

    public int createNew(int userId, Workload workload) {
        String sql = String.format("select nextval('%s.seq_workload')", getHsSchemaName());
        int newWorkloadId = jdbcTemplate.queryForObject(sql, Integer.class);

        sql = String.format("INSERT INTO %s.workloads (workload_id, gpsd_id, user_id, start_date, end_date, created_on) VALUES (?, ?, ?, ?, ?, localtimestamp)", getHsSchemaName());
        jdbcTemplate.update(sql, new Object[] {newWorkloadId, workload.getGpsdId(), userId, workload.getStartDate(), workload.getEndDate()});

        return newWorkloadId;
    }

    public void setCompletedOn(int workloadId) {
        String sql = String.format("UPDATE %s.workloads SET completed_on  = localtimestamp where workload_id = ?", getHsSchemaName());
        jdbcTemplate.update(sql, new Object[] {workloadId});
    }
}
