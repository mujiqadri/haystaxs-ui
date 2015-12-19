package com.haystaxs.ui.business.entities.repositories;

import com.haystaxs.ui.business.entities.UserQuery;
import com.haystaxs.ui.business.entities.selection.QueryType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

        if(startTime == null || startTime.isEmpty()) {
            startTime = "00:00:00";
        }
        if(endTime == null || endTime.isEmpty()) {
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
                String.format(" limit %d OFFSET %d ", pageSize, (pageNo-1) * pageSize);

        List<UserQuery> resultSet = jdbcTemplate.query(sql, params.toArray(), new BeanPropertyRowMapper<UserQuery>(UserQuery.class));

        return resultSet;
    }

    public List<String> getQueryTypes(String normalizedUserName, String forDate) {
        String sql = String.format("select DISTINCT qrytype from %s.queries " +
                " where to_char(logsessiontime, 'DD-MON-YYYY') = ? ", normalizedUserName);

        List<String> resultSet = jdbcTemplate.queryForList(sql, new Object[] { forDate.toUpperCase() }, String.class);

        return resultSet;
    }
}
