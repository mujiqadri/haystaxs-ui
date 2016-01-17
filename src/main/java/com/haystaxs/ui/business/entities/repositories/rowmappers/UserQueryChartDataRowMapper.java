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

        if(!rs.isLast()) {
            record.setDate(hsDateFormatter.format(rs.getDate("date")));
        }

        //record.setTotalDuration(MathUtil.roundTo2DecimalPlaces(rs.getDouble("total_duration")));
        record.setTotalDuration(rs.getInt("total_duration"));
        record.setTotalCount(rs.getInt("total_count"));
        record.setAnalyzeDuration(rs.getInt("analyze_duration"));
        record.setAnalyzeCount(rs.getInt("analyze_count"));
        record.setCommitDuration(rs.getInt("commit_duration"));
        record.setCommitCount(rs.getInt("commit_count"));
        record.setCreateExternalTableDuration(rs.getInt("create_external_table_duration"));
        record.setCreateExternalTableCount(rs.getInt("create_external_table_count"));
        record.setCreateTableDuration(rs.getInt("create_table_duration"));
        record.setCreateTableCount(rs.getInt("create_table_count"));
        record.setDeleteDuration(rs.getInt("delete_duration"));
        record.setDeleteCount(rs.getInt("delete_count"));
        record.setExclusiveLockDuration(rs.getInt("exclusive_lock_duration"));
        record.setExclusiveLockCount(rs.getInt("exclusive_lock_count"));
        record.setInternalDuration(rs.getInt("internal_duration"));
        record.setInternalCount(rs.getInt("internal_count"));
        record.setOthersDuration(rs.getInt("others_duration"));
        record.setOthersCount(rs.getInt("others_count"));
        record.setShowConfigurationDuration(rs.getInt("show_configuration_duration"));
        record.setShowConfigurationCount(rs.getInt("show_configuration_count"));
        record.setShowDuration(rs.getInt("show_duration"));
        record.setShowCount(rs.getInt("show_count"));
        record.setTransactionOperationDuration(rs.getInt("transaction_operation_duration"));
        record.setTransactionOperationCount(rs.getInt("transaction_operation_count"));
        record.setTruncateTableDuration(rs.getInt("truncate_table_duration"));
        record.setTruncateTableCount(rs.getInt("truncate_table_count"));
        record.setUpdateDuration(rs.getInt("update_duration"));
        record.setUpdateCount(rs.getInt("update_count"));
        record.setSelectDuration(rs.getInt("select_duration"));
        record.setSelectCount(rs.getInt("select_count"));
        record.setInsertDuration(rs.getInt("insert_duration"));
        record.setInsertCount(rs.getInt("insert_count"));
        record.setDropTableDuration(rs.getInt("drop_table_duration"));
        record.setDropTableCount(rs.getInt("drop_table_count"));

        return (record);
    }
}
