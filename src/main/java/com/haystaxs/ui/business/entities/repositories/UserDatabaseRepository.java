package com.haystaxs.ui.business.entities.repositories;

import com.haystaxs.ui.business.entities.UserQueryChartData;
import com.haystaxs.ui.business.entities.UserQuery;
import com.haystaxs.ui.business.entities.repositories.rowmappers.GpsdRowMapper;
import com.haystaxs.ui.business.entities.repositories.rowmappers.UserQueryChartDataRowMapper;
import com.haystaxs.ui.business.entities.selection.QueryType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Adnan on 10/21/2015.
 */
@Repository
public class UserDatabaseRepository extends RepositoryBase {
    final static Logger logger = LoggerFactory.getLogger(UserDatabaseRepository.class);

    public UserDatabaseRepository() {
        logger.trace(logger.getName() + " instantiated.");
    }

    public List<UserQuery> getTopQueries(String normalizedUserName, String forDate) {
        String sql = String.format("SELECT  logdatabase, loguser, logtimemin as queryStartTime, logtimemax as queryEndTime, extract(epoch from logduration) as durationSeconds,sql\n" +
                "FROM %s.queries\n" +
                "where to_char(logsessiontime, 'DD-MON-YYYY') = ?\n" +
                "order by 5 desc limit 10", normalizedUserName);

        List<UserQuery> resultSet = jdbcTemplate.query(sql, new Object[]{forDate.toUpperCase()}, new BeanPropertyRowMapper<UserQuery>(UserQuery.class));

        return resultSet;
    }

    @Cacheable(value = "dataCache")
    public List<QueryType> getQueryCountByCategory(String normalizedUserName, String forDate) {
        String sql = String.format("SELECT  qrytype as queryType, count(*) as count, sum(extract(epoch from logduration)) as totalDuration\n" +
                "FROM %s.queries\n" +
                "where to_char(logsessiontime, 'DD-MON-YYYY') = ?\n" +
                "group by  qrytype\n" +
                "order by 2 desc", normalizedUserName);

        List<QueryType> resultSet = jdbcTemplate.query(sql, new Object[]{forDate.toUpperCase()}, new BeanPropertyRowMapper<QueryType>(QueryType.class));

        return resultSet;
    }

    public List<UserQuery> getQueries(String normalizedUserName, String startDate, String endDate,
                                      String startTime, String endTime,
                                      String dbNameLike, String userNameLike, String durationGreaterThan,
                                      String sqlLike,
                                      String queryType,
                                      int pageSize, int pageNo,
                                      String orderBy) {
        String whereClause = " where ";
        ArrayList<Object> params = new ArrayList<Object>();

        if (startTime == null || startTime.isEmpty()) {
            startTime = "00:00:00";
        }
        if (endTime == null || endTime.isEmpty()) {
            endTime = "23:59:59";
        }

        whereClause += String.format(" logsessiontime::TIMESTAMP >= '%s %s'::timestamp", startDate, startTime);
        whereClause += String.format(" AND logsessiontime::TIMESTAMP <= '%s %s'::timestamp", endDate, endTime);

        if (dbNameLike != null && !dbNameLike.isEmpty()) {
            whereClause += String.format(" AND logdatabase LIKE '%%%s%%'\n", dbNameLike);
            //params.add(dbNameLike);
        }
        if (userNameLike != null && !userNameLike.isEmpty()) {
            whereClause += String.format(" AND loguser LIKE '%%%s%%'\n", userNameLike);
            //params.add(userNameLike);
        }
        if (durationGreaterThan != null && !durationGreaterThan.isEmpty()) {
            whereClause += " AND extract(epoch from logduration) > ?\n";
            params.add(Double.parseDouble(durationGreaterThan));
        }
        if (sqlLike != null && !sqlLike.isEmpty()) {
            whereClause += String.format(" AND sql LIKE '%%%s%%'\n", sqlLike);
            //params.add(userNameLike);
        }
        if (queryType != null && !queryType.equals("ALL")) {
            whereClause += String.format(" AND qrytype = '%s'\n", queryType);
        }

        String sql = String.format("SELECT logdatabase, loguser, logtimemin as queryStartTime, " +
                " logtimemax as queryEndTime, extract(epoch from logduration) as durationSeconds, sql, qryType, count(0) OVER () as totalRows " +
                " FROM %s.queries ", normalizedUserName) +
                whereClause +
                " ORDER BY " + orderBy +
                String.format(" limit %d OFFSET %d ", pageSize, (pageNo - 1) * pageSize);

        List<UserQuery> resultSet = jdbcTemplate.query(sql, params.toArray(), new BeanPropertyRowMapper<UserQuery>(UserQuery.class));

        return resultSet;
    }

