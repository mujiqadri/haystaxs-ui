package com.haystaxs.ui.util;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Adnan on 10/22/2015.
 */
public class HsAuthFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        if(exception.getClass().isAssignableFrom(UsernameNotFoundException.class) || exception.getClass().isAssignableFrom(BadCredentialsException.class)) {
            setDefaultFailureUrl("/login?error=1");
        } else if(exception.getClass().isAssignableFrom(AccountNotVerifiedException.class)) {
            setDefaultFailureUrl("/login?error=2");
        }
        super.onAuthenticationFailure(request, response, exception);
    }
}
