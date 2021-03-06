package com.haystaxs.ui.business.entities.repositories;

import com.haystaxs.ui.business.entities.Gpsd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
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

    // TODO: Need a good eviction strategy
    //@Cacheable(value = RepositoryBase.CACHE_NAME, key = RepositoryBase.CACHE_KEY_GENERATOR_STRING)
    public List<Gpsd> getAllClusters(int userId, boolean isDeployedOnCluster) {

        String sql = "select c.* from %s.cluster c join %1$s.cluster_users cu  on c.cluster_id = cu.cluster_id where ";
        if(isDeployedOnCluster) {
                sql += " cu.user_id = 1 or ";
        }
        sql += " cu.cluster_id = ? ORDER BY c.friendly_name;";

        sql = String.format(sql, getHsSchemaName());

        List<Gpsd> resultSet = jdbcTemplate.query(sql, new Object[]{userId}, new BeanPropertyRowMapper<Gpsd>(Gpsd.class));

        return resultSet;
    }

    public int getDefaultClusterId(int userId) {
        String sql = String.format("SELECT cluster_id FROM %s.cluster_users where user_id = ? AND is_default = TRUE",
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

