package com.haystaxs.ui.util;

import com.haystaxs.ui.business.entities.HsUser;
import com.haystaxs.ui.business.entities.repositories.UserRepository;
import com.haystaxs.ui.web.controllers.AuthController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.*;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.servlet.ServletException;
import java.util.ArrayList;

/**
 * Created by Adnan on 10/22/2015.
 */
@Component
public class HsAuthProvider extends AbstractUserDetailsAuthenticationProvider {
    final static Logger logger = LoggerFactory.getLogger(AuthController.class);

    //@Qualifier("userDetailsServiceImpl")
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AppConfig appConfig;

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) throws AuthenticationException {
        throw new NotImplementedException();
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String emailAddress = authentication.getName();
        String password = (String) authentication.getCredentials();

        HsUser hsUser = (HsUser) userRepository.findUserByEmail(emailAddress);

        if(hsUser == null) {
            logger.info(String.format("Authentication failed. Email address %s not found.", emailAddress));
            throw new UsernameNotFoundException("No such email exists !");
        } else if(!password.equals(hsUser.getPassword())) {
            logger.info(String.format("Authentication failed. Incorrect password entered for %s not found.", emailAddress));
            throw new BadCredentialsException("Incorrect Password entered !");
        } else if(!hsUser.isRegVerified()) {
            logger.info(String.format("Authentication failed. Registration not yet verified for %s.", emailAddress));
            throw new AccountNotVerifiedException("Please verify yourself first.");
        } else if(appConfig.isDeployedOnCluster() && hsUser.getUserId() != 1 && hsUser.isAdmin()) {
            throw new InternalAuthenticationServiceException("The application is configured to be deployed on a cluster, this user is cannot be an admin.");
        }

        // TODO: Update last_login

        return(new UsernamePasswordAuthenticationToken(hsUser, password, new ArrayList<GrantedAuthority>() {{
            add(new SimpleGrantedAuthority("ROLE_USER"));
        }}));
    }

    @Override
    protected UserDetails retrieveUser(String s, UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) throws AuthenticationException {
        throw new NotImplementedException();
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }
}
