package com.runner.sportsmeter.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.runner.sportsmeter.enums.UserMetrics;

import java.util.Date;

/**
 * Created by angelr on 02-Sep-15.
 */
@ParseClassName("Account")
public class Account extends ParseObject {

//    private String userName;
//    private String name;
//    private String Email;
//    private ParseUser currentUser;
//    private Boolean isVerified;
//    private Double userWeight;
//    private Double userHeight;
//    private Date memberSince;
//    private UserMetrics usersMetricsUnits;

    public Account (){}

    public ParseUser getCurrentUser() {
        return getParseUser("user");
    }

    public void setCurrentUser(ParseUser currentUser) {
        put("user", currentUser);
    }

    public Double getUserWeight() {
        return getDouble("userWeight");
    }

    public void setUserWeight(Double userWeight) {
        put("userWeight", userWeight);
    }

    public Double getUserHeight() {
        return getDouble("userHeight");
    }

    public void setUserHeight(Double userHeight) {
        put("userHeight", userHeight);
    }

    public Date getMemberSince() {
        return getDate("memberSince");
    }

    public void setMemberSince(Date memberSince) {
        put("memberSince", memberSince);
    }

    public UserMetrics getUsersMetricsUnits() {
        return UserMetrics.valueOf(getString("userMetrics"));
    }

    public void setUsersMetricsUnits(UserMetrics usersMetricsUnits) {
        put("userMetrics", usersMetricsUnits.toString());
    }

    public String getName() {
        return getString("name");
    }

    public void setName(String name) {
        put("name", name);
    }

    public String getEmail() {
        return getString("email");
    }

    public void setEmail(String eMail) {
        put("email", eMail);
    }

    public Boolean getIsVerified() {
        return getBoolean("emailVerified");
    }

    public void setIsVerified(Boolean isVerified) {
        put("emailVerified", isVerified);
    }

    public static ParseQuery<Account> getQuery() {
        return ParseQuery.getQuery(Account.class);
    }
}
