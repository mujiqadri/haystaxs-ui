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

    public String hasBeenUploadedBefore(int userId, String checksum, String fileName) {
        List<QueryLog> queryLogs = queryLogRespository.getAll(userId);

        int errorCode = 0;
        String previousFileName = "";

        for(QueryLog ql : queryLogs) {
            if(ql.getFileChecksum().equals(checksum)) {
                errorCode += 1;
                previousFileName = ql.getOriginalFileName();
            }
            if(ql.getOriginalFileName().equals(fileName)) {
                errorCode += 2;
            }
            if(errorCode > 0)
                break;
        }

        if(errorCode == 1) {
            return String.format("An identical query log file with a different name [%s] has already been uploaded. Will not be processed.", previousFileName);
        } else if (errorCode == 2) {
            // TODO: Gotta think about this case
            return "";
        }
        else if (errorCode == 3) {
            return String.format("An identical query log file has already been uploaded. Will not be processed.");
        }

        return "";
    }
}
