package com.haystaxs.ui.web.controllers;

import com.haystaxs.ui.business.entities.RunLog;
import com.haystaxs.ui.business.entities.repositories.RunLogRespository;
import com.haystaxs.ui.business.entities.repositories.UserRepository;
import com.haystaxs.ui.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    final static Logger logger = LoggerFactory.getLogger(AuthController.class);

//    @Value("#{appProps.sfp}")
//    private String sfp;
    @Autowired
    private Environment env;
    //@Qualifier("usi")
    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RunLogRespository runLogRespository;
    @Autowired
    private MailUtil mailUtil;
    @Autowired
    private MiscUtil miscUtil;
    @Autowired
    private FileUtil fileUtil;

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
        RunLog runLog = runLogRespository.getRunLogById(1, 1);

        model.addAttribute("backendJSON", runLog.getModelJson());

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
}
