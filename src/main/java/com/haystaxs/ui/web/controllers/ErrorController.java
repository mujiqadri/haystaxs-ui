package com.haystaxs.ui.web.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by Adnan on 10/26/2015.
 */
@Controller
public class ErrorController {
    @RequestMapping("/404")
    @ResponseBody
    public String handle404() {
        return "No such URL exists for this application shin shin shin shin shin shin.";
    }

    // NOTE: This is conflicting with the mvc:resources tag, its taking precedence over it !!
    /*@RequestMapping("*//**")
    @ResponseBody
    *//* NOTE: This will always be hit for a 404 before the web.xml can process the request *//*
    public String matchAnyRequest() {
        return "Page not found da da da da da da da.";
    }*/
}
