package com.haystaxs.ui.web.controllers;

import com.haystaxs.ui.business.entities.HsUser;
import com.haystaxs.ui.business.entities.repositories.UserRepository;
import com.haystaxs.ui.util.MailUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.spring.support.Layout;

@Controller
public class AuthController {
    final static Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    UserRepository userRepository;
    @Autowired
    private MailUtil mailUtil;

    @Layout(value = "", enabled = false)
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login() {
        return ("login");
    }

    @Layout(value = "", enabled = false)
    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String register() {
        return "registerUser";
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String register(HsUser hsUser) {
        //int userId = userRepository.createNew(hsUser);

        int userId = 991;

        mailUtil.sendEmail("Verify Registration",
                String.format("Congratulations on registering for Haystaxs. Your userId is %1s", userId),
                new String[] { "adnanshussain@gmail.com" });

        return "registerUserSuccessful";
    }

    @Layout(value = "", enabled = false)
    @RequestMapping(value = "/regdone", method = RequestMethod.GET)
    public String registerSuccess() {
        mailUtil.sendEmail("Verify Registration",
                String.format("Congratulations on registering for Haystaxs. Your userId is %1s", 2134),
                new String[] { "adnanshussain@gmail.com" });

        return "registerUserSuccess";
    }

    @Layout(value = "", enabled = false)
    @RequestMapping(value = "/verifyreg", method = RequestMethod.GET)
    public String verifyRegistration(@RequestParam("id") int userId,
                                     @RequestParam("code") String verificationCode,
                                     Model model) {

        boolean userVerified = userRepository.verifyRegistration(userId, verificationCode);

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
