package com.runner.sportsmeter.models;

import android.os.Parcel;
import android.os.Parcelable;
import com.parse.ParseUser;

/**
 * Created by angelr on 27-Jul-15.
 */
public class Session implements Parcelable
//        extends ParseObject
{

    private ParseUser currentUser;
    private double maxSpeed;
    private double averageSpeed;
    private double distance;
    private double duration;
    private double timePerKilometer;
    private String userName;
    private String createdAt;
    private String sportType;

    public Session(double dis, double dur, double max, double avr, ParseUser curUser){
        this.distance = dis;
        this.duration = dur;
        this.maxSpeed = max;
        this.averageSpeed = avr;
        setTimePerKilometer(dis, dur);
        this.currentUser = curUser;
    }

    public Session(double distance, double duration, double maxSpeed, double averageSpeed, String createdAt, ParseUser currentUser) {
        this(distance, duration, maxSpeed, averageSpeed, currentUser);
        this.createdAt = createdAt;
    }

    public Session(double distance, double duration, double maxSpeed, double averageSpeed, String createdAt, ParseUser currentUser, String username) {
        this(distance, duration, maxSpeed, averageSpeed, createdAt, currentUser);
        this.userName = setUserName(username);
    }

    public Session(double distance, double duration, double maxSpeed, double averageSpeed, double timePer,
                   String createdAt, ParseUser currentUser, String username, String type) {
        this(distance, duration, maxSpeed, averageSpeed, createdAt, currentUser);
        this.timePerKilometer = timePer;
        this.userName = setUserName(username);
        this.sportType = type;
    }

    public String getSportType() {
        return this.sportType;
    }

    public String getUserName() {
        return this.userName;
    }

    public String setUserName(String users) {
        String user = (users != null ? users : "Anonymous");
        return user;
    }

    public String getCreatedAt() {
        return this.createdAt;
    }

    public ParseUser getCurrentUser() {
        return this.currentUser;
    }

    public double getMaxSpeed() {
        return this.maxSpeed;
    }

    public double getAverageSpeed() {
        return this.averageSpeed;
    }

    public double getDistance() {
        return this.distance;
    }

    public double getDuration() {
        return this.duration;
    }

    public double getTimePerKilometer() {
        return this.timePerKilometer;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(maxSpeed);
        dest.writeDouble(averageSpeed);
        dest.writeDouble(distance);
        dest.writeDouble(duration);
        dest.writeDouble(timePerKilometer);
        dest.writeString(createdAt);
        dest.writeString(userName);
    }

    public static final Parcelable.Creator<Session> CREATOR
            = new Parcelable.Creator<Session>() {
        public Session createFromParcel(Parcel in) {
            return new Session(in);
        }

        public Session[] newArray(int size) {
            return new Session[size];
        }
    };

    private Session(Parcel in) {
        maxSpeed = in.readDouble();
        averageSpeed = in.readDouble();
        distance = in.readDouble();
        duration = in.readDouble();
        timePerKilometer = in.readDouble();
        createdAt = in.readString();
        userName = in.readString();
    }
}