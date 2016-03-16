package com.haystaxs.ui.web.controllers;

import com.haystack.domain.Tables;
import com.haystack.service.database.Cluster;
import com.haystack.service.database.Greenplum;
import com.haystack.util.Credentials;
import com.haystack.util.DBConnectService;
import com.haystaxs.ui.business.entities.Gpsd;
import com.haystaxs.ui.business.entities.HsUser;
import com.haystaxs.ui.business.entities.repositories.ClusterRepository;
import com.haystaxs.ui.business.entities.repositories.UserQueriesRepository;
import com.haystaxs.ui.business.entities.repositories.UserRepository;
import com.haystaxs.ui.business.services.HaystaxsLibService;
import com.haystaxs.ui.util.FileUtil;
import com.haystaxs.ui.util.MailUtil;
import com.haystaxs.ui.util.MiscUtil;
import com.haystaxs.ui.util.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

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
    private UserQueriesRepository userDatabaseRepository;
    @Autowired
    private HaystaxsLibService haystaxsLibService;
    @Autowired
    private ClusterRepository clusterRepository;

/*
    @ModelAttribute("principal")
    private HsUser getPrincipal() {
        return (HsUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
*/

    @RequestMapping("/test/test1")
    @ResponseBody
    public String test1() {
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

    @RequestMapping("test/hslib/old")
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

    //@Cacheable(value = "jdkCache")//, key = "#root.methodName.concat('_').concat(#id)")
    @RequestMapping("/test/caching/{id}")
    @ResponseBody
    public List<Gpsd> testCaching(@PathVariable("id") String id) {
        return clusterRepository.getAllClusters(2, false);
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
        return haystaxsLibService.getGpsdJson(id);
    }

    @RequestMapping("/test/exploredb")
    public String exploreDb() {
        //return haystaxsLibService.getTablesInfoForDbExplorer();
        return "db_explorer";
    }

    @RequestMapping("/test/exploredb/json")
    @ResponseBody
    public Tables exploreDbJson() {
        return haystaxsLibService.getTablesInfoForDbExplorer(2);
    }

    @RequestMapping("/test/clearcache")
    @ResponseBody
    @CacheEvict(value = "dataCache", allEntries = true)
    public String evictAllDataCache() {
        return "DataCache Cleared";
    }

    @RequestMapping("/test/createuserschema")
    @ResponseBody
    public String createUserQuesriesSchema(@RequestParam("userId") int userId) {
        HsUser hsUser = userRepository.getById(userId);
        haystaxsLibService.createUserQueriesSchema(miscUtil.getNormalizedUserName(hsUser.getEmailAddress()));

        return "Done.";
    }

    @RequestMapping("/test/awsrs/select")
    @ResponseBody
    public String awsRedshiftSelect() throws SQLException, ClassNotFoundException {
        DBConnectService dbConnectService = new DBConnectService(DBConnectService.DBTYPE.REDSHIFT);
        Credentials credentials = new Credentials();
        credentials.setCredentials("cluster-01.cymk7frawzj0.us-west-2.redshift.amazonaws.com", "5439", "haystack", "gpadmin", "Abcd1234");
        dbConnectService.connect(credentials);

        ResultSet rs = dbConnectService.execQuery("select * from public.employees;");

        String result = "";

        while (rs.next()) {
            result += rs.getString(2);
            result += ";;";
        }

        rs.close();

        return result;
    }

    @RequestMapping("/test/hslib")
    @ResponseBody
    public String testHsLib() {
        Cluster cluster = new Greenplum();
        Credentials credentials = new Credentials();
        credentials.setCredentials("24.150.86.245", "5432", "haystack", "gpadmin", "password");
        cluster.connect(credentials);

        cluster.loadTables(credentials, false);

        return "success";
    }
    }
