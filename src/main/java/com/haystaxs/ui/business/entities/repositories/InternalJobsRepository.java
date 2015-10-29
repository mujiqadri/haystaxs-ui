package com.haystaxs.ui.business.entities.repositories;

import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Created by Adnan on 10/23/2015.
 */
@Repository
public class InternalJobsRepository extends RepositoryBase{
    public void createNew(int userId, String statusText, int contextId) {
        String sql = "select nextval('haystack.seq_internal_jobs')";
        int newId = jdbcTemplate.queryForObject(sql, Integer.class);

        sql = "INSERT INTO haystack.internal_jobs(internal_job_id, submitted_by, submitted_on, status_text, context_id) VALUES (?, ?, localtimestamp, ?, ?);";
        jdbcTemplate.update(sql, new Object[] {newId, userId, statusText, contextId});
    }
}