    public List<String> getQueryTypes(String normalizedUserName, String forDate) {
        String sql = String.format("select DISTINCT qrytype from %s.queries " +
                " where to_char(logsessiontime, 'DD-MON-YYYY') = ? ", normalizedUserName);

        List<String> resultSet = jdbcTemplate.queryForList(sql, new Object[]{forDate.toUpperCase()}, String.class);

        return resultSet;
    }

    @Cacheable(value = "dataCache")
    public List<UserQueryChartData> getQueriesForChart(String normalizedUserName) {
        String sql = String.format("SELECT DATE,\n" +
                "       sum(TOTAL_DURATION) TOTAL_DURATION,\n" +
                "       sum(TOTAL_COUNT) TOTAL_COUNT,\n" +
                "       sum(ANALYZE_DURATION) ANALYZE_DURATION,\n" +
                "        sum(ANALYZE_COUNT) ANALYZE_COUNT,\n" +
                "       sum(COMMIT_DURATION) COMMIT_DURATION,\n" +
                "        sum(COMMIT_COUNT) COMMIT_COUNT,\n" +
                "       sum(CREATE_EXTERNAL_TABLE_DURATION) CREATE_EXTERNAL_TABLE_DURATION,\n" +
                "        sum(CREATE_EXTERNAL_TABLE_COUNT) CREATE_EXTERNAL_TABLE_COUNT,\n" +
                "       sum(CREATE_TABLE_DURATION) CREATE_TABLE_DURATION,\n" +
                "        sum(CREATE_TABLE_COUNT) CREATE_TABLE_COUNT,\n" +
                "       sum(DELETE_DURATION) DELETE_DURATION,\n" +
                "        sum(DELETE_COUNT) DELETE_COUNT,\n" +
                "       sum(DROP_TABLE_DURATION) DROP_TABLE_DURATION,\n" +
                "        sum(DROP_TABLE_COUNT) DROP_TABLE_COUNT,\n" +
                "       sum(EXCLUSIVE_LOCK_DURATION) EXCLUSIVE_LOCK_DURATION,\n" +
                "        sum(EXCLUSIVE_LOCK_COUNT) EXCLUSIVE_LOCK_COUNT,\n" +
                "       sum(INSERT_DURATION) INSERT_DURATION,\n" +
                "        sum(INSERT_COUNT) INSERT_COUNT,\n" +
                "       sum(INTERNAL_DURATION) INTERNAL_DURATION,\n" +
                "        sum(INTERNAL_COUNT) INTERNAL_COUNT,\n" +
                "       sum(OTHERS_DURATION) OTHERS_DURATION,\n" +
                "        sum(OTHERS_COUNT) OTHERS_COUNT,\n" +
                "       sum(SELECT_DURATION) SELECT_DURATION,\n" +
                "        sum(SELECT_COUNT) SELECT_COUNT,\n" +
                "       sum(SHOW_CONFIGURATION_DURATION) SHOW_CONFIGURATION_DURATION,\n" +
                "        sum(SHOW_CONFIGURATION_COUNT) SHOW_CONFIGURATION_COUNT,\n" +
                "       sum(SHOW_DURATION) SHOW_DURATION,\n" +
                "        sum(SHOW_COUNT) SHOW_COUNT,\n" +
                "       sum(TRANSACTION_OPERATION_DURATION) TRANSACTION_OPERATION_DURATION,\n" +
                "        sum(TRANSACTION_OPERATION_COUNT) TRANSACTION_OPERATION_COUNT,\n" +
                "       sum(TRUNCATE_TABLE_DURATION) TRUNCATE_TABLE_DURATION,\n" +
                "        sum(TRUNCATE_TABLE_COUNT) TRUNCATE_TABLE_COUNT,\n" +
                "       sum(UPDATE_DURATION) UPDATE_DURATION,\n" +
                "        sum(UPDATE_COUNT) UPDATE_COUNT\n" +
                "FROM (\n" +
                "       select logsessiontime as DATE,\n" +
                "              CASE WHEN qrytype IS NULL THEN  duration END as TOTAL_DURATION,\n" +
                "              CASE WHEN qrytype IS NULL THEN  count END as TOTAL_COUNT,\n" +
                "              CASE WHEN qrytype = 'ANALYZE' THEN duration END as ANALYZE_DURATION,\n" +
                "              CASE WHEN qrytype = 'ANALYZE' THEN  count END as ANALYZE_COUNT,\n" +
                "              CASE WHEN qrytype = 'COMMIT' THEN duration END as COMMIT_DURATION,\n" +
                "              CASE WHEN qrytype = 'COMMIT' THEN count END as COMMIT_COUNT,\n" +
                "              CASE WHEN qrytype = 'CREATE EXTERNAL TABLE' THEN duration END as CREATE_EXTERNAL_TABLE_DURATION,\n" +
                "              CASE WHEN qrytype = 'CREATE EXTERNAL TABLE' THEN count END as CREATE_EXTERNAL_TABLE_COUNT,\n" +
                "              CASE WHEN qrytype = 'CREATE TABLE' THEN duration END as CREATE_TABLE_DURATION,\n" +
                "              CASE WHEN qrytype = 'CREATE TABLE' THEN count END as CREATE_TABLE_COUNT,\n" +
                "              CASE WHEN qrytype = 'DELETE' THEN duration END as DELETE_DURATION,\n" +
                "              CASE WHEN qrytype = 'DELETE' THEN count END as DELETE_COUNT,\n" +
                "              CASE WHEN qrytype = 'DROP TABLE' THEN duration END as DROP_TABLE_DURATION,\n" +
                "              CASE WHEN qrytype = 'DROP TABLE' THEN count END as DROP_TABLE_COUNT,\n" +
                "              CASE WHEN qrytype = 'EXCLUSIVE LOCK' THEN duration END as EXCLUSIVE_LOCK_DURATION,\n" +
                "              CASE WHEN qrytype = 'EXCLUSIVE LOCK' THEN count END as EXCLUSIVE_LOCK_COUNT,\n" +
                "              CASE WHEN qrytype = 'INSERT' THEN duration END as INSERT_DURATION,\n" +
                "              CASE WHEN qrytype = 'INSERT' THEN count END as INSERT_COUNT,\n" +
                "              CASE WHEN qrytype = 'INTERNAL' THEN duration END as INTERNAL_DURATION,\n" +
                "              CASE WHEN qrytype = 'INTERNAL' THEN count END as INTERNAL_COUNT,\n" +
                "              CASE WHEN qrytype = 'OTHERS' THEN duration END as OTHERS_DURATION,\n" +
                "              CASE WHEN qrytype = 'OTHERS' THEN count END as OTHERS_COUNT,\n" +
                "              CASE WHEN qrytype = 'SELECT' THEN duration END as SELECT_DURATION,\n" +
                "              CASE WHEN qrytype = 'SELECT' THEN count END as SELECT_COUNT,\n" +
                "              CASE WHEN qrytype = 'SHOW CONFIGURATION' THEN duration END as SHOW_CONFIGURATION_DURATION,\n" +
                "              CASE WHEN qrytype = 'SHOW CONFIGURATION' THEN count END as SHOW_CONFIGURATION_COUNT,\n" +
                "              CASE WHEN qrytype = 'SHOW' THEN duration END as SHOW_DURATION,\n" +
                "              CASE WHEN qrytype = 'SHOW' THEN count END as SHOW_COUNT,\n" +
                "              CASE WHEN qrytype = 'TRANSACTION-OPERATION' THEN duration END as TRANSACTION_OPERATION_DURATION,\n" +
                "              CASE WHEN qrytype = 'TRANSACTION-OPERATION' THEN count END as TRANSACTION_OPERATION_COUNT,\n" +
                "              CASE WHEN qrytype = 'TRUNCATE TABLE' THEN duration END as TRUNCATE_TABLE_DURATION,\n" +
                "              CASE WHEN qrytype = 'TRUNCATE TABLE' THEN count END as TRUNCATE_TABLE_COUNT,\n" +
                "              CASE WHEN qrytype = 'UPDATE' THEN duration END as UPDATE_DURATION,\n" +
                "              CASE WHEN qrytype = 'UPDATE' THEN count END as UPDATE_COUNT\n" +
                "\n" +
                "       FROM (\n" +
                "              select logsessiontime::date, qrytype, count(0), coalesce(sum(extract(epoch from logduration)),0) as duration\n" +
                "              from %s.queries\n" +
                "              --where logsessiontime::date between '2015-01-01' and '2016-01-01'\n" +
                "              group by rollup(logsessiontime::date, qrytype)\n" +
                "            ) AS Y\n" +
                "     ) AS X\n" +
                "where DATE IS NOT NULL\n" +
                "group by date\n" +
                "order by date;\n", normalizedUserName);

        List<UserQueryChartData> result = jdbcTemplate.query(sql, new UserQueryChartDataRowMapper());

        return result;
    }
}
