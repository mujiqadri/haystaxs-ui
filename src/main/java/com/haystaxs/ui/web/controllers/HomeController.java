package com.haystaxs.ui.web.controllers;

import com.haystaxs.ui.business.entities.*;
import com.haystaxs.ui.business.entities.repositories.*;
import com.haystaxs.ui.business.services.HaystaxsLibService;
import com.haystaxs.ui.business.services.QueryLogService;
import com.haystaxs.ui.support.UploadedFileInfo;
import com.haystaxs.ui.util.AppConfig;
import com.haystaxs.ui.util.FileUtil;
import com.haystaxs.ui.util.MiscUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.thymeleaf.spring.support.Layout;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;
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
    private HaystaxsLibService haystaxsLibService;
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
    public String dashBoard(//@RequestParam(value = "db", required = false) Integer db,
                            //RunLog runLog,
                            @RequestParam Map<String, String> reqParams,
                            //Principal principal,
                            Model model) {
//        model.addAttribute("msg", messages.getMessage("dashboard.msg1", null, new Locale("en", "US")));
//        model.addAttribute("msg2", "This is the house that jack built");
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        HsUser hsUser = (HsUser) ((LinkedHashMap) model).get("principal");

        // Check is the param db is present and if the db belongs to this user.
        if (reqParams.containsKey("db")) {
            int gpsdId = Integer.parseInt(reqParams.get("db"));
            Gpsd gpsd = gpsdRepository.getSingle(gpsdId, hsUser.getUserId());
            if (gpsd != null) {
                model.addAttribute("selectedDb", gpsd);
                List<QueryLog> queryLogs = queryLogRespository.getAll(hsUser.getUserId());
                model.addAttribute("runLogs", queryLogs);
            }
        }

        model.addAttribute("pageName", "Dashboard");
        //model.addAttribute("userDbs", gpsdRepository.getAll(hsUser.getUserId()));

        return ("dashboard");
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

        String originalFileName = gpsdFile.getOriginalFilename();
        int newGpsdId = gpsdRepository.createNew(getUserId(), originalFileName);
        String normalizedUserName = getNormalizedUserName();
        String gpsdFileDirectory = appConfig.getGpsdSaveDirectory() + File.separator + normalizedUserName + File.separator + "gpsd" + File.separator + newGpsdId;
        String gpsdFilePath = gpsdFileDirectory + File.separator + originalFileName;
        UploadedFileInfo uploadedFileInfo = new UploadedFileInfo();

        logger.trace("Saving GPSD file to " + gpsdFilePath);

        try {
            fileUtil.saveMultipartFileToPath(gpsdFile, gpsdFileDirectory, originalFileName);

            logger.trace("About to invoke Async method createGPSD.");
            haystaxsLibService.createGPSD(newGpsdId, normalizedUserName, gpsdFilePath);
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

    @RequestMapping(value = "/gpsd/file/{gpsdId}")
    @ResponseBody
    public String gpsdFile(@PathVariable("gpsdId") int gpsdId) {
        Gpsd gpsd = gpsdRepository.getSingle(gpsdId, getUserId());

        String fullPath = appConfig.getGpsdSaveDirectory() + File.separator + getNormalizedUserName() + File.separator +
                "gpsd" + File.separator + gpsdId + File.separator + gpsd.getFilename();

        String result = "";

        try {
            result = new String(Files.readAllBytes(Paths.get(fullPath)));
        } catch (java.io.IOException e) {
            result = "GPSD File Not Found !";
        }

        return result;
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
        // Used by the Custom Thymeleaf Attribute "hs:asis"
        model.addAttribute("appRealPath", servletContext.getRealPath("/"));

        return "querylog_main";
    }

    @Layout(enabled = false, value = "")
    @RequestMapping("/querylog/list")
    public String userQueryLogsList(Model model) {
        List<QueryLogDate> queryLogDates = queryLogRespository.getAllQueryLogDates(getUserId());
        model.addAttribute("queryLogDates", queryLogDates);

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

                    // TODO: Need to fix this for tar.gz
                    fileUtil.unZip(fileBaseDir + File.separator + originalFileName, fileBaseDir);

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
        haystaxsLibService.analyzeQueryLogs(extractedQueryLogFiles);

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
    //endregion

    //region ### Workload Actions ###
    @RequestMapping(value = "/workload/create", method = RequestMethod.GET)
    public String newWorkload(Workload workload, Model model) {
        List<String> distinctGpsds = gpsdRepository.getAllDistinct(getUserId());

        model.addAttribute("distinctGpsds", distinctGpsds);

        return "workload_create";
    }

    @RequestMapping(value = "/workload/create", method = RequestMethod.POST)
    @ResponseBody
    public String createWorkload(@RequestParam("dbName") String dbName,
                                 @RequestParam("fromDate") String fromDate,
                                 @RequestParam("toDate") String toDate,
                                 Model model) {
        int maxGpsdId = 0;

        try {
            maxGpsdId = gpsdRepository.getMaxGpsdIdByName(getUserId(), dbName);
        } catch (Exception ex) {
            return "NO GPSD Found with dbName = " + dbName;
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

            return "Invalid date submitted";
        }

        int newWorkloadId = workloadRepository.createNew(getUserId(), workload);

        String modelJson = haystaxsLibService.processWorkload(newWorkloadId);

        String jsonFileBaseDir = appConfig.getGpsdSaveDirectory() + File.separator + getNormalizedUserName() + File.separator
                + "workloads" + File.separator;

        try {
            fileUtil.saveToFile(modelJson.getBytes(), jsonFileBaseDir, Integer.toString(newWorkloadId) + ".json");
        } catch (IOException e) {
            // TODO: log the error
            e.printStackTrace();
        }

        return modelJson;
    }
    //endregion

    @RequestMapping("/visualizer")
    public String showInVisualizer(@RequestParam("rl") int runLogId,
                                   Model model) {
        HsUser hsUser = (HsUser) ((LinkedHashMap) model).get("principal");

        /*RunLog runLog = runLogRespository.getRunLogById(runLogId, hsUser.getUserId());

        model.addAttribute("backendJSON", runLog.getModelJson());*/

        return "visualizer";
    }

    @ExceptionHandler(Throwable.class)
    public String handleException(Throwable t) {
        return "redirect:/errorPages/500.jsp";
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleException1(Throwable t) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("errorMsg", t.toString());
        modelAndView.setViewName("error");

        return modelAndView;
    }
}
