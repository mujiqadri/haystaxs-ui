package com.haystaxs.ui.business.entities;

/**
 * Created by adnan on 12/20/15.
 */
public class UserQueryChartData {
    private String date;
    private int totalDuration;
    private int totalCount;
    private int analyzeDuration;
    private int analyzeCount;
    private int commitDuration;
    private int commitCount;
    private int createExternalTableDuration;
    private int createExternalTableCount;
    private int createTableDuration;
    private int createTableCount;
    private int deleteDuration;
    private int deleteCount;
    private int exclusiveLockDuration;
    private int exclusiveLockCount;
    private int internalDuration;
    private int internalCount;
    private int othersDuration;
    private int othersCount;
    private int showConfigurationDuration;
    private int showConfigurationCount;
    private int showDuration;
    private int showCount;
    private int transactionOperationDuration;
    private int transactionOperationCount;
    private int truncateTableDuration;
    private int truncateTableCount;
    private int updateDuration;
    private int updateCount;
    private int selectDuration;
    private int selectCount;
    private int insertDuration;
    private int insertCount;
    private int dropTableDuration;
    private int dropTableCount;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getSelectCount() {
        return selectCount;
    }

    public void setSelectCount(int selectCount) {
        this.selectCount = selectCount;
    }

    public int getInsertCount() {
        return insertCount;
    }

    public void setInsertCount(int insertCount) {
        this.insertCount = insertCount;
    }

    public int getDropTableCount() {
        return dropTableCount;
    }

    public void setDropTableCount(int dropTableCount) {
        this.dropTableCount = dropTableCount;
    }

    public int getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(int totalDuration) {
        this.totalDuration = totalDuration;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getSelectDuration() {
        return selectDuration;
    }

    public void setSelectDuration(int selectDuration) {
        this.selectDuration = selectDuration;
    }

    public int getInsertDuration() {
        return insertDuration;
    }

    public void setInsertDuration(int insertDuration) {
        this.insertDuration = insertDuration;
    }

    public int getDropTableDuration() {
        return dropTableDuration;
    }

    public void setDropTableDuration(int dropTableDuration) {
        this.dropTableDuration = dropTableDuration;
    }

    public int getAnalyzeDuration() {
        return analyzeDuration;
    }

    public void setAnalyzeDuration(int analyzeDuration) {
        this.analyzeDuration = analyzeDuration;
    }

    public int getAnalyzeCount() {
        return analyzeCount;
    }

    public void setAnalyzeCount(int analyzeCount) {
        this.analyzeCount = analyzeCount;
    }

    public int getCommitDuration() {
        return commitDuration;
    }

    public void setCommitDuration(int commitDuration) {
        this.commitDuration = commitDuration;
    }

    public int getCommitCount() {
        return commitCount;
    }

    public void setCommitCount(int commitCount) {
        this.commitCount = commitCount;
    }

    public int getCreateExternalTableDuration() {
        return createExternalTableDuration;
    }

    public void setCreateExternalTableDuration(int createExternalTableDuration) {
        this.createExternalTableDuration = createExternalTableDuration;
    }

    public int getCreateExternalTableCount() {
        return createExternalTableCount;
    }

    public void setCreateExternalTableCount(int createExternalTableCount) {
        this.createExternalTableCount = createExternalTableCount;
    }

    public int getCreateTableDuration() {
        return createTableDuration;
    }

    public void setCreateTableDuration(int createTableDuration) {
        this.createTableDuration = createTableDuration;
    }

    public int getCreateTableCount() {
        return createTableCount;
    }

    public void setCreateTableCount(int createTableCount) {
        this.createTableCount = createTableCount;
    }

    public int getDeleteDuration() {
        return deleteDuration;
    }

    public void setDeleteDuration(int deleteDuration) {
        this.deleteDuration = deleteDuration;
    }

    public int getDeleteCount() {
        return deleteCount;
    }

    public void setDeleteCount(int deleteCount) {
        this.deleteCount = deleteCount;
    }

    public int getExclusiveLockDuration() {
        return exclusiveLockDuration;
    }

    public void setExclusiveLockDuration(int exclusiveLockDuration) {
        this.exclusiveLockDuration = exclusiveLockDuration;
    }

    public int getExclusiveLockCount() {
        return exclusiveLockCount;
    }

    public void setExclusiveLockCount(int exclusiveLockCount) {
        this.exclusiveLockCount = exclusiveLockCount;
    }

    public int getInternalDuration() {
        return internalDuration;
    }

    public void setInternalDuration(int internalDuration) {
        this.internalDuration = internalDuration;
    }

    public int getInternalCount() {
        return internalCount;
    }

    public void setInternalCount(int internalCount) {
        this.internalCount = internalCount;
    }

    public int getOthersDuration() {
        return othersDuration;
    }

    public void setOthersDuration(int othersDuration) {
        this.othersDuration = othersDuration;
    }

    public int getOthersCount() {
        return othersCount;
    }

    public void setOthersCount(int othersCount) {
        this.othersCount = othersCount;
    }

    public int getShowConfigurationDuration() {
        return showConfigurationDuration;
    }

    public void setShowConfigurationDuration(int showConfigurationDuration) {
        this.showConfigurationDuration = showConfigurationDuration;
    }

    public int getShowConfigurationCount() {
        return showConfigurationCount;
    }

    public void setShowConfigurationCount(int showConfigurationCount) {
        this.showConfigurationCount = showConfigurationCount;
    }

    public int getShowDuration() {
        return showDuration;
    }

    public void setShowDuration(int showDuration) {
        this.showDuration = showDuration;
    }

    public int getShowCount() {
        return showCount;
    }

    public void setShowCount(int showCount) {
        this.showCount = showCount;
    }

    public int getTransactionOperationDuration() {
        return transactionOperationDuration;
    }

    public void setTransactionOperationDuration(int transactionOperationDuration) {
        this.transactionOperationDuration = transactionOperationDuration;
    }

    public int getTransactionOperationCount() {
        return transactionOperationCount;
    }

    public void setTransactionOperationCount(int transactionOperationCount) {
        this.transactionOperationCount = transactionOperationCount;
    }

    public int getTruncateTableDuration() {
        return truncateTableDuration;
    }

    public void setTruncateTableDuration(int truncateTableDuration) {
        this.truncateTableDuration = truncateTableDuration;
    }

    public int getTruncateTableCount() {
        return truncateTableCount;
    }

    public void setTruncateTableCount(int truncateTableCount) {
        this.truncateTableCount = truncateTableCount;
    }

    public int getUpdateDuration() {
        return updateDuration;
    }

    public void setUpdateDuration(int updateDuration) {
        this.updateDuration = updateDuration;
    }

    public int getUpdateCount() {
        return updateCount;
    }

    public void setUpdateCount(int updateCount) {
        this.updateCount = updateCount;
    }
}
