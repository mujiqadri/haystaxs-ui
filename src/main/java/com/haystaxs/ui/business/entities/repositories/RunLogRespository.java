package com.haystaxs.ui.business.entities.repositories;

import com.haystaxs.ui.business.entities.Gpsd;
import com.haystaxs.ui.business.entities.RunLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Created by Adnan on 10/22/2015.
 */
@Repository
public class RunLogRespository extends RepositoryBase{
    final static Logger logger = LoggerFactory.getLogger(RunLogRespository.class);

    public RunLogRespository() {
        logger.debug("RunLogRepository instantiated.");
    }

    public List<RunLog> getAllForGpsd(int gpsdId, int userId) {
        String sql = "select * from haystack.run_log where gpsd_id = ? and user_id = ? order by workload_submitted_on";

        //List<Gpsd> resultSet = jdbcTemplate.query(sql, new Object[] { userId }, new GpsdRowMapper());
        List<RunLog> resultSet = jdbcTemplate.query(sql, new Object[] { gpsdId, userId }, new BeanPropertyRowMapper<RunLog>(RunLog.class));

        return resultSet;
    }

    public RunLog getRunLogById(int runLogId, int userId) {
        String sql = "select * from haystack.run_log where run_id = ? and user_id = ?";

        RunLog runLog = jdbcTemplate.queryForObject(sql, new Object[] { runLogId, userId }, new BeanPropertyRowMapper<RunLog>(RunLog.class));

        return runLog;
    }

    public int createNew(RunLog runLog, int gpsdId, int userId, StringBuilder sb) {
        String sql = "select nextval('haystack.seq_run_log')";
        int newRunLogId = jdbcTemplate.queryForObject(sql, Integer.class);

        sql = "INSERT INTO haystack.run_log(run_id, runlog_name, gpsd_id, user_id, directory_name, workload_submitted_on, status) VALUES (?, ?, ?, ?, ?, localtimestamp, ?)";
        sb.append(UUID.randomUUID().toString());
        jdbcTemplate.update(sql, new Object[] {newRunLogId, runLog.getRunlogName(), gpsdId, userId, sb.toString(), "SUBMITTED"});

        return newRunLogId;
    }
}
