package com.haystaxs.ui.business.entities.repositories.rowmappers;

import com.haystaxs.ui.business.entities.Gpsd;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * Created by Adnan on 10/21/2015.
 */
public class GpsdRowMapper implements RowMapper {
    @Override
    public Object mapRow(ResultSet rs, int i) throws SQLException {
        Gpsd gpsd = new Gpsd();

        gpsd.setGpsdId(rs.getInt("cluster_id"));
        gpsd.setDbName(rs.getString("dbname"));
        gpsd.setGpsdVersion(rs.getString("cluster_version"));
        gpsd.setNoOfLines(rs.getInt("nooflines"));
        gpsd.setFileSubmittedOn(rs.getTimestamp("file_submitted_on"));
        gpsd.setStatus(rs.getString("status"));
        gpsd.setGpsdDate(rs.getTimestamp("cluster_date"));

        return (gpsd);
    }
}
