package com.user.usercenter.model.request;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;

public class UserRegisterRequest implements Serializable {

    private static final long serialVersionUID = -8674164519322959100L;
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

    public String getCheckPassword() {
        return checkPassword;
    }

    public void setCheckPassword(String checkPassword) {
        this.checkPassword = checkPassword;
    }

    private String checkPassword;

}
