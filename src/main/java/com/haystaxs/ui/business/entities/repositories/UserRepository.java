package com.haystaxs.ui.business.entities.repositories;

import com.haystaxs.ui.business.entities.HsUser;
import com.haystaxs.ui.business.entities.repositories.rowmappers.HsUserRowMapper;
import com.haystaxs.ui.util.MiscUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Repository;

import java.util.*;

/**
 * Created by Adnan on 10/16/2015.
 */
@Repository
public class UserRepository extends  RepositoryBase{
    final static Logger logger = LoggerFactory.getLogger(UserRepository.class);
    @Autowired
    private MiscUtil miscUtil;

    public UserRepository() {
        logger.trace(logger.getName() + " instantiated.");
    }

    public HsUser findUserByEmail(String email) {
        String sql = String.format("select * from %s.users where email_address = '%s'", getHsSchemaName(), email);

        try {
            return (HsUser) jdbcTemplate.queryForObject(sql, new HsUserRowMapper());
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    public int createNew(HsUser hsUser) {
        /*
            -- To get a UUID alternative, works in newer versions of postgres
            SELECT md5(random()::text || clock_timestamp()::text)::uuid
         */

        String sql = String.format("select nextval('%s.seq_users')", getHsSchemaName());
        int newUserId = jdbcTemplate.queryForObject(sql, Integer.class);
        String regVerificationCode = UUID.randomUUID().toString();

        sql = String.format("INSERT INTO %s.users(user_id, first_name, last_name, email_address, password, organization, " +
                "created_on, reg_requested_on, req_verificaton_code, reg_verified, user_name) VALUES " +
                "(?, ?, ?, ?, ?, ?, localtimestamp, localtimestamp, ?, false, ?)", getHsSchemaName());
        jdbcTemplate.update(sql, new Object[] {newUserId, hsUser.getFirstName(), hsUser.getLastName(), hsUser.getEmailAddress(), hsUser.getPassword(), hsUser.getOrganization(),
        regVerificationCode, miscUtil.getNormalizedUserName(hsUser.getEmailAddress())});

        return newUserId;
    }

    public void selectTest() {
        String sql = "select * from haystack.users";
        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql);
    }
}
