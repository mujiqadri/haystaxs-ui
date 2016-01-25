package com.haystaxs.ui.web.controllers;

import com.haystack.domain.Tables;
import com.haystaxs.ui.business.entities.repositories.UserDatabaseRepository;
import com.haystaxs.ui.business.entities.repositories.UserRepository;
import com.haystaxs.ui.business.services.HaystaxsLibService;
import com.haystaxs.ui.support.JsonResponse;
import com.haystaxs.ui.util.FileUtil;
import com.haystaxs.ui.util.MailUtil;
import com.haystaxs.ui.util.MiscUtil;
import com.haystaxs.ui.util.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * Created by Adnan on 10/21/2015.
 */
@Controller
public class TestController {
    final static Logger logger = LoggerFactory.getLogger(TestController.class);

//    @Value("#{appProps.sfp}")
//    private String sfp;
    @Autowired
    private Environment env;
    //@Qualifier("usi")
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MailUtil mailUtil;
    @Autowired
    private MiscUtil miscUtil;
    @Autowired
    private FileUtil fileUtil;
    @Autowired
    private UserDatabaseRepository userDatabaseRepository;
    @Autowired
    private HaystaxsLibService haystaxsLibServiceWrapper;

/*
    @ModelAttribute("principal")
    private HsUser getPrincipal() {
        return (HsUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
*/

    @RequestMapping("/test/test1")
    @ResponseBody
    public String test1() {
        userRepository.selectTest();
        return("chal gia bhan ka lora");
    }

    @RequestMapping("/test/randomcontent")
    @ResponseBody
    public String randomContent() {
        return("Lorum ipsum bahin ka dipsum");
    }

    @RequestMapping("/test/sendmail")
    @ResponseBody
    public String testMail() throws Exception {
        mailUtil.sendEmail("GPSD submitted successfully",
                "Hi, Thanks for submitting the efffing GPSD file, go have some coffee and take a shit, we will get back to you once it's processed.",
                new String[] {"mujtaba.qadri@gmail.com", "adnan.hussain@danatev.com"});

        return "Email sent";
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseBody
    public String handle404() {
        return ("Sorry 404 Baby");
    }

    /*
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public String handleAnyException() {
        return ("All exceptions caught here");
    }
    */

    @RequestMapping("/test/tm")
    public String testMsgs() {
        return "testMsgs";
    }

    @RequestMapping("/test/pg/{pgName}")
    public String testPage(@PathVariable String pgName, Model model) {
        //RunLog runLog = runLogRespository.getRunLogById(1, 1);

        //model.addAttribute("backendJSON", runLog.getModelJson());

        return pgName;
    }

    @RequestMapping("ex1")
    public String ex1() {
        throw new ResourceNotFoundException();
    }

    @RequestMapping("exany")
    public String exAny() throws Exception {
        throw new Exception("Hello errornous");
    }

    @RequestMapping("test/hslib")
    @ResponseBody
    public String testHaystackLib(Model model) throws IOException {
        /*ConfigProperties configProperties = new ConfigProperties();
        configProperties.loadProperties();
        CatalogService cs = new CatalogService(configProperties);*/

        return "Success";
    }

    @RequestMapping("test/ftp")
    @ResponseBody
    public String testFtp() throws IOException {
        fileUtil.uploadFileToFtp(null, "chikna@chiknee.chicken/gpsd", null);

        return "FTP Success";
    }

    @RequestMapping("/test/log4j")
    @ResponseBody
    public String testLog4j() {
        logger.debug("The quick brown fox jumps over the lazy dog and the lazy dog just sits there doing fucking nothing !!!");
        return "Done logging";
    }

    @RequestMapping("/test/newlayout")
    public String testNewLayout(Model model) {
        model.addAttribute("title", "Dashboard 101");
        return "blank_template_page";
    }

    @Cacheable(value = "dataCache", key = "#root.methodName.concat('_').concat(#id)")
    @RequestMapping("/test/caching/{id}")
    @ResponseBody
    public JsonResponse testCaching(@PathVariable("id") String id) {
        return new JsonResponse("Working", "What the fuck");
    }

    /*@RequestMapping("/visualizer/{wlId}")
    public String showInVisualizer(@PathVariable("wlId") int workloadId, Model model) {
        //HsUser hsUser = (HsUser) ((LinkedHashMap) model).get("principal");

        *//*RunLog runLog = runLogRespository.getRunLogById(runLogId, hsUser.getUserId());

        model.addAttribute("backendJSON", runLog.getModelJson());*//*

        return "visualizer";
    }*/

    @RequestMapping("/test/gpsdjson")
    @ResponseBody
    public String gpsdJson(@RequestParam("id") int id) {
        return haystaxsLibServiceWrapper.getGpsdJson(id);
    }

    @RequestMapping("/test/exploredb")
    public String exploreDb() {
        //return haystaxsLibServiceWrapper.getTablesInfoForDbExplorer();
        return "db_explorer";
    }

    @RequestMapping("/test/exploredb/json")
    @ResponseBody
    public Tables exploreDbJson() {
        return haystaxsLibServiceWrapper.getTablesInfoForDbExplorer();
    }
}
