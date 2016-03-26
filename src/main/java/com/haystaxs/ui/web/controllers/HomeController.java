package com.haystaxs.ui.web.controllers;

import com.haystack.domain.Tables;
import com.haystaxs.ui.business.entities.*;
import com.haystaxs.ui.business.entities.repositories.*;
import com.haystaxs.ui.business.entities.selection.QueryLogMinMaxDateTimes;
import com.haystaxs.ui.business.services.HaystaxsLibService;
import com.haystaxs.ui.business.services.QueryLogService;
import com.haystaxs.ui.support.HsSessionAttributes;
import com.haystaxs.ui.support.JsonResponse;
import com.haystaxs.ui.support.PaginationInfo;
import com.haystaxs.ui.support.UploadedFileInfo;
import com.haystaxs.ui.util.AppConfig;
import com.haystaxs.ui.util.FileUtil;
import com.haystaxs.ui.util.MiscUtil;
import org.omg.CORBA.portable.ApplicationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.RequestContext;
import org.thymeleaf.spring.support.Layout;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.mail.Session;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Adnan on 10/16/2015.
 */
@Controller
//@SessionAttributes(HsSessionAttributes.ACTIVE_CLUSTER_ID)
public class HomeController {
    final static Logger logger = LoggerFactory.getLogger(HomeController.class);

    //region ### Autowired Components ###
    @Autowired
    ServletContext servletContext;
    @Autowired
    private MessageSource messages;
    @Autowired
    private GpsdRepository gpsdRepository;
    @Autowired
    private QueryLogRespository queryLogRespository;
    @Autowired
    private InternalJobsRepository internalJobsRepository;
    @Autowired
    private WorkloadRepository workloadRepository;
    @Autowired
    private UserQueriesRepository userDatabaseRepository;
    @Autowired
    private FileUtil fileUtil;
    //    @Autowired
//    private MailUtil mailUtil;
    @Autowired
    private MiscUtil miscUtil;
    @Autowired
    private AppConfig appConfig;
    @Autowired
    private QueryLogService queryLogService;
    @Autowired
    private HaystaxsLibService haystaxsLibServiceWrapper;
    @Autowired
    private ClusterRepository clusterRepository;
    @Autowired
    private HttpSession httpSession;
    //endregion

