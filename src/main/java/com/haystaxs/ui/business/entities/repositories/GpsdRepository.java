package com.haystaxs.ui.business.entities.repositories;

import com.haystaxs.ui.business.entities.Gpsd;
import com.haystaxs.ui.business.entities.repositories.rowmappers.GpsdRowMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Created by Adnan on 10/21/2015.
 */
@Repository
public class GpsdRepository extends RepositoryBase {
    final static Logger logger = LoggerFactory.getLogger(GpsdRepository.class);

    public GpsdRepository() {
        logger.debug("GpsdRepository instantiated.");
    }

    public List<Gpsd> getAllForUser(int userId) {
        String sql = "select * from haystack.gpsd where user_id = ?";

        //List<Gpsd> resultSet = jdbcTemplate.query(sql, new Object[] { userId }, new GpsdRowMapper());
        List<Gpsd> resultSet = jdbcTemplate.query(sql, new Object[] { userId }, new BeanPropertyRowMapper<Gpsd>(Gpsd.class));

        return resultSet;
    }

    public Gpsd getSingle(int gpsdId, int userId) {
        String sql = String.format("select * from %s.gpsd where gpsd_id = ? and user_id = ?", hsSchemaName());

        Gpsd result = jdbcTemplate.queryForObject(sql, new Object[] {gpsdId, userId}, new BeanPropertyRowMapper<Gpsd>(Gpsd.class));

        return result;
    }

    public int createNew(Gpsd gpsd, int userId, StringBuilder sb) {
        String sql = String.format("select nextval('%s.seq_gpsd')", hsSchemaName());
        int newGpsdId = jdbcTemplate.queryForObject(sql, Integer.class);

        sql = String.format("INSERT INTO %s.gpsd(gpsd_id, user_id, dbname, filename, file_submitted_on, status) VALUES (?, ?, ?, ?, localtimestamp, ?)", hsSchemaName());
        sb.append(UUID.randomUUID().toString() + ".sql");
        jdbcTemplate.update(sql, new Object[] {newGpsdId, userId, gpsd.getDbname(), sb.toString(), "SUBMITTED"});

        return newGpsdId;
    }
}
