package com.runner.sportsmeter.models;

import com.parse.*;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by angelr on 01-Dec-15.
 */
@ParseClassName("Segments")
public class Segments extends ParseObject {
    public Segments() {
    }

    public ParseUser getCurrentUser() {
        return getParseUser("currentUser");
    }

    public void setCurrentUser(ParseUser currentUser) {
        put("currentUser", currentUser);
    }

    public int getSegmentId() {
        return getInt("segmentId");
    }

    public void setSegmentId(int segmentId) {
        put("segmentId", segmentId);
    }

    public String getName() {
        return getString("segmentName");
    }

    public void setName(String name) {
        put("segmentName", name);
    }

//    public ParseFile getMapImage() {
//        return getParseFile("mapImage");
//    }

    public void setMapImage(ParseFile mapImage) {
        put("mapImage", mapImage);
    }

    //    public ArrayList<ParseGeoPoint> getGeoPointsArray() {
//        return get("geoPoints");
//    }
    public ArrayList<ParseGeoPoint> getGeoPointsArray() {
        return (ArrayList<ParseGeoPoint>) get("geoPoints");
    }

    public void setGeoPointsArray(ArrayList<ParseGeoPoint> geoPoints) {
        addAll("geoPoints", geoPoints);
    }

    public Date getCreatedAt() {
        return getDate("createdAt");
    }

    public static ParseQuery<Segments> getQuery() {
        return ParseQuery.getQuery(Segments.class);
    }

    public void setDistance(double dist) {
        put("distance", dist);
    }

    public Double getDistance() {
        return getDouble("distance");
    }
}