    //region ### Controller Level Model Attributes ###
    @ModelAttribute("principal")
    private HsUser getPrincipal() {
        return (HsUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @ModelAttribute("userFirstName")
    private String getUserFirstName() {
        return getPrincipal().getFirstName();
    }

    @ModelAttribute("showBreadCrumbs")
    private boolean showBreadCrumbs() {
        return false;
    }

    @ModelAttribute("allUserClusters")
    private List<Gpsd> getAllUserClusters() {
        List<Gpsd> result = clusterRepository.getAllClusters(getUserId(), isDeployedOnCluster());

        if (result.size() == 0) {
            httpSession.removeAttribute(HsSessionAttributes.ACTIVE_CLUSTER_ID);
        }

        return result;
    }

    @ModelAttribute("isDeployedOnCluster")
    private boolean isDeployedOnCluster() {
        return (appConfig.isDeployedOnCluster());
    }

    @ModelAttribute(HsSessionAttributes.ACTIVE_CLUSTER_ID)
    private int getActiveClusterId() {
        Integer result = 0;

        try {
            result = (Integer) httpSession.getAttribute(HsSessionAttributes.ACTIVE_CLUSTER_ID);
        } catch (Exception ex) {
        }

        return (result == null ? 0 : result);
    }

    @ModelAttribute("isClusterAdmin")
    private boolean isClusterAdmin() {
        return getPrincipal().getUserId() == 1;
    }
    //endregion

    //region ### Controller Level Helper Methods ###
    private int getUserId() {
        return getPrincipal().getUserId();
    }

    private String getUserSchemaName() {
        if (!isDeployedOnCluster()) {
            return miscUtil.getNormalizedUserName(getPrincipal().getEmailAddress());
        } else {
            return miscUtil.getNormalizedUserName(appConfig.getClusterAdminEmailAddress());
        }
    }

/*
    private int getActiveClusterId(Model model) {
        return (Integer) ((ModelMap)model).get(HsSessionAttributes.ACTIVE_CLUSTER_ID);
    }
*/

    private String getClusterName(int clusterId) throws Exception {
        for (Gpsd cluster : getAllUserClusters()) {
            if (cluster.getGpsdId() == clusterId) {
                return cluster.getFriendlyName();
            }
        }

        throw new Exception("No Such Cluster Found !");
    }
    //endregion

    //region ### Dashboard Action ###
    @RequestMapping("/dashboard")
    public String dashBoard(Model model, HttpServletRequest request) {
        model.addAttribute("title", "Dashboard");

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("HH:mm");

        QueryLogMinMaxDateTimes queryLogMinMaxDates = userDatabaseRepository.getQueryLogMinMaxDates(getUserSchemaName(),
                getActiveClusterId());

        if (queryLogMinMaxDates.getMinDate() != null && queryLogMinMaxDates.getMaxDate() != null) {
            model.addAttribute("queryLogsProcessed", true);

            model.addAttribute("minDate", simpleDateFormat.format(queryLogMinMaxDates.getMinDate()));
            model.addAttribute("maxDate", simpleDateFormat.format(queryLogMinMaxDates.getMaxDate()));
            model.addAttribute("minTime", simpleTimeFormat.format(queryLogMinMaxDates.getMinTime()));
            model.addAttribute("maxTime", simpleTimeFormat.format(queryLogMinMaxDates.getMaxTime()));

            try {
                model.addAttribute("dbNames", userDatabaseRepository.getDbNames(getUserSchemaName()));
                model.addAttribute("userNames", userDatabaseRepository.getUserNames(getUserSchemaName(), 1));
            } catch (Exception ex) {
                logger.error(ex.getMessage());
            }
        }

        return "dashboard";
    }

    @RequestMapping("/dashboard/ql/chartdata")
    @ResponseBody
    public List<UserQueriesChartData2> dashboardQueryLogChartData(@RequestParam(value = "fromDate", required = false) String fromDate,
                                                                  @RequestParam(value = "toDate", required = false) String toDate,
                                                                  @RequestParam(value = "dbName", required = false) String dbName,
                                                                  @RequestParam(value = "userName", required = false) String userName,
                                                                  Model model) {

        List<UserQueriesChartData2> result = userDatabaseRepository.getQueryStatsForChart(getUserSchemaName(),
                getActiveClusterId(), fromDate, toDate,
                dbName, userName);

        return (result);
    }

    @RequestMapping("/dashboard/ql/hourlyavgchartdata")
    @ResponseBody
    public List<UserQueriesHourlyChartData> hourlyQueryLogChartData(@RequestParam(value = "fromDate", required = false) String fromDate,
                                                                    @RequestParam(value = "toDate", required = false) String toDate,
                                                                    @RequestParam(value = "dbName", required = false) String dbName,
                                                                    @RequestParam(value = "userName", required = false) String userName,
                                                                    @RequestParam(value = "sqlWindowOp", required = false) String windowOp,
                                                                    Model model) {
        List<UserQueriesHourlyChartData> result = userDatabaseRepository.getHourlyQueryStatsForChart(getUserSchemaName(),
                getActiveClusterId(), fromDate, toDate, dbName, userName, windowOp);

        // The resulting list may contain less or none rows, so the below is a workaround as the chart needs full data
        /*if (result.size() < 25) {
            // Define an array to hold the 24 hour data plus the sequencer (Last row from query summing all the columns)
            UserQueryChartData[] finalResult = new UserQueryChartData[25];

            // If no data was returned from the query
            if (result.size() == 0) {
                // Set the sequencer object used in the client JS
                finalResult[24] = new UserQueryChartData();
            }

            for (int i = 0; i < result.size(); i++) {
                UserQueryChartData data = result.get(i);

                if (data.getDate() == null) {
                    // If sequencer is found put it in the last position
                    finalResult[24] = data;
                } else {
                    // Put all other hours found in the correct index
                    finalResult[Integer.parseInt(data.getDate())] = data;
                }
            }

            // Fill up all the other empty indexes with zero values
            for (int i = 0; i < 24; i++) {
                if (finalResult[i] == null) {
                    finalResult[i] = new UserQueryChartData(Integer.toString(i));
                }
            }

            return (Arrays.asList(finalResult));
        }*/

        return (result);
    }

    //endregion

    //region ### Cluster Actions ###
    @RequestMapping("/cluster/add")
    public String addCluster(Gpsd cluster, Model model) {
        model.addAttribute("title", "Add Cluster");
        model.addAttribute("cluster", cluster);

        return "add_cluster";
    }

    @RequestMapping(value = "/cluster/add", method = RequestMethod.POST)
    public String addClusterPost(Gpsd cluster, Model model, HttpSession session) {
        model.addAttribute("title", "Add Cluster");
        model.addAttribute("cluster", cluster);

        try {
            if (haystaxsLibServiceWrapper.tryConnectToCluster(cluster)) {
                int newClusterId = gpsdRepository.addCluster(cluster, true);
                session.setAttribute(HsSessionAttributes.ACTIVE_CLUSTER_ID, newClusterId);
            } else {
                model.addAttribute("error", "Error connecting to cluster");
                return "add_cluster";
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            model.addAttribute("error", ex.getMessage());
            return "add_cluster";
        }

        return "redirect:/cluster/main";
    }

    @RequestMapping("cluster/main")
    public String clusterMain(Model model) {
        model.addAttribute("title", "Cluster Main");

        return "gpsd_main";
    }

    @Layout(enabled = false, value = "")
    @RequestMapping("/cluster/list")
    public String clusterList(Model model) {
        List<Gpsd> gpsdees = gpsdRepository.getAll(getUserId(), isDeployedOnCluster());
        model.addAttribute("gpsdees", gpsdees);

        return "fragments/gpsd_list";
    }

    @RequestMapping("/cluster/exploredb/{gpsdId}")
    public String exploreDb(@PathVariable("gpsdId") int gpsdId, Model model) {
        model.addAttribute("gpsd_id", gpsdId);
        return "db_explorer";
    }

    // NOTE: Called as an Ajax request from ExploreDB page
    @RequestMapping("/cluster/exploredb/json")
    @ResponseBody
    public Tables exploreDbJson(@RequestParam("gpsd_id") int gpsdId) {
        return haystaxsLibServiceWrapper.getTablesInfoForDbExplorer(gpsdId);
    }

    @RequestMapping("/cluster/delete/{id}")
    @ResponseBody
    public String deleteGpsd(@PathVariable("id") int gpsdId, Model model) {
        gpsdRepository.delete(gpsdId, getUserId(), getUserSchemaName());

        if (!isDeployedOnCluster()) {
            fileUtil.deleteRecursively(String.format("%s/%s/gpsd/%d", appConfig.getGpsdSaveDirectory(), getUserSchemaName(),
                    gpsdId));
        }

        // TODO: Tell BG Process to delete the backend Database created based on this GPSD

        return "success";
    }

    @RequestMapping("/cluster/refresh")
    @ResponseBody
    public String refreshClusterData() {
        haystaxsLibServiceWrapper.refeshCluster(getActiveClusterId());
        return "success";
    }
    //endregion

    //region ### GPSD Actions ###
    @RequestMapping("gpsd/upload")
    public String gpsdUpload(Model model) {
        model.addAttribute("title", "GPSD Upload");
        // Used by the Custom Thymeleaf Attribute "hs:asis"
        model.addAttribute("appRealPath", servletContext.getRealPath("/"));

        return "gpsd_upload";
    }

    @RequestMapping(value = "/gpsd/upload", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public Map gpsdUpload(@RequestParam(value = "gpsd-file") MultipartFile gpsdFile, HttpServletRequest request) {
        List<UploadedFileInfo> uploadedFileInfos = new ArrayList<UploadedFileInfo>();
        Map<String, Object> result = new HashMap<String, Object>();

        if (gpsdFile.isEmpty()) {
            logger.error("Submitted GPSD file is empty.");
            // TODO: Log as internal error
            return result;
        }

        logger.trace("Creating GPSD DB Entry.");

        UploadedFileInfo uploadedFileInfo = new UploadedFileInfo();
        String originalFileName = gpsdFile.getOriginalFilename();
        int newGpsdId = gpsdRepository.createNew(getUserId(), originalFileName, true);
        String normalizedUserName = getUserSchemaName();
        String gpsdFileDirectory = appConfig.getGpsdSaveDirectory() + File.separator + normalizedUserName +
                File.separator + "gpsd" + File.separator + newGpsdId;
        String gpsdFilePath = gpsdFileDirectory + File.separator + originalFileName;

        logger.trace("Saving GPSD file to " + gpsdFilePath);

        try {
            // gpsdFile = the Multipart file uploaded
            // gpsdFileDirectory = /uploads/emailaddress_at_gmail_dot_com/gpsd/{newGpsdId}
            // originalFileName = fileName extracted from uploaded gpsdFile
            fileUtil.saveMultipartFileToPath(gpsdFile, gpsdFileDirectory, originalFileName);

            List<String> actualGpsdFileNames = fileUtil.unGZipTarArchive(gpsdFileDirectory + File.separator +
                    originalFileName, gpsdFileDirectory);

            if (actualGpsdFileNames.size() > 1 || actualGpsdFileNames.size() == 0) {
                throw new Exception("zip file contains either 0 OR more than 1 sql file(s)");
            } else {
                String actualGpsdFileName = actualGpsdFileNames.get(0);

                logger.trace("About to invoke Async method createGPSD.");

                haystaxsLibServiceWrapper.createGPSD(newGpsdId, normalizedUserName, gpsdFileDirectory +
                        File.separator + actualGpsdFileName);
                // TODO: Assuming GPSD has been created successfully, do an entry in user inbox (should be done by Muji ?)

                // NOTE: Assuming this will now be the default cluster..
                request.getSession().setAttribute(HsSessionAttributes.ACTIVE_CLUSTER_ID, newGpsdId);
            }
        } catch (Exception e) {
            logger.error("Error while saving GPSD file on server. Exception: ", e.getMessage());
            // Rollback the DB Entry..
            gpsdRepository.delete(newGpsdId, getUserId(), getUserSchemaName());
            uploadedFileInfo.setError(e.getMessage());
        }
        uploadedFileInfo.setName(originalFileName);
        uploadedFileInfo.setSize(gpsdFile.getSize());
        uploadedFileInfos.add(uploadedFileInfo);

        result.put("files", uploadedFileInfos);
        return result;
    }

    //@RequestMapping(value = "/gpsd/file/{gpsdId}")
    // WAS GIVING OUT OF MEMORY EXCEPTION
    @ResponseBody
    public String gpsdFile(@PathVariable("gpsdId") int gpsdId) {
        Gpsd gpsd = gpsdRepository.getSingle(gpsdId, getUserId());

        String fullPath = appConfig.getGpsdSaveDirectory() + File.separator + getUserSchemaName() + File.separator +
                "gpsd" + File.separator + gpsdId + File.separator + gpsd.getFilename();

        String result;

        try {
            result = new String(Files.readAllBytes(Paths.get(fullPath)));
        } catch (java.io.IOException e) {
            result = "GPSD File Not Found !";
        }

        return result;
    }

    @RequestMapping(value = "/gpsd/file/{gpsdId}")
    @ResponseBody
    public ResponseEntity<byte[]> downloadGpsdFile(@PathVariable("gpsdId") int gpsdId, HttpServletResponse resp) {
        Gpsd gpsd = gpsdRepository.getSingle(gpsdId, getUserId());
        String fullPath = appConfig.getGpsdSaveDirectory() + File.separator + getUserSchemaName() + File.separator +
                "gpsd" + File.separator + gpsdId + File.separator + gpsd.getFilename();
        final HttpHeaders headers = new HttpHeaders();

        File toServeUp = new File(fullPath);

        InputStream inputStream = null;

        try {
            inputStream = new FileInputStream(toServeUp);
        } catch (FileNotFoundException e) {
            //Also useful, this is a good was to serve down an error message
            String msg = "ERROR: GPSD File not found.";
            headers.setContentType(MediaType.TEXT_PLAIN);
            return new ResponseEntity<byte[]>(msg.getBytes(), headers, HttpStatus.NOT_FOUND);
        }

        resp.setContentType("text/plain");
        resp.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", gpsd.getFilename()));

        Long fileSize = toServeUp.length();
        resp.setContentLength(fileSize.intValue());

        OutputStream outputStream = null;

        try {
            outputStream = resp.getOutputStream();
        } catch (IOException e) {
            String msg = "ERROR: Could not generate output stream.";
            headers.setContentType(MediaType.TEXT_PLAIN);
            return new ResponseEntity<byte[]>(msg.getBytes(), headers, HttpStatus.NOT_FOUND);
        }

        byte[] buffer = new byte[1024];

        int read = 0;
        try {

            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }

            //close the streams to prevent memory leaks
            outputStream.flush();
            outputStream.close();
            inputStream.close();

        } catch (Exception e) {
            String msg = "ERROR: Could not read file.";
            headers.setContentType(MediaType.TEXT_PLAIN);
            return new ResponseEntity<byte[]>(msg.getBytes(), headers, HttpStatus.NOT_FOUND);
        }

        String msg = "SUCCESS: Done.";
        headers.setContentType(MediaType.TEXT_PLAIN);
        return new ResponseEntity<byte[]>(msg.getBytes(), headers, HttpStatus.OK);
    }
    //endregion

    //region ### QueryLog Actions ###
    @RequestMapping("/querylog/main")
    public String queryLogsMain(Model model) {
        model.addAttribute("title", "Query Logs");

        return "querylog_main";
    }

    @Layout(enabled = false, value = "")
    @RequestMapping("/querylog/list")
    public String queryLogsList(Model model,
                                @RequestParam(value = "pgNo", defaultValue = "1") int pageNo,
                                @RequestParam(value = "pgSize", defaultValue = "10") int pageSize,
                                @RequestParam(value = "fromDate", required = false) String fromDate,
                                @RequestParam(value = "toDate", required = false) String toDate) throws Exception {
        // Should be a class level static variable
        SimpleDateFormat hsDateFormatter = new SimpleDateFormat("dd-MMM-yyyy");

        Date fromDt, toDt;

        if (fromDate == null || fromDate.isEmpty())
            fromDate = "01-JAN-1960";
        if (toDate == null || toDate.isEmpty())
            toDate = hsDateFormatter.format(new Date());

        try {
            fromDt = hsDateFormatter.parse(fromDate);
            toDt = hsDateFormatter.parse(toDate);
        } catch (Exception ex) {
            throw ex;
        }

        List<QueryLogDate> queryLogDates = queryLogRespository.getQueryLogDates(getActiveClusterId(), fromDt, toDt, pageNo, pageSize);
        //http://www.javacodegeeks.com/2013/03/implement-bootstrap-pagination-with-spring-data-and-thymeleaf.html
        model.addAttribute("queryLogDates", queryLogDates);

        if (!queryLogDates.isEmpty()) {
            PaginationInfo paginationInfo = new PaginationInfo(queryLogDates.get(0).getTotalRows(), pageSize, pageNo);

            model.addAttribute("pi", paginationInfo);
        }

        return "fragments/querylog_list";
    }

    @RequestMapping("/querylog/upload")
    public String queryLogsUpload(Model model) throws Exception {
        model.addAttribute("title", "Upload Query Logs");
        // Used by the Custom Thymeleaf Attribute "hs:asis"
        model.addAttribute("appRealPath", servletContext.getRealPath("/"));

        model.addAttribute("activeClusterName", getClusterName(getActiveClusterId()));

        return "querylog_upload";
    }

    @RequestMapping(value = "/querylog/upload", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public Map queryLogsUpload(@RequestParam("querylog-files[]") MultipartFile[] queryLogFiles, Model model) throws Exception {
        logger.trace("No. of files uploaded = " + queryLogFiles.length);

        int newQueryLogId = 0;
        String normalizedUserName = getUserSchemaName();
        String queryLogsBaseDir = appConfig.getQueryLogSaveDirectory() + File.separator + normalizedUserName + File.separator + "querylogs" + File.separator;
        List<UploadedFileInfo> uploadedFileInfos = new ArrayList<UploadedFileInfo>();
        Map<Integer, String> extractedQueryLogFiles = new HashMap<Integer, String>();
        Map<String, Object> files = new HashMap<String, Object>();

        // Create a database entry and Upload each QueryLog uploaded
        for (int index = 0; index < queryLogFiles.length; index++) {
            MultipartFile queryLogFile = queryLogFiles[index];
            String originalFileName = queryLogFile.getOriginalFilename();

            UploadedFileInfo uploadedFileInfo = new UploadedFileInfo();
            uploadedFileInfo.setName(originalFileName);
            uploadedFileInfo.setSize(queryLogFile.getSize());

            String checksum = org.apache.commons.codec.digest.DigestUtils.md5Hex(queryLogFile.getInputStream());
            logger.trace(String.format("Filename: %s, Checksum: %s", originalFileName, checksum));

            String uploadedBeforeResult = queryLogService.hasBeenUploadedBefore(getUserId(), checksum, originalFileName);

            if (!uploadedBeforeResult.equals("")) {
                uploadedFileInfo.setError(uploadedBeforeResult);
            } else {
                try {
                    newQueryLogId = queryLogRespository.createNew(getUserId(), originalFileName, checksum, getActiveClusterId());
                    String fileBaseDir = queryLogsBaseDir + newQueryLogId;

                    fileUtil.saveMultipartFileToPath(queryLogFile, fileBaseDir, originalFileName);

                    //fileUtil.unZip(fileBaseDir + File.separator + originalFileName, fileBaseDir);
                    // NOTE: Assuming that the Tar file only has log files, no other files or folders...
                    fileUtil.unGZipTarArchive(fileBaseDir + File.separator + originalFileName, fileBaseDir);

                    extractedQueryLogFiles.put(newQueryLogId, File.separator + normalizedUserName + File.separator + "querylogs" + File.separator + newQueryLogId);
                } catch (Exception e) {
                    // TODO: Here we will need to populate the files Map to return the BlueImp client
                    logger.error(e.getMessage());
                    //return "You failed to upload " + name + " => " + e.getMessage();
                    //model.addAttribute("error", "File upload failed");
                    //return "forward:/querylog/new";
                }
            }

            uploadedFileInfos.add(uploadedFileInfo);
        }

        // TODO: Once analyzed put entries in user inbox (Muji or me ?)
        haystaxsLibServiceWrapper.processQueryLogs(extractedQueryLogFiles, getActiveClusterId());

        files.put("files", uploadedFileInfos);
        return files;
    }

    @Layout(value = "", enabled = false)
    @RequestMapping("/querylog/topqueries/{date}")
    public String topQueries(@PathVariable("date") String forDate, Model model) {
        model.addAttribute("userQueries", userDatabaseRepository.getTopQueries(getUserSchemaName(), forDate,
                getActiveClusterId()));

        return "fragments/top_user_queries_list";
    }

    @Layout(value = "", enabled = false)
    @RequestMapping("/querylog/querycategories/{date}")
    public String queryCountByCategories(@PathVariable("date") String forDate, Model model) {
        model.addAttribute("userQueries", userDatabaseRepository.getQueryCountByCategory(getUserSchemaName(),
                forDate, getActiveClusterId()));

        return "fragments/user_queries_by_cat_list";
    }

    @RequestMapping("/querylog/analyze")
    public String analyzeQueryLog(@RequestParam(value = "date", required = false) String forDate,
                                  Model model) {
        model.addAttribute("title", "Analyze Queries");

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("HH:mm");

        QueryLogMinMaxDateTimes queryLogMinMaxDates = userDatabaseRepository.getQueryLogMinMaxDates(getUserSchemaName(),
                getActiveClusterId());

        if (queryLogMinMaxDates.getMinDate() != null && queryLogMinMaxDates.getMaxDate() != null) {
            model.addAttribute("queryLogsProcessed", true);

            if (forDate != null && !forDate.isEmpty()) {
                model.addAttribute("minDate", forDate);
                model.addAttribute("maxDate", forDate);
            } else {
                model.addAttribute("minDate", simpleDateFormat.format(queryLogMinMaxDates.getMinDate()));
                model.addAttribute("maxDate", simpleDateFormat.format(queryLogMinMaxDates.getMaxDate()));
                model.addAttribute("minTime", simpleTimeFormat.format(queryLogMinMaxDates.getMinTime()));
                model.addAttribute("maxTime", simpleTimeFormat.format(queryLogMinMaxDates.getMaxTime()));
            }
            // For QueryTypes Filter Dropdown..
            //model.addAttribute("queryTypes", userDatabaseRepository.getQueryTypes(getUserSchemaName(), forDate));
            model.addAttribute("queryTypes", userDatabaseRepository.getAllQueryTypes());
            try {
                model.addAttribute("dbNames", userDatabaseRepository.getDbNames(getUserSchemaName()));
                model.addAttribute("userNames", userDatabaseRepository.getUserNames(getUserSchemaName(), 2));
            } catch (Exception ex) {
            }
        }

        return "querylog_analysis";
    }


    @Layout(value = "", enabled = false)
    @RequestMapping("/querylog/analyze/search")
    public String queryLogAnalyzeSearch(//@RequestParam("timespan") String timeSpan,
                                        @RequestParam("startDate") String startDate,
                                        @RequestParam("endDate") String endDate,
                                        @RequestParam("startTime") String startTime,
                                        @RequestParam("endTime") String endTime,
                                        @RequestParam("dbNameLike") String dbNameLike,
                                        @RequestParam("userNameLike") String userNameLike,
                                        @RequestParam("sqlLike") String sqlLike,
                                        @RequestParam("duration") String duration,
                                        @RequestParam("queryType") String queryType,
                                        @RequestParam(value = "pgSize", defaultValue = "25") int pageSize,
                                        @RequestParam(value = "pgNo", defaultValue = "1") int pageNo,
                                        @RequestParam(value = "orderBy", defaultValue = "queryStartTime ASC") String orderBy,
                                        Model model) {
        List<UserQuery> userQueries = userDatabaseRepository.getQueries(getUserSchemaName(), getActiveClusterId(),
                startDate, endDate,
                startTime, endTime, dbNameLike, userNameLike, duration, sqlLike, queryType, pageSize, pageNo, orderBy);
        // TODO: Check if list is empty

        model.addAttribute("forDate", startDate);
        model.addAttribute("userQueries", userQueries);

        if (!userQueries.isEmpty()) {
            int totalRows = userQueries.get(0).getTotalRows();
            PaginationInfo paginationInfo = new PaginationInfo(totalRows, pageSize, pageNo);

            model.addAttribute("pi", paginationInfo);
            model.addAttribute("orderBy", orderBy.split(" ")[0]);
            model.addAttribute("orderByDir", orderBy.split(" ")[1].equals("ASC") ? "DESC" : "ASC");
        }

        return "fragments/query_analysis_list";
    }

    @RequestMapping("/querylog/ast/analyze")
    public String analyzeASTs(@RequestParam(value = "date", required = false) String forDate,
                              Model model) {
        model.addAttribute("title", "Analyze ASTs");

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("HH:mm");

        // Need a repo method for this
        model.addAttribute("queryLogsProcessed", true);

        List<Ast> asts = new ArrayList<Ast>();

        if (forDate != null && !forDate.isEmpty()) {
            asts = userDatabaseRepository.getASTs(getUserSchemaName(), getActiveClusterId(), forDate, null, null, 25, 1, " sum_duration DESC ");
            if (!asts.isEmpty()) {
                int totalRows = asts.get(0).getTotalRows();
                PaginationInfo paginationInfo = new PaginationInfo(totalRows, 25, 1);

                model.addAttribute("pi", paginationInfo);
                model.addAttribute("orderBy", "sum_duration");
                model.addAttribute("orderByDir", "DESC");
            }

            model.addAttribute("forDate", forDate);
            model.addAttribute("asts", asts);
        }

        return "ast_analysis";
    }

    @Layout(value = "", enabled = false)
    @RequestMapping("/querylog/ast/analyze/search")
    public String astAnalyzeSearch(@RequestParam("forDate") String forDate,
                                   @RequestParam("astLike") String astLike,
                                   @RequestParam("duration") String duration,
                                   @RequestParam(value = "pgSize", defaultValue = "25") int pageSize,
                                   @RequestParam(value = "pgNo", defaultValue = "1") int pageNo,
                                   @RequestParam(value = "orderBy", defaultValue = "sum_duration DESC") String orderBy,
                                   Model model) {
        List<Ast> asts = userDatabaseRepository.getASTs(getUserSchemaName(), getActiveClusterId(),
                forDate, duration, astLike, pageSize, pageNo, orderBy);
        // TODO: Check if list is empty

        model.addAttribute("forDate", forDate);
        model.addAttribute("asts", asts);

        if (!asts.isEmpty()) {
            int totalRows = asts.get(0).getTotalRows();
            PaginationInfo paginationInfo = new PaginationInfo(totalRows, pageSize, pageNo);

            model.addAttribute("pi", paginationInfo);
            model.addAttribute("orderBy", orderBy.split(" ")[0]);
            model.addAttribute("orderByDir", orderBy.split(" ")[1].equals("ASC") ? "DESC" : "ASC");
        }

        return "fragments/ast_analysis_list";
    }
    //endregion

    //region ### Workload Actions ###
    @RequestMapping(value = "/workload/create", method = RequestMethod.GET)
    public String newWorkload(Workload workload, Model model) {
        model.addAttribute("title", "Create Workoad");

        //List<String> distinctGpsds = gpsdRepository.getAllDistinct(getUserId());

        QueryLogMinMaxDateTimes queryLogMinMaxDates = userDatabaseRepository.getQueryLogMinMaxDates(getUserSchemaName(),
                getActiveClusterId());

        if (queryLogMinMaxDates.getMinDate() != null && queryLogMinMaxDates.getMaxDate() != null) {
            model.addAttribute("queryLogsProcessed", true);

            // TODO: Define this formatter at the class level
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy");

            //model.addAttribute("distinctGpsds", distinctGpsds);
            model.addAttribute("minDate", simpleDateFormat.format(queryLogMinMaxDates.getMinDate()));
            model.addAttribute("maxDate", simpleDateFormat.format(queryLogMinMaxDates.getMaxDate()));
        }

        return "workload_create";
    }

    @RequestMapping(value = "/workload/create", method = RequestMethod.POST)
    @ResponseBody
    public JsonResponse createWorkload(//@RequestParam("dbName") String dbName,
                                       @RequestParam("fromDate") String fromDate,
                                       @RequestParam("toDate") String toDate,
                                       Model model) {
        /*int maxGpsdId = 0;

        try {
            //maxGpsdId = gpsdRepository.getMaxGpsdIdByName(getUserId(), dbName);
        } catch (Exception ex) {
            return new JsonResponse(JsonResponse.FAILURE, "NO GPSD Found with dbName = " + dbName);
        }*/

        SimpleDateFormat simpleDateFormatter = new SimpleDateFormat("dd-MMM-yyyy");

        Workload workload = new Workload();
        workload.setGpsdId(getActiveClusterId());
        try {
            workload.setStartDate(simpleDateFormatter.parse(fromDate));
            workload.setEndDate(simpleDateFormatter.parse(toDate));
        } catch (ParseException e) {
            // TODO
            e.printStackTrace();
            return new JsonResponse(JsonResponse.FAILURE, "Invalid date submitted");
        }

        int newWorkloadId = workloadRepository.createNew(getUserId(), workload);

        haystaxsLibServiceWrapper.processWorkload(newWorkloadId, getUserSchemaName());

        return new JsonResponse(JsonResponse.SUCCESS, "Workload Submitted", Integer.toString(newWorkloadId));
    }

    @RequestMapping("/workload/progress")
    @ResponseBody
    public JsonResponse workloadProgress(@RequestParam("workloadId") int workloadId) {
        int progressPercent = workloadRepository.getWorkloadProgress(workloadId);

        return new JsonResponse(JsonResponse.SUCCESS, Integer.toString(progressPercent));
    }

    @RequestMapping(value = "/workload/json/{id}", method = RequestMethod.GET)
    @ResponseBody
    public String workloadJson(@PathVariable("id") int workloadId) {
        String fullPath = appConfig.getGpsdSaveDirectory() + File.separator + getUserSchemaName() + File.separator +
                "workloads" + File.separator + workloadId + ".json";

        String result;

        try {
            result = new String(Files.readAllBytes(Paths.get(fullPath)));
        } catch (java.io.IOException e) {
            result = "Workload File Not Found !";
        }

        return result;
    }

    @RequestMapping("/workload/list")
    public String visualizeWorkloads(Model model) {
        model.addAttribute("title", "Processed Workloads");
        List<Workload> workloads = workloadRepository.getLastnWorkloads(getActiveClusterId(), 10);
        model.addAttribute("workloads", workloads);

        return "workload_list";
    }

    @RequestMapping("/visualizer/{wlId}")
    public String showInVisualizer(@PathVariable("wlId") int workloadId, Model model) {
        model.addAttribute("title", "Visualizer");
        model.addAttribute("workloadId", workloadId);

        return "visualizer";
    }
    //endregion

    //region ### Miscellaneous ###
    @RequestMapping("/misc/createuserschema")
    @ResponseBody
    public String createUserSchema() {
        haystaxsLibServiceWrapper.createUserQueriesSchema(getUserSchemaName());
        return "Done";
    }

    @RequestMapping("/misc/changeactivecluster")
    @ResponseBody
    public String changeActiveCluster(@RequestParam("clusterId") int clusterId) {
        httpSession.setAttribute(HsSessionAttributes.ACTIVE_CLUSTER_ID, clusterId);
        return "success";
    }
    //endregion

    /*@ExceptionHandler(Throwable.class)
    public String handleException(Throwable t) {
        return "redirect:/errorPages/500.jsp";
    }*/

    @ExceptionHandler(Exception.class)
    public ModelAndView handleException1(Throwable t) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("errorMsg", t.toString());
        modelAndView.addObject("stackTrace", t.getStackTrace());
        modelAndView.setViewName("error");

        return modelAndView;
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseBody
    public String handleMyException(Exception exception) {
        logger.debug(exception.getMessage());
        return exception.getMessage();
    }
}
