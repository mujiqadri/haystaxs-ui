package com.haystaxs.ui.business.entities.repositories.rowmappers;

import com.haystaxs.ui.business.entities.Gpsd;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Adnan on 10/21/2015.
 */
public class GpsdRowMapper implements RowMapper {
    @Override
    public Object mapRow(ResultSet rs, int i) throws SQLException {
        Gpsd gpsd = new Gpsd();
        gpsd.setGpsdId(rs.getInt("gpsd_id"));
        gpsd.setDbname(rs.getString("dbname"));

        return (gpsd);
    }
}
