package com.newrunner.googlemap;

/**
 * Created by angelr on 02-Sep-15.
 */
public class Account {

    private String userName;
    private String name;
    private String eMail;
    private Boolean isVerified;
    private String createdAt;

    public Account (){

    }

    public Account (String user, String nam, String mail, Boolean verified, String created){
        this.userName = user;
        this.name = nam;
        this.eMail = mail;
        this.isVerified = verified;
        this.createdAt = created;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String geteMail() {
        return eMail;
    }

    public void seteMail(String eMail) {
        this.eMail = eMail;
    }

    public Boolean getIsVerified() {
        return isVerified;
    }

    public void setIsVerified(Boolean isVerified) {
        this.isVerified = isVerified;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
