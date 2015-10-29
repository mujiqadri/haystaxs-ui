package com.haystaxs.ui.business.entities.repositories;

import com.haystaxs.ui.business.entities.HsUser;
import com.haystaxs.ui.business.entities.repositories.rowmappers.HsUserRowMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.*;

/**
 * Created by Adnan on 10/16/2015.
 */
@Repository
public class UserRepository extends  RepositoryBase{
    final static Logger logger = LoggerFactory.getLogger(UserRepository.class);

    public UserRepository() {
        logger.debug("UserRepository instantiated");
    }

    public HsUser findUserByEmail(String email) {
        String sql = String.format("select * from %s.users where email_address = '%s'", hsSchemaName(), email);

        try {
            return (HsUser) jdbcTemplate.queryForObject(sql, new HsUserRowMapper());
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    public void add(HsUser hsUser) {
        /*
        INSERT INTO haystack.users(
            password, organization, created_on, email_address,
            user_id, full_name, reg_verification_code,
            reg_verified)
        VALUES ('abc', 'def', localtimestamp, 'a@b.c',
            2, 'a b c', (SELECT uuid_in(md5(random()::text || now()::text)::cstring)), false);

            -- To get a UUID alternative, works in newer versions of postgres
            SELECT md5(random()::text || clock_timestamp()::text)::uuid
         */
    }

    public void selectTest() {
        String sql = "select * from haystack.users";
        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql);
    }
}
