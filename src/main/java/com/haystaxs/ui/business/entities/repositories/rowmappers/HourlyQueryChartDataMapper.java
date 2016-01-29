package com.haystaxs.ui.business.entities.repositories.rowmappers;

import com.haystaxs.ui.business.entities.UserQueryChartData;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

/**
 * Created by Adnan on 10/21/2015.
 */
public class HourlyQueryChartDataMapper implements RowMapper {
    @Override
    public Object mapRow(ResultSet rs, int i) throws SQLException {
        UserQueryChartData record = new UserQueryChartData();

        SimpleDateFormat hsDateFormatter = new SimpleDateFormat("hh");

        if(!rs.isLast())
            record.setDate(rs.getString("date"));

        record.setTotalDuration(rs.getInt("total_duration"));
        record.setAnalyzeDuration(rs.getInt("analyze_duration"));
        record.setCommitDuration(rs.getInt("commit_duration"));
        record.setCreateExternalTableDuration(rs.getInt("create_external_table_duration"));
        record.setCreateTableDuration(rs.getInt("create_table_duration"));
        record.setDeleteDuration(rs.getInt("delete_duration"));
        record.setExclusiveLockDuration(rs.getInt("exclusive_lock_duration"));
        record.setInternalDuration(rs.getInt("internal_duration"));
        record.setOthersDuration(rs.getInt("others_duration"));
        record.setShowConfigurationDuration(rs.getInt("show_configuration_duration"));
        record.setShowDuration(rs.getInt("show_duration"));
        record.setTransactionOperationDuration(rs.getInt("transaction_operation_duration"));
        record.setTruncateTableDuration(rs.getInt("truncate_table_duration"));
        record.setUpdateDuration(rs.getInt("update_duration"));
        record.setSelectDuration(rs.getInt("select_duration"));
        record.setInsertDuration(rs.getInt("insert_duration"));
        record.setDropTableDuration(rs.getInt("drop_table_duration"));

        return (record);
    }
}
