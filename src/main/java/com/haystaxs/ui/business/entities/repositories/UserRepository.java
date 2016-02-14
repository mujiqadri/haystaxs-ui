package com.haystaxs.ui.business.entities.repositories;

import com.haystaxs.ui.business.entities.HsUser;
import com.haystaxs.ui.business.entities.repositories.rowmappers.HsUserRowMapper;
import com.haystaxs.ui.util.MiscUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import java.util.*;

/**
 * Created by Adnan on 10/16/2015.
 */
@Repository
public class UserRepository extends RepositoryBase {
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

    public boolean verifyRegistration(int userId, String verificationCode) {
        String sql = String.format("UPDATE %s.users SET reg_verified = true where user_id = ? and reg_verification_code = ?",
                getHsSchemaName());

        int noOfRows = jdbcTemplate.update(sql, new Object[]{userId, verificationCode});

        return (noOfRows > 0 ? true : false);
    }

    public HsUser createNew(HsUser hsUser) {
        /*
            -- To get a UUID alternative, works in newer versions of postgres
            SELECT md5(random()::text || clock_timestamp()::text)::uuid
         */

        String sql = String.format("select nextval('%s.seq_users')", getHsSchemaName());
        int newUserId = jdbcTemplate.queryForObject(sql, Integer.class);
        String regVerificationCode = UUID.randomUUID().toString();

        sql = String.format("INSERT INTO %s.users(user_id, first_name, last_name, email_address, password, organization, " +
                "created_on, reg_requested_on, reg_verification_code, reg_verified, user_name, is_admin) VALUES " +
                "(?, ?, ?, ?, ?, ?, localtimestamp, localtimestamp, ?, false, ?, ?)", getHsSchemaName());
        jdbcTemplate.update(sql, new Object[]{newUserId, hsUser.getFirstName(), hsUser.getLastName(), hsUser.getEmailAddress(),
                hsUser.getPassword(), hsUser.getOrganization(),
                regVerificationCode, miscUtil.getNormalizedUserName(hsUser.getEmailAddress()), hsUser.isAdmin()});

        hsUser.setRegVerificationCode(regVerificationCode);
        hsUser.setUserId(newUserId);

        return hsUser;
    }

    public HsUser getById(int userId) {
        String sql = String.format("SELECT * FROM %s.users WHERE user_id = ?", getHsSchemaName());

        HsUser result = null;

        try {
            result = jdbcTemplate.queryForObject(sql, new Object[]{userId}, new BeanPropertyRowMapper<HsUser>(HsUser.class));
        } catch (Exception ex) {
            logger.error("User not found for ID {}", userId);
        }

        return result;
    }
}
