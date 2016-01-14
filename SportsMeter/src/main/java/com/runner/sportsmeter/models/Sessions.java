package com.runner.sportsmeter.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Date;

/**
 * Created by angelr on 21-Aug-15.
 */
@ParseClassName("Sessions")
public class Sessions extends ParseObject {

    public Sessions(){}

    public Double getMaxSpeed() {
        return getDouble("maxSpeed");
    }

    public void setMaxSpeed(Double max) {
        put("maxSpeed", max);
    }

    public Double getAverageSpeed() {
        return getDouble("averageSpeed");
    }

    public void setAverageSpeed(Double avr) {
        put("averageSpeed", avr);
    }

    public Double getDistance() {
        return getDouble("distance");
    }

    public void setDistance(Double dist) {
        put("distance", dist);
    }

    public Long getDuration() {
        return getLong("duration");
    }

    public void setDuration(Long dur) {
        put("duration", dur);
    }

    public Double getTimePerKilometer() {
        return getDouble("timePerKilometer");
    }

    public void setTimePerKilometer(Double pace) {
        put("timePerKilometer", pace);
    }

    public ParseUser getParseUser() {
        return getParseUser("username");
    }

    public void setParseUser(ParseUser user) {
        put("username", user);
    }

    public String getName() {
        return getString("name");
    }

    public void setName(String name) {
        put("name", name);
    }

    public String getSportType() {
        return getString("sportType");
    }

    public void setSportType(String name) {
        put("sportType", name);
    }

    public Date getCreatedAt(){
        long createdAt = getLong("createdAt");
        return createdAt > 0L?new Date(createdAt):null;
    }

//    public String getObjectId(){
//        return getString("objectId");
//    }

    public static ParseQuery<Sessions> getQuery() {
        return ParseQuery.getQuery(Sessions.class);
    }
}
