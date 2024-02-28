package com.user.usercenter.model.request;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;

public class UserRegisterLogin implements Serializable {

    private static final long serialVersionUID = -4603584520782275869L;

    private String userAccount;

    private String userPassword;

    public String getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(String userAccount) {
        this.userAccount = userAccount;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

}
