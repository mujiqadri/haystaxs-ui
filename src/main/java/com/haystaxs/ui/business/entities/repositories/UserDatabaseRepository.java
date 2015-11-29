package com.haystaxs.ui.business.entities.repositories;

import com.haystaxs.ui.business.entities.Gpsd;
import com.haystaxs.ui.business.entities.UserQuery;
import com.haystaxs.ui.business.entities.repositories.rowmappers.GpsdRowMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

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
                "order by 5 desc limit 15;", normalizedUserName);

        List<UserQuery> resultSet = jdbcTemplate.query(sql, new Object[] { forDate.toUpperCase() }, new BeanPropertyRowMapper<UserQuery>(UserQuery.class));

        return resultSet;
    }

    public List<Map<String, Object>> getQueryCountByCategory(String normalizedUserName, String forDate) {
        String sql = String.format("SELECT  qrytype, count(*) as count\n" +
                "FROM %s.queries\n" +
                "where to_char(logsessiontime, 'DD-MON-YYYY') = ?\n" +
                "group by  qrytype\n" +
                "order by 2 desc,1", normalizedUserName);

        List<Map<String, Object>> resultSet = jdbcTemplate.queryForList(sql, new Object[] { forDate.toUpperCase() });

        return resultSet;
    }
}
