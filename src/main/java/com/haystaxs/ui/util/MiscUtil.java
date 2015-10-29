package com.haystaxs.ui.util;

import org.springframework.stereotype.Component;

/**
 * Created by Adnan on 10/27/2015.
 */
@Component
public class MiscUtil {
    public String getNormalizedUserName(String email) {
        return(email.replace(".", "_dot_")
                .replace("@", "_at_"));
    }
}
