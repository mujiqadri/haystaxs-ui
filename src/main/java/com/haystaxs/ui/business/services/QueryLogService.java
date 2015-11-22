package com.haystaxs.ui.business.services;

import com.haystaxs.ui.business.entities.QueryLog;
import com.haystaxs.ui.business.entities.repositories.QueryLogRespository;
import com.sun.org.apache.regexp.internal.RESyntaxException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by adnan on 11/20/15.
 */
@Service
public class QueryLogService {
    @Autowired
    private QueryLogRespository queryLogRespository;

    public List<QueryLog> getAllUnprocessed(int userId) {
        List<QueryLog> allQueryLogs = queryLogRespository.getAll(userId);
        List<QueryLog> result = new ArrayList<QueryLog>();

        for(QueryLog ql : allQueryLogs) {
            if(ql.getStatus().equals("UPLOADED")) {
                result.add(ql);
            }
        }

        return result;
    }
}
