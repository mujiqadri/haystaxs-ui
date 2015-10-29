package com.haystaxs.ui.business.entities.repositories.rowmappers;

import com.haystaxs.ui.business.entities.HsUser;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.core.GrantedAuthority;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by Adnan on 10/22/2015.
 */
public class HsUserRowMapper implements RowMapper {
    @Override
    public Object mapRow(ResultSet rs, int i) throws SQLException {
        HsUser hsUser = new HsUser();//(rs.getString("email_address"), rs.getString("password"), new ArrayList<GrantedAuthority>());

        hsUser.setUserId(rs.getInt("user_id"));
        hsUser.setFirstName(rs.getString("first_name"));
        hsUser.setLastName(rs.getString("last_name"));
        hsUser.setEmailAddress(rs.getString("email_address"));
        hsUser.setPassword(rs.getString("password"));
        hsUser.setOrganization(rs.getString("organization"));
        hsUser.setRegVerified(rs.getBoolean("reg_verified"));
        hsUser.setCreatedOn(rs.getTimestamp("created_on"));

        return (hsUser);
    }
}
