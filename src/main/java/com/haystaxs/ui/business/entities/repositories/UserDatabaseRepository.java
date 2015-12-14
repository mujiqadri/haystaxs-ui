package com.haystaxs.ui.business.entities.repositories;

import com.haystaxs.ui.business.entities.Gpsd;
import com.haystaxs.ui.business.entities.UserQuery;
import com.haystaxs.ui.business.entities.repositories.rowmappers.GpsdRowMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.validation.ObjectError;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

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

    public List<Map<String, Object>> getQueryCountByCategory(String normalizedUserName, String forDate) {
        String sql = String.format("SELECT  qrytype, count(*) as count\n" +
                "FROM %s.queries\n" +
                "where to_char(logsessiontime, 'DD-MON-YYYY') = ?\n" +
                "group by  qrytype\n" +
                "order by 2 desc, 1", normalizedUserName);

        List<Map<String, Object>> resultSet = jdbcTemplate.queryForList(sql, new Object[]{forDate.toUpperCase()});

        return resultSet;
    }

    public List<UserQuery> getQueries(String normalizedUserName, String forDate,
                                      String dbNameLike, String userNameLike, String durationGreaterThan,
                                      /*String startTime, String endTime,*/ String sqlLike,
                                      String queryType,
                                      int pageSize, int pageNo) {
        String whereClause = "";
        ArrayList<Object> params = new ArrayList<Object>();

        params.add(forDate.toUpperCase());

        if (dbNameLike != null && !dbNameLike.isEmpty()) {
            whereClause = String.format(" AND logdatabase LIKE '%%%s%%'", dbNameLike);
            //params.add(dbNameLike);
        }
        if (userNameLike != null && !userNameLike.isEmpty()) {
            whereClause += String.format(" AND loguser LIKE '%%%s%%'", userNameLike);
            //params.add(userNameLike);
        }
        if (durationGreaterThan != null && !durationGreaterThan.isEmpty()) {
            whereClause += " AND extract(epoch from logduration) > ?";
            params.add(Double.parseDouble(durationGreaterThan));
        }
        if (sqlLike != null && !sqlLike.isEmpty()) {
            whereClause += String.format(" AND sql LIKE '%%%s%%'", sqlLike);
            //params.add(userNameLike);
        }
        if (queryType != null && !queryType.equals("ALL")) {
            whereClause += String.format(" AND qrytype = '%s'", queryType);
        }

        String sql = String.format("SELECT logdatabase, loguser, logtimemin as queryStartTime, " +
                "logtimemax as queryEndTime, extract(epoch from logduration) as durationSeconds, sql, qryType, count(0) OVER () as totalRows " +
                "FROM %s.queries ", normalizedUserName) +
                "where to_char(logsessiontime, 'DD-MON-YYYY') = ? " +
                whereClause +
                "ORDER BY logtimemin ASC " +
                String.format("limit %d OFFSET %d", pageSize, (pageNo-1) * pageSize);

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
