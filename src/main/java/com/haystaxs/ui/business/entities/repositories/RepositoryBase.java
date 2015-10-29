package com.haystaxs.ui.business.entities.repositories;

import com.haystaxs.ui.web.controllers.AuthController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * Created by Adnan on 10/21/2015.
 */
public class RepositoryBase {
    final static Logger logger = LoggerFactory.getLogger(AuthController.class);
    protected DataSource dataSource;
    protected JdbcTemplate jdbcTemplate;

    public RepositoryBase(){
        logger.debug("RepositoryBase instantiated.");
    }

    @Autowired
    protected void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        jdbcTemplate = new JdbcTemplate(this.dataSource);
    }

    protected String hsSchemaName() {
        return "haystack";
    }
}
