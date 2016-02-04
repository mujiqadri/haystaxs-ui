package com.haystaxs.ui.business.entities.repositories;

import com.haystaxs.ui.business.entities.Cluster;
import com.haystaxs.ui.business.entities.Gpsd;
import com.haystaxs.ui.business.entities.repositories.rowmappers.GpsdRowMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Adnan on 10/21/2015.
 */
@Repository
public class ClusterRepository extends RepositoryBase {
    final static Logger logger = LoggerFactory.getLogger(ClusterRepository.class);

    public ClusterRepository() {
        logger.trace(logger.getName() + " instantiated.");
    }

    public int createNewCluster(int userId, Cluster cluster) {
        String sql = String.format("select nextval('%s.seq_clusters')", getHsSchemaName());
        int newClusterId = jdbcTemplate.queryForObject(sql, Integer.class);

        sql = String.format("INSERT INTO %s.cluster (cluster_id, host, cluster_name, password, username, " +
                "schema_refresh_schedule, query_refresh_schedule, created_on, cluster_type, user_id) " +
                "VALUES (?,?,?,?,?,?,?, LOCALTIMESTAMP,?,?)", getHsSchemaName());

        jdbcTemplate.update(sql, new Object[]{ newClusterId, cluster.getHost(), cluster.getClusterName(),
                cluster.getPassword(), cluster.getUserName(), cluster.getSchemaRefreshSchedule(),
                cluster.getQueryRefreshSchedule(), cluster.getClusterType(),
                userId });

        return newClusterId;
    }

    public List<Cluster> getAllClusters(int userId) {
        String sql = String.format("SELECT * FROM %s.cluster WHERE user_id = ? ORDER BY cluster_name DESC", getHsSchemaName());

        List<Cluster> resultSet = jdbcTemplate.query(sql, new Object[]{ userId }, new BeanPropertyRowMapper<Cluster>(Cluster.class));

        return resultSet;
    }

    public boolean clusterBelongsToUser(int userId, int clusterId) {
        String sql = String.format("SELECT count(0) FROM %s.cluster WHERE user_id = ? AND cluster_id = ?",
                getHsSchemaName());

        int result = jdbcTemplate.queryForObject(sql, new Object[] {userId, clusterId}, Integer.class);

        return (result > 0 ? true : false);
    }

    public int getMaxClusterId(int userId) {
        String sql = String.format("SELECT MAX(cluster_id) FROM %s.cluster WHERE user_id = ?",
                getHsSchemaName());

        int result = jdbcTemplate.queryForObject(sql, new Object[] {userId}, Integer.class);

        return (result);
    }

    /*public Gpsd getSingle(int gpsdId, int userId) {
        String sql = String.format("select * from %s.gpsd where gpsd_id = ? and user_id = ?", getHsSchemaName());

        Gpsd result = jdbcTemplate.queryForObject(sql, new Object[]{gpsdId, userId}, new BeanPropertyRowMapper<Gpsd>(Gpsd.class));

        return result;
    }*/
}

