package com.haystaxs.ui.web.controllers;

import com.haystaxs.ui.business.entities.HsUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by Adnan on 10/17/2015.
 */
@Controller
public class AuthController {
    final static Logger logger = LoggerFactory.getLogger(AuthController.class);

    @RequestMapping(value = "login", method = RequestMethod.GET)
    public String login() {
        return ("login");
    }

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String register() {
        return "registerUser";
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String register(HsUser hsUser) {

        return "registerUserSuccessful";
    }
}
