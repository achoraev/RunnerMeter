package com.newrunner.sportsmeter.models;

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
    public Number getMaxSpeed() {
        return getDouble("maxSpeed");
    }

    public Number getAverageSpeed() {
        return getDouble("averageSpeed");
    }

    public Number getDistance() {
        return getDouble("distance");
    }

    public Number getDuration() {
        return getDouble("duration");
    }

    public Number getTimePerKilometer() {
        return getDouble("timePerKilometer");
    }

    public ParseUser getAuthor() {
        return getParseUser("username");
    }

    public Date getCreatedAt(){
        return getDate("createdAt");
    }

    public static ParseQuery<Sessions> getQuery() {
        return ParseQuery.getQuery(Sessions.class);
    }
}
