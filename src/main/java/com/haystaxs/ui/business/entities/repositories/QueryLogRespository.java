package com.haystaxs.ui.business.entities.repositories;

import com.haystaxs.ui.business.entities.QueryLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Adnan on 10/22/2015.
 */
@Repository
public class QueryLogRespository extends RepositoryBase{
    final static Logger logger = LoggerFactory.getLogger(QueryLogRespository.class);

    public QueryLogRespository() {
        logger.trace(logger.getName() + " instantiated.");

    }

    public List<QueryLog> getAllForGpsd(int gpsdId, int userId) {
        String sql = "select * from haystack.query_log where gpsd_id = ? and user_id = ? order by submitted_on";

        //List<Gpsd> resultSet = jdbcTemplate.query(sql, new Object[] { userId }, new GpsdRowMapper());
        List<QueryLog> resultSet = jdbcTemplate.query(sql, new Object[] { gpsdId, userId }, new BeanPropertyRowMapper(QueryLog.class));

        return resultSet;
    }

    public QueryLog getById(int queryLogId, int userId) {
        String sql = "select * from haystack.query_log where query_log_id = ? and user_id = ?";

        QueryLog result = jdbcTemplate.queryForObject(sql, new Object[] { queryLogId, userId }, new BeanPropertyRowMapper<QueryLog>(QueryLog.class));

        return result;
    }

    public int createNew(QueryLog queryLog, int gpsdId, int userId) {
        String sql = String.format("select nextval('%s.seq_query_log')", getHsSchemaName());
        int newQueryLogId = jdbcTemplate.queryForObject(sql, Integer.class);

        sql = String.format("INSERT INTO %s.query_log(query_log_id, gpsd_id, user_id, submitted_on) VALUES (?, ?, ?, localtimestamp)", getHsSchemaName());
        jdbcTemplate.update(sql, new Object[] { newQueryLogId, gpsdId, userId });

        return newQueryLogId;
    }
}
