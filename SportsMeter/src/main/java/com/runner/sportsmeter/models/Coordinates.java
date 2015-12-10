package com.runner.sportsmeter.models;

import com.parse.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by angelr on 22-Oct-15.
 */
@ParseClassName("Coordinates")
public class Coordinates extends ParseObject {

    public Coordinates(){}

    public ParseUser getCurrentUser() {
        return getParseUser("user");
    }

    public void setCurrentUser(ParseUser currentUser) {
        put("user", currentUser);
    }

    public ParseGeoPoint getStartAndEndPoint() {
        return getParseGeoPoint("startAndEndPoint");
    }

    public void setStartAndEndPoint(ParseGeoPoint startAndEndPoint) {
        put("startAndEndPoint", startAndEndPoint);
    }

    public List<Object> getStartAndEndCoordinates() {
        return getList("startAndEndCoordinates");
    }

    public void setStartAndEndCoordinates(ArrayList<ParseGeoPoint> geoPoints) {
        addAll("startAndEndCoordinates", geoPoints);
    }

    public void setAcl(ParseACL acl) {
        put("ACL", acl);
    }

    public Date getCreatedAt(){
        return getDate("createdAt");
    }

    public static ParseQuery<Coordinates> getQuery() {
        return ParseQuery.getQuery(Coordinates.class);
    }
}
