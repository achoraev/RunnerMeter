package com.newrunner.googlemap;

import com.parse.ParseUser;

/**
 * Created by angelr on 27-Jul-15.
 */
public class Session {

    private ParseUser currentUser = null;
    private double maxSpeed = 0;
    private double averageSpeed = 0;
    private double distance = 0;
    private double duration = 0;
    private double timePerKilometer = 0;

    public Session() {

    }

    public ParseUser getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(ParseUser currentUser) {
        this.currentUser = currentUser;
    }

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(double maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public double getAverageSpeed() {
        return averageSpeed;
    }

    public void setAverageSpeed(double averageSpeed) {
        this.averageSpeed = averageSpeed;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public double getTimePerKilometer() {
        return timePerKilometer;
    }

    public void setTimePerKilometer(double distance, double duration) {
        this.timePerKilometer = calculateTimePerKilometer(distance, duration);
    }

    private double calculateTimePerKilometer(double distance, double duration) {
        double result = ((duration / 1000 / 60) / (distance / 1000));
        double finalResult = result - (result % 1);
        if(result % 1 != 0) {
            finalResult += 0.60 * (result % 1);
        } else if(result % 1 == 0.60) {
            finalResult += 1.00;
        }

        finalResult = Math.round(finalResult * 100);
        return finalResult / 100;
    }
}
