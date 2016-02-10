package com.runner.sportsmeter.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.runner.sportsmeter.enums.Gender;
import com.runner.sportsmeter.enums.SportTypes;
import com.runner.sportsmeter.enums.UserMetrics;

import java.util.Date;

/**
 * Created by angelr on 02-Sep-15.
 */
@ParseClassName("Account")
public class Account extends ParseObject {

    public Account (){}

    public ParseUser getCurrentUser() {
        return getParseUser("user");
    }

    public void setCurrentUser(ParseUser currentUser) {
        put("user", currentUser);
    }

    public String getFacebookId() {
        return getString("facebookId");
    }

    public void setFacebookId(String id) {
        put("facebookId", id);
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

    public Gender getGender() {
        return Gender.valueOf(getString("gender"));
    }

    public void setGender(Gender gender) {
        put("gender", gender.toString());
    }

    public SportTypes getSportType() {
        return SportTypes.valueOf(getString("sportType"));
    }

    public void setSportType(SportTypes type) {
        put("sportType", type.toString());
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
        return getBoolean("isVerified");
    }

    public void setIsVerified(Boolean isVerified) {
        put("isVerified", isVerified);
    }

    public static ParseQuery<Account> getQuery() {
        return ParseQuery.getQuery(Account.class);
    }
}
