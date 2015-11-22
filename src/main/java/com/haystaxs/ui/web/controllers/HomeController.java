package com.haystaxs.ui.web.controllers;

import com.haystack.domain.Query;
import com.haystack.service.CatalogService;
import com.haystack.util.ConfigProperties;
import com.haystaxs.ui.business.entities.Gpsd;
import com.haystaxs.ui.business.entities.HsUser;
import com.haystaxs.ui.business.entities.QueryLog;
import com.haystaxs.ui.business.entities.Workload;
import com.haystaxs.ui.business.entities.repositories.GpsdRepository;
import com.haystaxs.ui.business.entities.repositories.InternalJobsRepository;
import com.haystaxs.ui.business.entities.repositories.QueryLogRespository;
import com.haystaxs.ui.business.services.QueryLogService;
import com.haystaxs.ui.support.JsonResponse;
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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.thymeleaf.spring.support.Layout;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.*;

/**
 * Created by Adnan on 10/16/2015.
 */
@Controller
public class HomeController {
    final static Logger logger = LoggerFactory.getLogger(HomeController.class);

    //region ### Autowired Components ###
    @Autowired
    private MessageSource messages;
    @Autowired
    private GpsdRepository gpsdRepository;
    @Autowired
    private QueryLogRespository queryLogRespository;
    @Autowired
    private InternalJobsRepository internalJobsRepository;
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
    private boolean showBreadCrumbs() { return false; }
    //endregion

    private int getUserId() {
        return getPrincipal().getUserId();
    }

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

    //region ### GPSD Actions ###
    @RequestMapping("/gpsdees")
    public String userGpsds(Model model) {
        HsUser hsUser = (HsUser) ((LinkedHashMap) model).get("principal");

        List<Gpsd> gpsdees = gpsdRepository.getAll(hsUser.getUserId());
        model.addAttribute("gpsdees", gpsdees);

        return "gpsd_list";
    }

    @RequestMapping(value = "/gpsd/new", method = {RequestMethod.GET, RequestMethod.POST})
    public String newGpsd(Gpsd gpsd,
                          @RequestParam Map<String, String> reqParams,
                          Model model, BindingResult bindingResult) {

        HsUser hsUser = (HsUser) ((LinkedHashMap) model).get("principal");

        model.addAttribute("pageName", "Submit GPSD");

        return "gpsd_create_new";
    }

    @RequestMapping(value = "/gpsd/create", method = RequestMethod.POST)
    public String createGpsd(Gpsd gpsd,
                             @RequestParam Map<String, String> reqParams,
                             @RequestParam("gpsd-file") MultipartFile gpsdFile,
                             Model model, BindingResult bindingResult) throws Exception {

        HsUser hsUser = (HsUser) ((LinkedHashMap) model).get("principal");

        // TODO: Should be a client-side check also
        if (gpsdFile.isEmpty()) {
            model.addAttribute("error", "GPSD Sql file is required.");
            return "forward:/gpsd/new";
        }

        // Create a database entry for the GPSD
        //StringBuilder uploadedGpsdFileName = new StringBuilder();
        String originalFileName = gpsdFile.getOriginalFilename();
        int newGpsdId = gpsdRepository.createNew(gpsd, hsUser.getUserId(), originalFileName);
        String normalizedUserName = miscUtil.getNormalizedUserName(hsUser.getEmailAddress());
        String fileDirectory = appConfig.getGpsdSaveDirectory() + File.separator +
                normalizedUserName + File.separator + "gpsd" + File.separator + newGpsdId;

        logger.trace("Saving GPSD file to " + fileDirectory + File.separator + originalFileName);

        try {
            fileUtil.SaveFileToPath(gpsdFile, fileDirectory, originalFileName);
        }
        catch (Exception e) {
            logger.error(e.getMessage());
            //return "You failed to upload " + name + " => " + e.getMessage();
            model.addAttribute("error", "File upload failed");
            return "forward:/gpsd/new";
        }

        String gpsdFilePath = fileDirectory + File.separator + originalFileName;
        // NOTE: The next 2 lines should not be in the webapp code
        ConfigProperties configProperties = new ConfigProperties();
        configProperties.loadProperties();

        CatalogService cs = new CatalogService(configProperties);
        boolean hadErrors = cs.executeGPSD(newGpsdId, normalizedUserName, gpsdFilePath);

        if(hadErrors) {
            logger.debug(String.format("CatalogService.processGPSD(%d, %s, %s) ran with some errors !", newGpsdId, normalizedUserName,
                    gpsdFilePath));
        }

        // NOTE: statusText should come from a constant
        //internalJobsRepository.createNew(hsUser.getUserId(), "GPSD_SUBMITTED", newGpsdId);


        /*
        // TODO: Check this out for sending HTML emails
        // http://www.thymeleaf.org/doc/articles/springmail.html
        try {
            mailUtil.sendEmail("GPSD submitted successfully",
                    "Hi, Thanks for submitting the efffing GPSD file, go have some coffee and take a shit, we will get back to you once it's processed.",
                    new String[] {hsUser.getEmailAddress()});
        } catch (Exception ex) {
            throw ex;
            // log the e
            // What to do over here !!
        }*/

        // NOTE: I don't really need to send out an email here as the next screen can show this message..

        model.addAttribute("pageName", "GPSD upload success");

        return "gpsd_upload_successful";
    }
    //endregion

