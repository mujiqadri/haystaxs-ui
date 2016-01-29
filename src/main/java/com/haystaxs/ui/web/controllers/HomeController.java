package com.haystaxs.ui.web.controllers;

import com.haystaxs.ui.business.entities.*;
import com.haystaxs.ui.business.entities.repositories.*;
import com.haystaxs.ui.business.entities.selection.QueryLogMinMaxDateTimes;
import com.haystaxs.ui.business.services.HaystaxsLibService;
import com.haystaxs.ui.business.services.QueryLogService;
import com.haystaxs.ui.support.JsonResponse;
import com.haystaxs.ui.support.PaginationInfo;
import com.haystaxs.ui.support.UploadedFileInfo;
import com.haystaxs.ui.util.AppConfig;
import com.haystaxs.ui.util.FileUtil;
import com.haystaxs.ui.util.MiscUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.thymeleaf.spring.support.Layout;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
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
    private UserDatabaseRepository userDatabaseRepository;
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
    //endregion

    //region ### Controller Level Helper Methods ###
    private int getUserId() {
        return getPrincipal().getUserId();
    }

    private String getNormalizedUserName() {
        return miscUtil.getNormalizedUserName(getPrincipal().getEmailAddress());
    }
    //endregion

    //region ### Dashboard Action ###
    @RequestMapping({"/dashboard", "/"})
    public String dashBoard(Model model) {
        model.addAttribute("title", "Dashboard");

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("HH:mm");

        QueryLogMinMaxDateTimes queryLogMinMaxDates = userDatabaseRepository.getQueryLogMinMaxDates(getNormalizedUserName());

        /*if (forDate != null && !forDate.isEmpty()) {
            model.addAttribute("minDate", forDate);
            model.addAttribute("maxDate", forDate);
        } else*/
        {
            model.addAttribute("minDate", simpleDateFormat.format(queryLogMinMaxDates.getMinDate()));
            model.addAttribute("maxDate", simpleDateFormat.format(queryLogMinMaxDates.getMaxDate()));
            model.addAttribute("minTime", simpleTimeFormat.format(queryLogMinMaxDates.getMinTime()));
            model.addAttribute("maxTime", simpleTimeFormat.format(queryLogMinMaxDates.getMaxTime()));
        }
        // For QueryTypes Filter Dropdown..
        //model.addAttribute("queryTypes", userDatabaseRepository.getQueryTypes(getNormalizedUserName(), forDate));
        //model.addAttribute("queryTypes", userDatabaseRepository.getAllQueryTypes());
        try {
            model.addAttribute("dbNames", userDatabaseRepository.getDbNames(getNormalizedUserName()));
            model.addAttribute("userNames", userDatabaseRepository.getUserNames(getNormalizedUserName()));
        } catch (Exception ex) {
        }

        return ("dashboard");
    }

    @RequestMapping("/dashboard/ql/chartdata")
    @ResponseBody
    public List<UserQueryChartData> dashboardQueryLogChartData(@RequestParam(value = "fromDate", required = false) String fromDate,
                                                               @RequestParam(value = "toDate", required = false) String toDate,
                                                               @RequestParam(value = "dbName", required = false) String dbName,
                                                               @RequestParam(value = "userName", required = false) String userName) {

        List<UserQueryChartData> result = userDatabaseRepository.getQueryStatsForChart(getNormalizedUserName(), fromDate, toDate,
                dbName, userName);

        return (result);
    }

    @RequestMapping("/dashboard/ql/hourlyavgchartdata")
    @ResponseBody
    public List<UserQueryChartData> hourlyQueryLogChartData(@RequestParam(value = "fromDate", required = false) String fromDate,
                                                            @RequestParam(value = "toDate", required = false) String toDate,
                                                            @RequestParam(value = "dbName", required = false) String dbName,
                                                            @RequestParam(value = "userName", required = false) String userName,
                                                            @RequestParam("sqlWindowOp") String windowOp) {
        List<UserQueryChartData> result = userDatabaseRepository.getHourlyQueryStatsForChart(getNormalizedUserName(), fromDate, toDate,
                dbName, userName, windowOp);

        // The resulting list may contain less or none rows, so the below is a workaround as the chart needs full data
        if (result.size() < 25) {
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
            for(int i = 0; i < 24; i++) {
                if(finalResult[i] == null) {
                    finalResult[i] = new UserQueryChartData(Integer.toString(i));
                }
            }

            return (Arrays.asList(finalResult));
        }

        return (result);
    }

    //endregion

    //region ### GPSD Actions ###
    @RequestMapping("gpsd/main")
    public String gpsdMain(Model model) {
        // Used by the Custom Thymeleaf Attribute "hs:asis"
        model.addAttribute("appRealPath", servletContext.getRealPath("/"));

        return "gpsd_main";
    }

    @RequestMapping(value = "/gpsd/upload", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public Map gpsdUpload(@RequestParam(value = "gpsd-file") MultipartFile gpsdFile) {
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
        int newGpsdId = gpsdRepository.createNew(getUserId(), originalFileName);
        String normalizedUserName = getNormalizedUserName();
        String gpsdFileDirectory = appConfig.getGpsdSaveDirectory() + File.separator + normalizedUserName + File.separator + "gpsd" + File.separator + newGpsdId;
        String gpsdFilePath = gpsdFileDirectory + File.separator + originalFileName;

        logger.trace("Saving GPSD file to " + gpsdFilePath);

        try {
            fileUtil.saveMultipartFileToPath(gpsdFile, gpsdFileDirectory, originalFileName);

            logger.trace("About to invoke Async method createGPSD.");
            haystaxsLibServiceWrapper.createGPSD(newGpsdId, normalizedUserName, gpsdFilePath);
            // TODO: Assuming GPSD has been created successfully, do an entry in user inbox (should be done by Muji ?)
            logger.trace("createGPSD invoked as a separate thread.");
        } catch (Exception e) {
            logger.error("Error whle saving GPSD file on server. Exception: ", e.getMessage());
            // TODO: Log internal error. Should we also rollback the DB Entry ??
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

        String fullPath = appConfig.getGpsdSaveDirectory() + File.separator + getNormalizedUserName() + File.separator +
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
        String fullPath = appConfig.getGpsdSaveDirectory() + File.separator + getNormalizedUserName() + File.separator +
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

    @Layout(enabled = false, value = "")
    @RequestMapping("/gpsd/list")
    public String userGpsds(Model model) {
        List<Gpsd> gpsdees = gpsdRepository.getAll(getUserId());
        model.addAttribute("gpsdees", gpsdees);

        return "fragments/gpsd_list";
    }

    @RequestMapping("/gpsd/delete/{id}")
    public String deleteGpsd(@PathVariable("id") int gpsdId, Model model) {
        // Delete the gpsd entry
        // Remove the physical file
        // Tell BG Process to delete the backend Database created based on this GPSD
        throw new NotImplementedException();
    }
    //endregion

    //region ### QueryLog Actions ###
    @RequestMapping("/querylog/main")
    public String userQueryLogs(Model model) {
        model.addAttribute("title", "Query Logs");
        // Used by the Custom Thymeleaf Attribute "hs:asis"
        model.addAttribute("appRealPath", servletContext.getRealPath("/"));

        return "querylog_main";
    }

    @Layout(enabled = false, value = "")
    @RequestMapping("/querylog/list")
    public String userQueryLogsList(Model model,
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

        List<QueryLogDate> queryLogDates = queryLogRespository.getQueryLogDates(getUserId(), fromDt, toDt, pageNo, pageSize);
        //http://www.javacodegeeks.com/2013/03/implement-bootstrap-pagination-with-spring-data-and-thymeleaf.html
        model.addAttribute("queryLogDates", queryLogDates);

        if (!queryLogDates.isEmpty()) {
            PaginationInfo paginationInfo = new PaginationInfo(queryLogDates.get(0).getTotalRows(), pageSize, pageNo);

            model.addAttribute("pi", paginationInfo);
        }

        return "fragments/querylog_list";
    }

    @RequestMapping(value = "/querylog/upload", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public Map uploadQueryLog(@RequestParam("querylog-files[]") MultipartFile[] queryLogFiles) throws Exception {
        logger.trace("No. of files uploaded = " + queryLogFiles.length);

        int newQueryLogId = 0;
        String normalizedUserName = getNormalizedUserName();
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
                newQueryLogId = queryLogRespository.createNew(getUserId(), originalFileName, checksum);
                String fileBaseDir = queryLogsBaseDir + newQueryLogId;

                try {
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
        haystaxsLibServiceWrapper.analyzeQueryLogs(extractedQueryLogFiles);

        files.put("files", uploadedFileInfos);
        return files;
    }

    @Layout(value = "", enabled = false)
    @RequestMapping("/querylog/topqueries/{date}")
    public String topQueries(@PathVariable("date") String forDate, Model model) {
        model.addAttribute("userQueries", userDatabaseRepository.getTopQueries(getNormalizedUserName(), forDate));

        return "fragments/top_user_queries_list";
    }

    @Layout(value = "", enabled = false)
    @RequestMapping("/querylog/querycategories/{date}")
    public String queryCountByCategories(@PathVariable("date") String forDate, Model model) {
        model.addAttribute("userQueries", userDatabaseRepository.getQueryCountByCategory(getNormalizedUserName(), forDate));

        return "fragments/user_queries_by_cat_list";
    }

    @RequestMapping("/querylog/analyze")
    public String analyzeQueryLog(@RequestParam(value = "date", required = false) String forDate,
                                  Model model) {
        model.addAttribute("title", "Analyze Queries");

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("HH:mm");

        QueryLogMinMaxDateTimes queryLogMinMaxDates = userDatabaseRepository.getQueryLogMinMaxDates(getNormalizedUserName());

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
        //model.addAttribute("queryTypes", userDatabaseRepository.getQueryTypes(getNormalizedUserName(), forDate));
        model.addAttribute("queryTypes", userDatabaseRepository.getAllQueryTypes());
        try {
            model.addAttribute("dbNames", userDatabaseRepository.getDbNames(getNormalizedUserName()));
            model.addAttribute("userNames", userDatabaseRepository.getUserNames(getNormalizedUserName()));
        } catch (Exception ex) {
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
        List<UserQuery> userQueries = userDatabaseRepository.getQueries(getNormalizedUserName(), startDate, endDate,
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
    //endregion

    //region ### Workload Actions ###
    @RequestMapping(value = "/workload/create", method = RequestMethod.GET)
    public String newWorkload(Workload workload, Model model) {
        model.addAttribute("title", "Create Workoad");

        List<String> distinctGpsds = gpsdRepository.getAllDistinct(getUserId());

        QueryLogMinMaxDateTimes queryLogMinMaxDates = userDatabaseRepository.getQueryLogMinMaxDates(getNormalizedUserName());

        // TODO: Define this formatter at the class level
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy");

        model.addAttribute("distinctGpsds", distinctGpsds);
        model.addAttribute("minDate", simpleDateFormat.format(queryLogMinMaxDates.getMinDate()));
        model.addAttribute("maxDate", simpleDateFormat.format(queryLogMinMaxDates.getMaxDate()));

        return "workload_create";
    }

    @RequestMapping(value = "/workload/create", method = RequestMethod.POST)
    @ResponseBody
    public JsonResponse createWorkload(@RequestParam("dbName") String dbName,
                                       @RequestParam("fromDate") String fromDate,
                                       @RequestParam("toDate") String toDate,
                                       Model model) {
        int maxGpsdId = 0;

        try {
            maxGpsdId = gpsdRepository.getMaxGpsdIdByName(getUserId(), dbName);
        } catch (Exception ex) {
            return new JsonResponse(JsonResponse.FAILURE, "NO GPSD Found with dbName = " + dbName);
        }

        SimpleDateFormat simpleDateFormatter = new SimpleDateFormat("dd-MMM-yyyy");

        Workload workload = new Workload();
        workload.setGpsdId(maxGpsdId);
        try {
            workload.setStartDate(simpleDateFormatter.parse(fromDate));
            workload.setEndDate(simpleDateFormatter.parse(toDate));
        } catch (ParseException e) {
            // TODO
            e.printStackTrace();
            return new JsonResponse(JsonResponse.FAILURE, "Invalid date submitted");
        }

        int newWorkloadId = workloadRepository.createNew(getUserId(), workload);

        haystaxsLibServiceWrapper.processWorkload(newWorkloadId, getNormalizedUserName());

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
        String fullPath = appConfig.getGpsdSaveDirectory() + File.separator + getNormalizedUserName() + File.separator +
                "workloads" + File.separator + workloadId + ".json";

        String result;

        try {
            result = new String(Files.readAllBytes(Paths.get(fullPath)));
        } catch (java.io.IOException e) {
            result = "Workload File Not Found !";
        }

        return result;
    }

    @RequestMapping("/workloads/list")
    public String visualizeWorkloads(Model model) {
        model.addAttribute("title", "Processed Workloads");
        List<Workload> workloads = workloadRepository.getLastnWorkloads(getUserId(), 10);
        model.addAttribute("workloads", workloads);

        return "visualize_workloads";
    }
    //endregion

    @RequestMapping("/visualizer/{wlId}")
    public String showInVisualizer(@PathVariable("wlId") int workloadId, Model model) {
        model.addAttribute("title", "Visualizer");
        model.addAttribute("workloadId", workloadId);

        return "visualizer";
    }

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
