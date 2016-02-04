package com.haystaxs.ui.web.controllers;

import com.haystaxs.ui.business.entities.Cluster;
import com.haystaxs.ui.business.entities.HsUser;
import com.haystaxs.ui.business.entities.repositories.ClusterRepository;
import com.haystaxs.ui.business.entities.repositories.UserRepository;
import com.haystaxs.ui.support.ClusterTypes;
import com.haystaxs.ui.util.AppConfig;
import com.haystaxs.ui.util.EmailUtil;
import com.haystaxs.ui.util.MailUtil;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring.support.Layout;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Locale;

@Controller
public class AuthController {
    final static Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    AppConfig appConfig;
    @Autowired
    UserRepository userRepository;
    @Autowired
    private MailUtil mailUtil;
    @Autowired
    private EmailUtil emailUtil;
    @Autowired
    private ClusterRepository clusterRepository;

    @Layout(value = "", enabled = false)
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login() {
        return ("login");
    }

    @Layout(value = "", enabled = false)
    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String register(Model model) {
        if(appConfig.isDeployedOnCluster()) {
            return "login";
        }

        model.addAttribute("candidateUser", new HsUser());
        return "registerUser";
    }

    @Layout(value = "", enabled = false)
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String register(HsUser hsUser, Locale locale, Model model) throws Exception {
        if(appConfig.isDeployedOnCluster()) {
            return "login";
        }

        HsUser newHsUser = null;

        try {
            newHsUser = userRepository.createNew(hsUser);
        } catch(DuplicateKeyException dkEx) {
            logger.error(String.format("Unable to create user %s, duplicate email found.", hsUser.getEmailAddress()));
            model.addAttribute("emailInUseError", true);
            model.addAttribute("candidateUser", hsUser);
            return("registerUser");
        } catch (Exception ex) {
            logger.error(String.format("FATAL ERROR: Unable to create user %s", hsUser.getEmailAddress()));
            // this case should never happen, means something is really wrong, maybe DB down.
            throw ex;
        }

        try {
            Context context = new Context();
            context.setVariable("userName", newHsUser.getFirstName());
            context.setVariable("userId", newHsUser.getUserId());
            context.setVariable("verifyCode", newHsUser.getRegVerificationCode());
            context.setVariable("baseUrl", appConfig.getWebAppBaseUrl());

            emailUtil.sendHtmlEmail(newHsUser.getEmailAddress(), "Haystaxs - Verify Registration",
                    appConfig.getDefaultFromEmailAddress(), context, "verify-reg-email");
        } catch(Exception ex) {
            // TODO: Log this in internal errrors table
            logger.error(String.format("Error sending verification email to user with userId = %d", newHsUser.getUserId()));
        }

        return "registerUserSuccess";
    }

    @Layout(value = "", enabled = false)
    @RequestMapping(value = "/verifyreg/{userId}/{code}", method = RequestMethod.GET)
    public String verifyRegistration(@PathVariable("userId") int userId,
                                     @PathVariable("code") String verificationCode,
                                     Model model) {
        boolean userVerified = userRepository.verifyRegistration(userId, verificationCode);

        if(userVerified) {
            if(!appConfig.isDeployedOnCluster()) {
                Cluster cluster = new Cluster();
                cluster.setClusterName("DEMO Cluster 01");
                cluster.setClusterType(ClusterTypes.GREENPLUM.value());
                clusterRepository.createNewCluster(userId, cluster);
            }
        }

        model.addAttribute("userVerified", userVerified);

        return "verifyRegistrationResult";
    }

    /*@ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseBody
    public String handleMyException(Exception exception) {
        logger.debug("Handling MissingServletRequestParameterException exception");
        return exception.getMessage();
    }*/

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public String handleException1(Throwable t) {
        logger.debug("Handling any fucking exception");
        return t.getMessage();
    }
}
