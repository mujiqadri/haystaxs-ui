package com.haystaxs.ui.util;

import org.springframework.security.authentication.AccountStatusException;

/**
 * Created by Adnan on 10/22/2015.
 */
public class AccountNotVerifiedException extends AccountStatusException {
    public AccountNotVerifiedException(String msg) {
        super(msg);
    }

    public AccountNotVerifiedException(String msg, Throwable t) {
        super(msg, t);
    }
}