    @RequestMapping("/database/delete")
    public String deleteGpsd(@RequestParam("db") int gpsdId,
                             Model model) {
        // Delete the gpsd entry
        // Remove the physical file
        // Tell BG Process to delete the backend Database created based on this GPSD
        return "redirect:/dashboard";
    }

    //region ### QueryLog Actions ###
    @RequestMapping("/querylog/main")
    public String userQueryLogs(Model model) {
        HsUser hsUser = (HsUser) ((LinkedHashMap) model).get("principal");

        List<QueryLog> querylogs = queryLogRespository.getAll(hsUser.getUserId());
        model.addAttribute("querylogs", querylogs);

        return "querylog_main";
    }

    @Layout(enabled = false, value = "")
    @RequestMapping("/querylog/list")
    public String userQueryLogsList(Model model) {
        HsUser hsUser = (HsUser) ((LinkedHashMap) model).get("principal");

        List<QueryLog> querylogs = queryLogRespository.getAll(hsUser.getUserId());
        model.addAttribute("querylogs", querylogs);

        return "fragments/querylog_list";
    }

    @RequestMapping(value = "/querylog/new", method = {RequestMethod.GET, RequestMethod.POST})
    public String newQueryLog(QueryLog queryLog,
                               @RequestParam Map<String, String> reqParams,
                               Model model, BindingResult bindingResult) {

        HsUser hsUser = (HsUser) ((LinkedHashMap) model).get("principal");

        // TODO: Check if submitted GPSD ID belongs to the user

        model.addAttribute("pageName", "Submit Query Log");

        return "querylog_upload";
    }

