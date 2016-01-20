package com.haystaxs.ui.business.entities.repositories;

import com.haystaxs.ui.business.entities.UserQueryChartData;
import com.haystaxs.ui.business.entities.UserQuery;
import com.haystaxs.ui.business.entities.repositories.rowmappers.HourlyAvgQueryChartDataMapper;
import com.haystaxs.ui.business.entities.repositories.rowmappers.TimelineQueryChartDataMapper;
import com.haystaxs.ui.business.entities.selection.QueryLogMinMaxDateTimes;
import com.haystaxs.ui.business.entities.selection.QueryType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Arrays;
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

    public List<String> getAllQueryTypes() {
        return(Arrays.asList(
        "UPDATE",
        "EXCLUSIVE LOCK",
        "SHOW",
        "INTERNAL",
        "DELETE",
        "INSERT",
        "OTHERS",
        "COMMIT",
        "CREATE EXTERNAL TABLE",
        "DROP TABLE",
        "CREATE TABLE",
        "SELECT",
        "ANALYZE",
        "SET CONFIGURATION",
        "TRUNCATE TABLE",
        "TRANSACTION-OPERATION"));
    }

    public QueryLogMinMaxDateTimes getQueryLogMinMaxDates(String normalizedUsername) {
        String sql = String.format("select min(logsessiontime)::date, max(logsessiontime)::date, " +
                        "min(logsessiontime)::time, max(logsessiontime)::time  from %s.queries",
                normalizedUsername);

        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql);
        rowSet.next();

        QueryLogMinMaxDateTimes result = new QueryLogMinMaxDateTimes();
        result.setMinDate(rowSet.getDate(1));
        result.setMaxDate(rowSet.getDate(2));
        result.setMinTime(rowSet.getTime(3));
        result.setMaxTime(rowSet.getTime(4));

        return result;
    }

    //@Cacheable(value = "dataCache")
    public List<UserQueryChartData> getQueryStatsForChart(String normalizedUserName, String fromDate, String toDate,
                                                          String dbName, String userName) {
        String whereClause = " ";
        boolean prependAnd = false;

        if(fromDate != null && toDate != null && !fromDate.isEmpty()  && !toDate.isEmpty()) {
            whereClause += " WHERE logsessiontime::date >= '" + fromDate + "' ";
            whereClause += " AND logsessiontime::date <= '" + toDate + "' ";
            prependAnd = true;
        }

        if(dbName != null && !dbName.isEmpty()) {
            whereClause += (prependAnd ? " AND " : " WHERE ") + " logdatabase = '" + dbName + "' ";
            prependAnd = true;
        }

        if(userName != null && !userName.isEmpty()) {
            whereClause += (prependAnd ? " AND " : " WHERE ") + " loguser = '" + userName + "' ";
        }

        String sql = String.format("SELECT DATE,\n" +
                "       coalesce(sum(TOTAL_DURATION), 0) TOTAL_DURATION,\n" +
                "       coalesce(sum(TOTAL_COUNT), 0) TOTAL_COUNT,\n" +
                "       coalesce(sum(ANALYZE_DURATION), 0) ANALYZE_DURATION,\n" +
                "       coalesce(sum(ANALYZE_COUNT), 0) ANALYZE_COUNT,\n" +
                "       coalesce(sum(COMMIT_DURATION), 0) COMMIT_DURATION,\n" +
                "       coalesce(sum(COMMIT_COUNT), 0) COMMIT_COUNT,\n" +
                "       coalesce(sum(CREATE_EXTERNAL_TABLE_DURATION), 0) CREATE_EXTERNAL_TABLE_DURATION,\n" +
                "       coalesce(sum(CREATE_EXTERNAL_TABLE_COUNT), 0) CREATE_EXTERNAL_TABLE_COUNT,\n" +
                "       coalesce(sum(CREATE_TABLE_DURATION), 0) CREATE_TABLE_DURATION,\n" +
                "       coalesce(sum(CREATE_TABLE_COUNT), 0) CREATE_TABLE_COUNT,\n" +
                "       coalesce(sum(DELETE_DURATION), 0) DELETE_DURATION,\n" +
                "       coalesce(sum(DELETE_COUNT), 0) DELETE_COUNT,\n" +
                "       coalesce(sum(DROP_TABLE_DURATION), 0) DROP_TABLE_DURATION,\n" +
                "       coalesce(sum(DROP_TABLE_COUNT), 0) DROP_TABLE_COUNT,\n" +
                "       coalesce(sum(EXCLUSIVE_LOCK_DURATION), 0) EXCLUSIVE_LOCK_DURATION,\n" +
                "       coalesce(sum(EXCLUSIVE_LOCK_COUNT), 0) EXCLUSIVE_LOCK_COUNT,\n" +
                "       coalesce(sum(INSERT_DURATION), 0) INSERT_DURATION,\n" +
                "       coalesce(sum(INSERT_COUNT), 0) INSERT_COUNT,\n" +
                "       coalesce(sum(INTERNAL_DURATION), 0) INTERNAL_DURATION,\n" +
                "       coalesce(sum(INTERNAL_COUNT), 0) INTERNAL_COUNT,\n" +
                "       coalesce(sum(OTHERS_DURATION), 0) OTHERS_DURATION,\n" +
                "       coalesce(sum(OTHERS_COUNT), 0) OTHERS_COUNT,\n" +
                "       coalesce(sum(SELECT_DURATION), 0) SELECT_DURATION,\n" +
                "       coalesce(sum(SELECT_COUNT), 0) SELECT_COUNT,\n" +
                "       coalesce(sum(SHOW_CONFIGURATION_DURATION), 0) SHOW_CONFIGURATION_DURATION,\n" +
                "       coalesce(sum(SHOW_CONFIGURATION_COUNT), 0) SHOW_CONFIGURATION_COUNT,\n" +
                "       coalesce(sum(SHOW_DURATION), 0) SHOW_DURATION,\n" +
                "       coalesce(sum(SHOW_COUNT), 0) SHOW_COUNT,\n" +
                "       coalesce(sum(TRANSACTION_OPERATION_DURATION), 0) TRANSACTION_OPERATION_DURATION,\n" +
                "       coalesce(sum(TRANSACTION_OPERATION_COUNT), 0) TRANSACTION_OPERATION_COUNT,\n" +
                "       coalesce(sum(TRUNCATE_TABLE_DURATION), 0) TRUNCATE_TABLE_DURATION,\n" +
                "       coalesce(sum(TRUNCATE_TABLE_COUNT), 0) TRUNCATE_TABLE_COUNT,\n" +
                "       coalesce(sum(UPDATE_DURATION), 0) UPDATE_DURATION,\n" +
                "       coalesce(sum(UPDATE_COUNT), 0) UPDATE_COUNT\n" +
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
                "              select logsessiontime::date, qrytype, count(0), round(coalesce(sum(extract(epoch from logduration)),0)) as duration\n" +
                "              from %s.queries\n" +
                "              %2s\n" +
                "              group by rollup(logsessiontime::date, qrytype)\n" +
                "            ) AS Y\n" +
                "     ) AS X\n" +
                "where DATE IS NOT NULL\n" +
                "group by rollup(date)\n" +
                "order by date;\n", normalizedUserName, whereClause);

        List<UserQueryChartData> result = jdbcTemplate.query(sql, new TimelineQueryChartDataMapper());

        return result;
    }

    //@Cacheable(value = "dataCache")
    public List<UserQueryChartData> getHourlyAvgQueryStatsForChart(String normalizedUserName, String fromDate, String toDate,
                                                                   String dbName, String userName) {
        String whereClause = " ";

        if(fromDate != null && toDate != null && !fromDate.isEmpty()  && !toDate.isEmpty()) {
            whereClause += " AND logsessiontime::date >= '" + fromDate + "' ";
            whereClause += " AND logsessiontime::date <= '" + toDate + "' ";
        }

        if(dbName != null && !dbName.isEmpty()) {
            whereClause += " AND logdatabase = '" + dbName + "' ";
        }

        if(userName != null && !userName.isEmpty()) {
            whereClause += " AND loguser = '" + userName + "' ";
        }

        String sql = String.format("select \n" +
                "\tdate,\n" +
                "\tcoalesce(sum(ANALYZE_DURATION), 0) ANALYZE_DURATION,\n" +
                "\tcoalesce(sum(COMMIT_DURATION), 0) COMMIT_DURATION,\n" +
                "\tcoalesce(sum(CREATE_EXTERNAL_TABLE_DURATION), 0) CREATE_EXTERNAL_TABLE_DURATION,\n" +
                "\tcoalesce(sum(CREATE_TABLE_DURATION), 0) CREATE_TABLE_DURATION,\n" +
                "\tcoalesce(sum(DELETE_DURATION), 0) DELETE_DURATION,\n" +
                "\tcoalesce(sum(DROP_TABLE_DURATION), 0) DROP_TABLE_DURATION,\n" +
                "\tcoalesce(sum(EXCLUSIVE_LOCK_DURATION), 0) EXCLUSIVE_LOCK_DURATION,\n" +
                "\tcoalesce(sum(INSERT_DURATION), 0) INSERT_DURATION,\n" +
                "\tcoalesce(sum(INTERNAL_DURATION), 0) INTERNAL_DURATION,\n" +
                "\tcoalesce(sum(OTHERS_DURATION), 0) OTHERS_DURATION,\n" +
                "\tcoalesce(sum(SELECT_DURATION), 0) SELECT_DURATION,\n" +
                "\tcoalesce(sum(SHOW_CONFIGURATION_DURATION), 0) SHOW_CONFIGURATION_DURATION,\n" +
                "\tcoalesce(sum(SHOW_DURATION), 0) SHOW_DURATION,\n" +
                "\tcoalesce(sum(TRANSACTION_OPERATION_DURATION), 0) TRANSACTION_OPERATION_DURATION,\n" +
                "\tcoalesce(sum(TRUNCATE_TABLE_DURATION), 0) TRUNCATE_TABLE_DURATION,\n" +
                "\tcoalesce(sum(UPDATE_DURATION), 0) UPDATE_DURATION\n" +
                "FROM (\t\n" +
                "\tselect \n" +
                "\t\tdate,\n" +
                "\t\tCASE WHEN qrytype = 'ANALYZE' THEN duration END as ANALYZE_DURATION,\n" +
                "\t\tCASE WHEN qrytype = 'COMMIT' THEN duration END as COMMIT_DURATION,\n" +
                "\t\tCASE WHEN qrytype = 'CREATE EXTERNAL TABLE' THEN duration END as CREATE_EXTERNAL_TABLE_DURATION,\n" +
                "\t\tCASE WHEN qrytype = 'CREATE TABLE' THEN duration END as CREATE_TABLE_DURATION,\n" +
                "\t\tCASE WHEN qrytype = 'DELETE' THEN duration END as DELETE_DURATION,\n" +
                "\t\tCASE WHEN qrytype = 'DROP TABLE' THEN duration END as DROP_TABLE_DURATION,\n" +
                "\t\tCASE WHEN qrytype = 'EXCLUSIVE LOCK' THEN duration END as EXCLUSIVE_LOCK_DURATION,\n" +
                "\t\tCASE WHEN qrytype = 'INSERT' THEN duration END as INSERT_DURATION,\n" +
                "\t\tCASE WHEN qrytype = 'INTERNAL' THEN duration END as INTERNAL_DURATION,\n" +
                "\t\tCASE WHEN qrytype = 'OTHERS' THEN duration END as OTHERS_DURATION,\n" +
                "\t\tCASE WHEN qrytype = 'SELECT' THEN duration END as SELECT_DURATION,\n" +
                "\t\tCASE WHEN qrytype = 'SHOW CONFIGURATION' THEN duration END as SHOW_CONFIGURATION_DURATION,\n" +
                "\t\tCASE WHEN qrytype = 'SHOW' THEN duration END as SHOW_DURATION,\n" +
                "\t\tCASE WHEN qrytype = 'TRANSACTION-OPERATION' THEN duration END as TRANSACTION_OPERATION_DURATION,\n" +
                "\t\tCASE WHEN qrytype = 'TRUNCATE TABLE' THEN duration END as TRUNCATE_TABLE_DURATION,\n" +
                "\t\tCASE WHEN qrytype = 'UPDATE' THEN duration END as UPDATE_DURATION\n" +
                "\tFROM (\n" +
                "\t\tselect qrytype, extract(hour from logsessiontime) as date, round(avg(extract(epoch from logduration))) as duration\n" +
                "\t\tfrom %s.queries\n" +
                "\t\twhere extract(epoch from logduration) > 0\n" +
                "\t\t--and logsessiontime < '2016-11-01' and logsessiontime >= '2015-10-01'\n" +
                "\t\t--and logsessiontime::date between '2015-10-01' and '2015-11-01'\n" +
                " %2s " +
                "\t\tgroup by EXTRACT(hour from logsessiontime),qrytype\n" +
                "\t\tORDER BY EXTRACT(hour from logsessiontime) ,qrytype\n" +
                "\t) AS A\n" +
                ") AS B\n" +
                "group by rollup(date)\n" +
                "order by date", normalizedUserName, whereClause);

        List<UserQueryChartData> result = jdbcTemplate.query(sql, new HourlyAvgQueryChartDataMapper());

        return result;
    }

    public List<String> getDbNames(String normalizedUserName) {
        String sql = String.format("select value from %s.query_metadata where type = 'dbname' order by 1", normalizedUserName);

        List<String> resultSet = jdbcTemplate.queryForList(sql, String.class);

        return resultSet;
    }

    public List<String> getUserNames(String normalizedUserName) {
        String sql = String.format("select value from %s.query_metadata where type = 'username' order by 1", normalizedUserName);

        List<String> resultSet = jdbcTemplate.queryForList(sql, String.class);

        return resultSet;
    }
}
