package com.haystaxs.ui.web.interceptors;

import com.haystaxs.ui.support.HsSessionAttributes;
import com.haystaxs.ui.util.AppConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by adnan on 2/11/16.
 */
public class CheckClusterAvailableInterceptor extends HandlerInterceptorAdapter {
    final static Logger logger = LoggerFactory.getLogger(CheckClusterAvailableInterceptor.class);

    @Autowired
    private AppConfig appConfig;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        logger.trace("Intercepting {}", request.getRequestURI());

        int activeClusterId = 0;

        try {
            activeClusterId = (Integer) request.getSession().getAttribute(HsSessionAttributes.ACTIVE_CLUSTER_ID);
        } catch (Exception ex) {

        }

        if(activeClusterId == 0) {
            if(appConfig.isDeployedOnCluster()) {
                response.sendRedirect(request.getContextPath() + "/cluster/add");
            } else {
                response.sendRedirect(request.getContextPath() + "/gpsd/upload");
            }
            return false;
        }

        return super.preHandle(request, response, handler);
    }
}
