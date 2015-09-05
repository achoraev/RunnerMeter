package com.newrunner.googlemap;

/**
 * Created by angelr on 02-Sep-15.
 */
public class Account {

    private String userName;
    private String name;
    private String Email;
    private Boolean isVerified;
    private String createdAt;

    public Account (){

    }

    public Account (String user, String nam, String mail, Boolean verified, String created){
        this.userName = user;
        this.name = nam;
        this.Email = mail;
        this.isVerified = setIsVerified(verified);
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

    public String getEmail() {
        return Email;
    }

    public void setEmail(String eMail) {
        this.Email = eMail;
    }

    public Boolean getIsVerified() {
        return isVerified;
    }

    public Boolean setIsVerified(Boolean isVerified) {
        return isVerified != null ? isVerified: false;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