    @RequestMapping(value = "/querylog/upload", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public Map uploadQueryLog(QueryLog queryLog,
                                 @RequestParam Map<String, String> reqParams,
                                 @RequestParam(value = "querylog-files") MultipartFile[] queryLogFiles,
                                 Model model, BindingResult bindingResult,
                                 HttpServletRequest request) throws Exception {

        HsUser hsUser = (HsUser) ((LinkedHashMap) model).get("principal");

        // TODO: This would now ONLY be a client side check
        /*if (queryLogFiles.length == 0 || !queryLogFile.getContentType().equals("application/x-zip-compressed")) {
            model.addAttribute("error", "Query Log <b>zip</b> file is required.");
            return "forward:/querylog/new";
        }*/

        int newQueryLogId = 0;
        String normalizedUserName = miscUtil.getNormalizedUserName(hsUser.getEmailAddress());
        String userBasedQueryLogsBaseDir = appConfig.getQueryLogSaveDirectory() + File.separator + normalizedUserName +
                File.separator + "querylogs" + File.separator;
        List<UploadedFileInfo> uploadedFileInfos = new ArrayList<UploadedFileInfo>();

        // Create a database entry and Upload each QueryLog uploaded
        for(int index=0; index < queryLogFiles.length; index++) {
            MultipartFile queryLogFile = queryLogFiles[index];
            UploadedFileInfo uploadedFileInfo = new UploadedFileInfo();
            newQueryLogId = queryLogRespository.createNew(queryLog, hsUser.getUserId());
            String fileBaseDir = userBasedQueryLogsBaseDir + newQueryLogId;

            uploadedFileInfo.setName(queryLogFile.getOriginalFilename());
            uploadedFileInfo.setSize(queryLogFile.getSize());

            try {
                String zipFileName = queryLogFile.getOriginalFilename();

                fileUtil.SaveFileToPath(queryLogFile, fileBaseDir, zipFileName);

                // Unzip the file
                fileUtil.unZip(fileBaseDir + File.separator + zipFileName, fileBaseDir);
            }
            catch (Exception e) {
                // TODO: Here we will need to populate the files Map to return the BlueImp client
                logger.error(e.getMessage());
                //return "You failed to upload " + name + " => " + e.getMessage();
                //model.addAttribute("error", "File upload failed");
                //return "forward:/querylog/new";
            }

            uploadedFileInfos.add(uploadedFileInfo);
        }

        Map<String, Object> files = new HashMap<String, Object>();
        files.put("files", uploadedFileInfos);
        return files;

        /*
        // NOTE: The next 2 lines should not be in the webapp code
        ConfigProperties configProperties = new ConfigProperties();
        configProperties.loadProperties();

        String fileBaseDirForGPFDist = File.separator + normalizedUserName + File.separator + "querylogs" + File.separator + newQueryLogId;
        CatalogService cs = new CatalogService(configProperties);
        boolean hadErrors = cs.processQueryLog(newQueryLogId, fileBaseDirForGPFDist);

        if(hadErrors) {
            logger.debug(String.format("CatalogService.processQueryLog(%d, %s, %s) ran with some errors !", newQueryLogId, normalizedUserName,
                    fileBaseDir));
        }

        // NOTE: statusText should come from a constant
        //internalJobsRepository.createNew(hsUser.getUserId(), "QUERYLOG_SUBMITTED", newQueryLogId);

        // NOTE: I don't really need to send out an email here as the next screen can show this message..

        model.addAttribute("pageName", "Query Log upload success");
        */

        //return "queryLogUploadSuccessful";
    }

    @RequestMapping(value = "/querylog/processall", produces = "application/json")
    @ResponseBody
    public JsonResponse processAllQueryLogs(Model model) {
        List<QueryLog> unprocessedQueryLogs = queryLogService.getAllUnprocessed(getUserId());

        if(unprocessedQueryLogs.size() == 0) {
            return new JsonResponse("Success", "No UNPROCESSED Query Logs found.");
        }

        // TODO: Change status of all to PROCESSING so that some other thread doesn't start the same op

        ConfigProperties configProperties = new ConfigProperties();
        try {
            configProperties.loadProperties();
        }
        catch (Exception ex) {
            logger.error("Cannot load hs-lib config properties. " + ex.getMessage());
        }
        CatalogService cs = new CatalogService(configProperties);

        String fileBaseDirForGPFDist;
        String normalizedUserName = miscUtil.getNormalizedUserName(getPrincipal().getEmailAddress());

        for(QueryLog ql: unprocessedQueryLogs) {
            fileBaseDirForGPFDist = File.separator + normalizedUserName + File.separator + "querylogs" + File.separator + ql.getQueryLogId();
            boolean hadErrors = cs.processQueryLog(ql.getQueryLogId(), fileBaseDirForGPFDist);

            if(hadErrors) {
                logger.debug(String.format("CatalogService.processQueryLog(%d, %s, %s) ran with some errors !", ql.getQueryLogId(), normalizedUserName,
                        fileBaseDirForGPFDist));
            }
        }

        return new JsonResponse("Success");
    }
    //endregion

    @RequestMapping(value = "/workload/new", method = RequestMethod.GET)
    public String newWorkload(Workload workload,
                              Model model) {
        HsUser hsUser = getPrincipal();

        List<Gpsd> gpsds = gpsdRepository.getAll(hsUser.getUserId());

        model.addAttribute("gpsds", gpsds);
        model.addAttribute("pageName", "Create Workload");

        return "createWorkload";
    }

    @RequestMapping(value = "/workload/create", method = RequestMethod.POST)
    @ResponseBody
    public String createWorkload(Workload workload,
                                 Model model) {
        return "done";
    }

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
