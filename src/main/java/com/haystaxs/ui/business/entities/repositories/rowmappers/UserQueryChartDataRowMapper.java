package com.haystaxs.ui.business.entities.repositories.rowmappers;

import com.haystaxs.ui.business.entities.Gpsd;
import com.haystaxs.ui.business.entities.UserQueryChartData;
import com.haystaxs.ui.util.MathUtil;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

/**
 * Created by Adnan on 10/21/2015.
 */
public class UserQueryChartDataRowMapper implements RowMapper {
    @Override
    public Object mapRow(ResultSet rs, int i) throws SQLException {
        UserQueryChartData record = new UserQueryChartData();

        SimpleDateFormat hsDateFormatter = new SimpleDateFormat("dd-MM-yyyy");

        record.setDate(hsDateFormatter.format(rs.getDate("date")));

        record.setTotalDuration(MathUtil.roundTo2DecimalPlaces(rs.getDouble("total_duration")));
        record.setTotalCount(rs.getInt("total_count"));
        record.setSelectDuration(rs.getDouble("select_duration"));
        record.setSelectCount(rs.getInt("select_count"));
        record.setInsertDuration(rs.getDouble("insert_duration"));
        record.setInsertCount(rs.getInt("insert_count"));
        record.setDropTableDuration(rs.getDouble("drop_table_duration"));
        record.setDropTableCount(rs.getInt("drop_table_count"));

        return (record);
    }
}
