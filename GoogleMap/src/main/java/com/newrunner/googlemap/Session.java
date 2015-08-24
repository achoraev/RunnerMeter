package com.newrunner.googlemap;

import android.os.Parcel;
import android.os.Parcelable;
import com.parse.ParseUser;

import java.util.Date;

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
    private Date createdAt;

    public Session() {
        this(0, 0, 0, 0, null);
    }

    public Session(double dis, double dur, double max, double avr, ParseUser curUser){
        this.distance = dis;
        this.duration = dur;
        this.maxSpeed = max;
        this.averageSpeed = avr;
        setTimePerKilometer(dis, dur);
        this.currentUser = curUser;
    }

    public Session(double distance, double duration, double maxSpeed, double averageSpeed, Date createdAt, ParseUser currentUser) {
        this(distance, duration, maxSpeed, averageSpeed, currentUser);
        this.createdAt = createdAt;
    }

    public Date getCreatedAt() {
        return createdAt;
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
//        dest.writeString(String.valueOf(createdAt));
//        dest.writeParcelable(currentUser, flags);
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
//        currentUser = in.readParcelable(ParseUser.class.getClassLoader());
    }
}
