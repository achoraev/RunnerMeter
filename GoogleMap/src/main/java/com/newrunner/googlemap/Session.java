package com.newrunner.googlemap;

import com.parse.ParseUser;

/**
 * Created by angelr on 27-Jul-15.
 */
public class Session {

    private ParseUser currentUser = null;
    private long maxSpeed = 0;
    private long averageSpeed = 0;
    private long distance = 0;
    private long duration = 0;
    private long timePerKilometer = 0;

    public Session() {

    }

    public ParseUser getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(ParseUser currentUser) {
        this.currentUser = currentUser;
    }

    public long getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(long maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public long getAverageSpeed() {
        return averageSpeed;
    }

    public void setAverageSpeed(long averageSpeed) {
        this.averageSpeed = averageSpeed;
    }

    public long getDistance() {
        return distance;
    }

    public void setDistance(long distance) {
        this.distance = distance;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getTimePerKilometer() {
        return timePerKilometer;
    }

    public void setTimePerKilometer(long distance, long duration) {
        this.timePerKilometer = calculateTimePerKilometer(distance, duration);
    }

    private long calculateTimePerKilometer(long distance, long duration) {
        long result = ((duration / 60) / (distance / 1000));
        return result;
    }
}
