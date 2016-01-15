package com.haystaxs.ui.business.entities.repositories;

import com.haystaxs.ui.business.entities.QueryLog;
import com.haystaxs.ui.business.entities.QueryLogDate;
import com.haystaxs.ui.business.entities.selection.QueryLogMinMaxDates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import java.util.Date;
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

    public List<QueryLog> getAll(int userId) {
        String sql = String.format("select * from %s.query_logs where user_id = ? order by submitted_on", getHsSchemaName());

        List<QueryLog> resultSet = jdbcTemplate.query(sql, new Object[] { userId }, new BeanPropertyRowMapper(QueryLog.class));

        for(QueryLog ql: resultSet) {
            sql = String.format("select * from %s.query_log_dates where query_log_id = ?", getHsSchemaName());

            List<QueryLogDate> qlds = jdbcTemplate.query(sql, new Object[] { ql.getQueryLogId() }, new BeanPropertyRowMapper(QueryLogDate.class));

            ql.setLogDates(qlds);
        }

        return resultSet;
    }

    public QueryLog getById(int queryLogId, int userId) {
        String sql = "select * from haystack.query_logs where query_log_id = ? and user_id = ?";

        QueryLog result = jdbcTemplate.queryForObject(sql, new Object[] { queryLogId, userId }, new BeanPropertyRowMapper<QueryLog>(QueryLog.class));

        return result;
    }

    public int createNew(int userId, String originalFileName, String checksum) {
        String sql = String.format("select nextval('%s.seq_query_log')", getHsSchemaName());
        int newQueryLogId = jdbcTemplate.queryForObject(sql, Integer.class);

        sql = String.format("INSERT INTO %s.query_logs(query_log_id, user_id, submitted_on, status, original_file_name, file_checksum) VALUES (?, ?, localtimestamp, 'UPLOADED', ?, ?)", getHsSchemaName());
        jdbcTemplate.update(sql, new Object[] { newQueryLogId, userId, originalFileName, checksum });

        return newQueryLogId;
    }

    public List<QueryLogDate> getQueryLogDates(int userId, Date fromDate, Date toDate, int pageNo, int pageSize) {
        String sql = String.format("select qld.*, ql.submitted_on, ql.original_file_name, count(0) OVER () as totalRows " +
                " from %s.query_log_dates qld INNER JOIN %s.query_logs ql " +
                " ON ql.query_log_id = qld.query_log_id where ql.user_id = ? " +
                " AND qld.log_date BETWEEN ? AND ? " +
                " ORDER BY qld.log_date DESC " +
                " LIMIT %d OFFSET %d ", getHsSchemaName(), getHsSchemaName(), pageSize, (pageNo-1) * pageSize);

        List<QueryLogDate> resultSet = jdbcTemplate.query(sql, new Object[] { userId, fromDate, toDate }, new BeanPropertyRowMapper(QueryLogDate.class));

        return resultSet;
    }

    public QueryLogMinMaxDates getQueryLogMinMaxDates(String normalizedUsername) {
        String sql = String.format("select min(logsessiontime)::date, max(logsessiontime)::date from %s.queries",
                normalizedUsername);

        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql);
        rowSet.next();

        QueryLogMinMaxDates result = new QueryLogMinMaxDates();
        result.setMinDate(rowSet.getDate(1));
        result.setMaxDate(rowSet.getDate(2));

        return result;
    }
}
