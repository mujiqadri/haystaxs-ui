package com.haystaxs.ui.web.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Adnan on 10/26/2015.
 */
@Controller
public class ErrorController {
    @RequestMapping("/404")
    @ResponseBody
    public String handle404(ModelMap model, HttpServletRequest request) {
        return "No such URL exists. ";
    }

    @RequestMapping("/error")
    public String error(Model model) {
        return "error";
    }

    // NOTE: This is conflicting with the mvc:resources tag, its taking precedence over it !!
    /*
    @RequestMapping("*//**")
    @ResponseBody
    // NOTE: This will always be hit for a 404 before the web.xml can process the request
    public String matchAnyRequest(HttpServletRequest request) {
        return "Page not found da da da da da da da.";
    }*/
}
