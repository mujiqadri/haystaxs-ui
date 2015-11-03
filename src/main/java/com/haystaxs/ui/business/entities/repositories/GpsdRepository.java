package com.haystaxs.ui.business.entities.repositories;

import com.haystaxs.ui.business.entities.Gpsd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Adnan on 10/21/2015.
 */
@Repository
public class GpsdRepository extends RepositoryBase {
    final static Logger logger = LoggerFactory.getLogger(GpsdRepository.class);

    public GpsdRepository() {
        logger.trace(logger.getName() + " instantiated.");
    }

    public List<Gpsd> getAll(int userId) {
        String sql = String.format("select * from %s.gpsd where user_id = ?", getHsSchemaName());

        //List<Gpsd> resultSet = jdbcTemplate.query(sql, new Object[] { userId }, new GpsdRowMapper());
        List<Gpsd> resultSet = jdbcTemplate.query(sql, new Object[] { userId }, new BeanPropertyRowMapper<Gpsd>(Gpsd.class));

        return resultSet;
    }

    public Gpsd getSingle(int gpsdId, int userId) {
        String sql = String.format("select * from %s.gpsd where gpsd_id = ? and user_id = ?", getHsSchemaName());

        Gpsd result = jdbcTemplate.queryForObject(sql, new Object[] {gpsdId, userId}, new BeanPropertyRowMapper<Gpsd>(Gpsd.class));

        return result;
    }

    public int createNew(Gpsd gpsd, int userId, String originalFileName) {
        String sql = String.format("select nextval('%s.seq_gpsd')", getHsSchemaName());
        int newGpsdId = jdbcTemplate.queryForObject(sql, Integer.class);

        sql = String.format("INSERT INTO %s.gpsd(gpsd_id, user_id, dbname, filename, file_submitted_on, status) VALUES (?, ?, ?, ?, localtimestamp, ?)", getHsSchemaName());
        //sb.append(UUID.randomUUID().toString() + ".sql");
        jdbcTemplate.update(sql, new Object[] {newGpsdId, userId, gpsd.getDbname(), originalFileName, "SUBMITTED"});

        return newGpsdId;
    }
}
