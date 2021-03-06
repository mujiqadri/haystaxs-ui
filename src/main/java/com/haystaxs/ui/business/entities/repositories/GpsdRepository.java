package com.haystaxs.ui.business.entities.repositories;

import com.haystaxs.ui.business.entities.Gpsd;
import com.haystaxs.ui.business.entities.repositories.rowmappers.GpsdRowMapper;
import com.sun.org.apache.bcel.internal.generic.GETFIELD;
import org.apache.commons.compress.archivers.sevenz.CLI;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import java.util.Date;
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

    public List<Gpsd> getAll(int userId, boolean isDeployedOnCluster) {
        String sql = "select c.*, cu.is_default from %s.cluster c join %1$s.cluster_users cu on c.cluster_id = cu.cluster_id where ";
        if(isDeployedOnCluster) {
            sql += " cu.user_id = 1 or ";
        }
        sql += " cu.user_id = ? ORDER BY c.cluster_id DESC;";

        sql = String.format(sql, getHsSchemaName());

        List<Gpsd> resultSet = jdbcTemplate.query(sql, new Object[]{userId}, new BeanPropertyRowMapper<Gpsd>(Gpsd.class));

        return resultSet;
    }

    public List<String> getAllDistinct(int userId) {
        String sql = String.format("SELECT DISTINCT g.friendly_name FROM %s.gpsd g " +
                " INNER JOIN %1$s.gpsd_users gu on g.gpsd_id = gu.gpsd_id " +
                " WHERE gu.user_id = ? ORDER BY g.friendly_name;", getHsSchemaName());

        List<String> resultSet = jdbcTemplate.queryForList(sql, String.class, new Object[]{userId});

        return resultSet;
    }

    public Gpsd getSingle(int gpsdId, int userId) {
        String sql = String.format("select * from %s.cluster where cluster_id = ? and user_id = ?", getHsSchemaName());

        Gpsd result = jdbcTemplate.queryForObject(sql, new Object[]{gpsdId, userId}, new BeanPropertyRowMapper<Gpsd>(Gpsd.class));

        return result;
    }

    public int getMaxGpsdIdByName(int userId, String dbName) {
        String sql = String.format("select max(gpsd_id) from %s.gpsd where dbname = ? and user_id = ?", getHsSchemaName());

        int result = jdbcTemplate.queryForObject(sql, new Object[]{dbName, userId}, Integer.class);

        return result;
    }

    public int createNew(int userId, String originalFileName, boolean isDefault) {
        String sql = String.format("select nextval('%s.seq_gpsd')", getHsSchemaName());
        int newGpsdId = jdbcTemplate.queryForObject(sql, Integer.class);

        sql = String.format("INSERT INTO %s.cluster (cluster_id, dbname, filename, cluster_db, cluster_date, cluster_params, " +
                        "cluster_version, nooflines, file_submitted_on, status, created_on, host, username, port, password, " +
                        "db_type, last_queries_refreshed_on, is_active, last_schema_refreshed_on, friendly_name) VALUES " +
                        "(?, NULL, ?, NULL, NULL, NULL," +
                        "NULL, 0, LOCALTIMESTAMP, ?, NULL, NULL, NULL, 0, NULL," +
                        "?, NULL, TRUE, NULL, ?)", getHsSchemaName());
        jdbcTemplate.update(sql, new Object[]{newGpsdId, originalFileName, "SUBMITTED", "GREEMPLUM",
                originalFileName + " [" + DateTime.now().toString("dd-MM-yyyy HH:mm") + "]"});

        sql = String.format("INSERT INTO %s.cluster_users(cluster_id, user_id, is_default) VALUES (?, ?, ?)", getHsSchemaName());
        jdbcTemplate.update(sql, new Object[]{newGpsdId, userId, isDefault});

        if(isDefault) {
            sql = String.format("UPDATE %s.cluster_users SET is_default = FALSE WHERE user_id = ? AND cluster_id <> ?",
                    getHsSchemaName());
            jdbcTemplate.update(sql, new Object[]{userId, newGpsdId});
        }

        return newGpsdId;
    }

    public int addCluster(Gpsd cluster, boolean makeDefault) {
        String sql = String.format("select nextval('%s.seq_gpsd')", getHsSchemaName());
        int newClusterId = jdbcTemplate.queryForObject(sql, Integer.class);

        sql = String.format("INSERT INTO %s.cluster (cluster_id, dbname, filename, cluster_db, cluster_date, cluster_params, " +
                "cluster_version, nooflines, file_submitted_on, status, created_on, host, username, port, password, " +
                "db_type, last_queries_refreshed_on, is_active, last_schema_refreshed_on, friendly_name) VALUES " +
                "(?, ?, NULL, NULL, NULL, NULL," +
                "NULL, 0, NULL, ?, LOCALTIMESTAMP, ?, ?, ?, ?," +
                "?, NULL, TRUE, NULL, ?)", getHsSchemaName());
        jdbcTemplate.update(sql, new Object[]{newClusterId, cluster.getDbName(), "CREATED", cluster.getHost(),
        cluster.getUserName(), cluster.getPort(), cluster.getPassword(), cluster.getDbType(), cluster.getFriendlyName()});

        sql = String.format("INSERT INTO %s.cluster_users(cluster_id, user_id, is_default) VALUES (?, ?, ?)", getHsSchemaName());
        jdbcTemplate.update(sql, new Object[]{newClusterId, 1 /*Always Cluster Admin*/, makeDefault});

        if(makeDefault) {
            sql = String.format("UPDATE %s.cluster_users SET is_default = FALSE WHERE user_id = 1 AND cluster_id <> ?",
                    getHsSchemaName());
            jdbcTemplate.update(sql, new Object[]{newClusterId});
        }

        return newClusterId;
    }

    public void delete(int gpsdId, int userId, String userSchemaName) {
        try {
            String sql = String.format("SELECT is_default FROM %s.cluster_users where cluster_id = ?",
                    getHsSchemaName());
            Boolean isDefault = jdbcTemplate.queryForObject(sql, new Object[] { gpsdId }, Boolean.class);

            sql = String.format("DELETE FROM %s.cluster_users WHERE cluster_id = ?", getHsSchemaName());
            jdbcTemplate.update(sql, gpsdId);

            sql = String.format("DELETE FROM %s.cluster_stats WHERE cluster_id = ?", getHsSchemaName());
            jdbcTemplate.update(sql, gpsdId);

            sql = String.format("DELETE FROM %s.cluster WHERE cluster_id = ?", getHsSchemaName());
            jdbcTemplate.update(sql, gpsdId);

            sql = String.format("DELETE FROM %s.queries WHERE cluster_id = ?", userSchemaName);
            jdbcTemplate.update(sql, gpsdId);

            // If this was the default cluster, change the default cluster to the last one, if this
            // was the only cluster then he should automatically be taken to Add cluster / Upload GPSD screen
            if(isDefault) {
                sql = String.format("SELECT MAX(cluster_id) FROM %s.cluster_users WHERE user_id = ?", getHsSchemaName());
                Integer maxGpsdId = jdbcTemplate.queryForObject(sql, new Object[] {userId}, Integer.class);

                if(maxGpsdId != null) {
                    sql = String.format("UPDATE %s.cluster_users SET is_default = TRUE WHERE cluster_id = ?", getHsSchemaName());
                    jdbcTemplate.update(sql, maxGpsdId);
                }
            }
        } catch (Exception ex) {
            logger.error("Could not delete all possible GPSD entries for cluster_id={}. Ex Msg: ", gpsdId, ex.getMessage());
        }
    }
}
