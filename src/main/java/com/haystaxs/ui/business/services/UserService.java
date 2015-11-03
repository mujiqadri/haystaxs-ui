package com.haystaxs.ui.business.services;

import com.haystaxs.ui.business.entities.HsUser;
import com.haystaxs.ui.business.entities.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Adnan on 10/17/2015.
 */
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public UserService() {}

    public void registerUser(HsUser hsUser) {
        userRepository.createNew(hsUser);
    }
}
