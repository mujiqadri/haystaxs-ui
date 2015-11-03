package com.haystaxs.ui.business.entities;

import org.springframework.security.core.GrantedAuthority;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Adnan on 10/16/2015.
 */
public class HsUser {
    private Integer userId = null;
    private String firstName = null;
    private String lastName = null;
    private String emailAddress = null;
    private String organization = null;
    private Timestamp createdOn = null;
    private Timestamp lastLogin = null;
    private Timestamp regRequestedOn = null;
    private String regVerificationCode = null;
    private boolean regVerified = false;
    private String password;
    private String userName;

    public HsUser() {
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public Timestamp getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Timestamp createdOn) {
        this.createdOn = createdOn;
    }

    public Timestamp getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Timestamp lastLogin) {
        this.lastLogin = lastLogin;
    }

    public Timestamp getRegRequestedOn() {
        return regRequestedOn;
    }

    public void setRegRequestedOn(Timestamp regRequestedOn) {
        this.regRequestedOn = regRequestedOn;
    }

    public String getRegVerificationCode() {
        return regVerificationCode;
    }

    public void setRegVerificationCode(String regVerificationCode) {
        this.regVerificationCode = regVerificationCode;
    }

    public boolean isRegVerified() {
        return regVerified;
    }

    public void setRegVerified(boolean regVerified) {
        this.regVerified = regVerified;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
