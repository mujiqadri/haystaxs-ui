package com.haystaxs.ui.web.controllers;

import com.haystack.service.CatalogService;
import com.haystack.util.ConfigProperties;
import com.haystaxs.ui.business.entities.Gpsd;
import com.haystaxs.ui.business.entities.HsUser;
import com.haystaxs.ui.business.entities.QueryLog;
import com.haystaxs.ui.business.entities.repositories.GpsdRepository;
import com.haystaxs.ui.business.entities.repositories.InternalJobsRepository;
import com.haystaxs.ui.business.entities.repositories.QueryLogRespository;
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

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Adnan on 10/16/2015.
 */
@Controller
public class HomeController {
    final static Logger logger = LoggerFactory.getLogger(HomeController.class);
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

    @ModelAttribute("principal")
    private HsUser getPrincipal() {
        return (HsUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
    @ModelAttribute("userFullName")
    private String getUserFullName() {
        return getPrincipal().getFirstName();
    }

    @RequestMapping({"welcome", "/"})
    public String welcome(Model model){
        model.addAttribute("pageName", "Welcome");

        return "welcome";
    }

    @RequestMapping({"/dashboard"})
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
                List<QueryLog> queryLogs = queryLogRespository.getAllForGpsd(gpsdId, hsUser.getUserId());
                model.addAttribute("runLogs", queryLogs);
            }
        }

        model.addAttribute("pageName", "Dashboard");
        model.addAttribute("userDbs", gpsdRepository.getAllForUser(hsUser.getUserId()));

        return ("dashboard");
    }

    @RequestMapping(value = "/database/new", method = {RequestMethod.GET, RequestMethod.POST})
    public String newGpsd(Gpsd gpsd,
                          @RequestParam Map<String, String> reqParams,
                          Model model, BindingResult bindingResult) {

        HsUser hsUser = (HsUser) ((LinkedHashMap) model).get("principal");

        model.addAttribute("pageName", "Submit GPSD");

        return "createNewGpsd";
    }

    @RequestMapping(value = "/database/create", method = RequestMethod.POST)
    public String createGpsd(Gpsd gpsd,
                             @RequestParam Map<String, String> reqParams,
                             @RequestParam("gpsd-file") MultipartFile gpsdFile,
                             Model model, BindingResult bindingResult) throws Exception {

        HsUser hsUser = (HsUser) ((LinkedHashMap) model).get("principal");

        // TODO: Should be a client-side check also
        if (gpsdFile.isEmpty()) {
            model.addAttribute("error", "GPSD Sql file is required.");
            return "forward:/database/new";
        }

        // Create a database entry for the GPSD
        StringBuilder uploadedGpsdFileName = new StringBuilder();
        int newGpsdId = gpsdRepository.createNew(gpsd, hsUser.getUserId(), uploadedGpsdFileName);
        String filePath = appConfig.getGpsdSaveDirectory() + File.separator +
                miscUtil.getNormalizedUserName(hsUser.getEmailAddress()) + File.separator +
                "gpsd";

        try {
            fileUtil.SaveFileToPath(gpsdFile, filePath, uploadedGpsdFileName.toString());
        }
        catch (Exception e) {
            //return "You failed to upload " + name + " => " + e.getMessage();
            model.addAttribute("error", "File upload failed");
            return "forward:/database/new";
        }

        String gpsdFilePath = appConfig.getGpsdSaveDirectory() + File.separator + filePath;
        // NOTE: The next 2 lines should not be in the webapp code
        ConfigProperties configProperties = new ConfigProperties();
        configProperties.loadProperties();


        CatalogService cs = new CatalogService(configProperties);
        cs.executeGPSD(newGpsdId, miscUtil.getNormalizedUserName(hsUser.getEmailAddress()), gpsdFilePath);

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

        return "gpsdUploadSuccessful";
    }

    @RequestMapping("/database/delete")
    public String deleteGpsd(@RequestParam("db") int gpsdId,
                             Model model) {
        // Delete the gpsd entry
        // Remove the physical file
        // Tell BG Process to delete the backend Database created based on this GPSD
        return "redirect:/dashboard";
    }

    @RequestMapping(value = "/querylog/new", method = {RequestMethod.GET, RequestMethod.POST})
    public String newQueryLog(QueryLog queryLog,
                               @RequestParam Map<String, String> reqParams,
                               Model model, BindingResult bindingResult) {

        HsUser hsUser = (HsUser) ((LinkedHashMap) model).get("principal");

        // TODO: Check if submitted GPSD ID belongs to the user

        model.addAttribute("pageName", "Submit Query Log");

        return "submitQueryLog";
    }

    @RequestMapping(value = "/querylog/create", method = RequestMethod.POST)
    public String uploadQueryLog(QueryLog queryLog,
                             @RequestParam Map<String, String> reqParams,
                             @RequestParam("workload-file") MultipartFile queryLogFile,
                             Model model, BindingResult bindingResult) throws Exception {

        HsUser hsUser = (HsUser) ((LinkedHashMap) model).get("principal");

        // TODO: Should be a client-side check also
        if (queryLogFile.isEmpty() || !queryLogFile.getContentType().equals("application/x-zip-compressed")) {
            model.addAttribute("error", "Query Log <b>zip</b> file is required.");
            return "forward:/querylog/new";
        }

        // Create a database entry for the GPSD
        StringBuilder uploadedQueryLogDirName = new StringBuilder();
        int newQueryLogId = queryLogRespository.createNew(queryLog, queryLog.getGpsdId(), hsUser.getUserId(), uploadedQueryLogDirName);

        try {
            String zippedFilePath = appConfig.getRunlogSaveDirectory() + File.separator + uploadedQueryLogDirName.toString();
            String zipFileName = queryLogFile.getOriginalFilename();

            fileUtil.SaveFileToPath(queryLogFile, zippedFilePath, zipFileName);

            // Unzip the file
            fileUtil.unZip(zippedFilePath + File.separator + zipFileName, zippedFilePath);
        }
        catch (Exception e) {
            //return "You failed to upload " + name + " => " + e.getMessage();
            model.addAttribute("error", "File upload failed");
            return "forward:/querylog/new";
        }

        // NOTE: statusText should come from a constant
        internalJobsRepository.createNew(hsUser.getUserId(), "QUERYLOG_SUBMITTED", newQueryLogId);

        // NOTE: I don't really need to send out an email here as the next screen can show this message..

        model.addAttribute("pageName", "Query Log upload success");

        return "runLogUploadSuccessful";
    }

    @RequestMapping("/visualizer")
    public String showInVisualizer(@RequestParam("rl") int runLogId,
                                   Model model) {
        HsUser hsUser = (HsUser) ((LinkedHashMap) model).get("principal");

        /*RunLog runLog = runLogRespository.getRunLogById(runLogId, hsUser.getUserId());

        model.addAttribute("backendJSON", runLog.getModelJson());*/

        return "visualizer";
    }
}
