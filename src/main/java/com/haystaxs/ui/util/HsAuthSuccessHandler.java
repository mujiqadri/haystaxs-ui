package com.haystaxs.ui.util;

import com.haystaxs.ui.business.entities.HsUser;
import com.haystaxs.ui.business.entities.repositories.ClusterRepository;
import com.haystaxs.ui.support.HsSessionAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by adnan on 2/8/16.
 */
public class HsAuthSuccessHandler implements AuthenticationSuccessHandler {
    final static Logger logger = LoggerFactory.getLogger(AuthenticationSuccessHandler.class);

    @Autowired
    private ClusterRepository clusterRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                        Authentication authentication) throws IOException, ServletException {
        HsUser hsUser = (HsUser) authentication.getPrincipal();
        try {
            int defaultClusterId = clusterRepository.getDefaultClusterId(hsUser.getUserId());
            httpServletRequest.getSession().setAttribute(HsSessionAttributes.ACTIVE_CLUSTER_ID, defaultClusterId);
        } catch (EmptyResultDataAccessException erdaEx) {
            // This means that the user must create a cluster or upload a GPSD after logging in
            //httpServletRequest.getSession().setAttribute(HsSessionAttributes.ACTIVE_CLUSTER_ID, 0);
        } catch(Exception ex) {
            logger.error("This should not be happening. Ex: {}", ex.getMessage());
        }

        httpServletResponse.sendRedirect("dashboard");
    }
}
