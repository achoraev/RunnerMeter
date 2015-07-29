package com.newrunner.googlemap;

import android.location.Location;
import com.google.android.gms.maps.model.LatLng;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by angelr on 29-Jul-15.
 */
public class Calculations {
    private static double maxSpeed;

    public static double getMaxSpeed() {
        return maxSpeed;
    }

    public static double calculateDistance(LatLng lastUpdatedCoord, LatLng currentCoordinates) {
        float[] result = new float[4];
        Location.distanceBetween(lastUpdatedCoord.latitude,
                lastUpdatedCoord.longitude,
                currentCoordinates.latitude,
                currentCoordinates.longitude, result);
        return result[0];
    }

    public static double calculateSpeed(Long time, Double distance) {
        double result;
        result = (distance / 1000) / (time / (60 * 60 * 1000) % 24);

        return result;
    }

    public static long calculateTime(String lastUpdateTime, String startTime) throws ParseException {
        Date lastDate = new SimpleDateFormat("HH:mm:ss").parse(lastUpdateTime);
        Date startDate = new SimpleDateFormat("HH:mm:ss").parse(startTime);

        long diff = lastDate.getTime() - startDate.getTime();

        return diff;
    }

    public static String convertTimeToString(long diff) {
        long diffSeconds = diff / 1000 % 60;
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000) % 24;

        String result = diffHours + "h:" + diffMinutes + "m:" + diffSeconds + "s";
        return result;
    }

    public static double calculateMaxSpeed(double currentSpeed) {
        if(currentSpeed > getMaxSpeed()){
            maxSpeed = currentSpeed;
        }

        return maxSpeed;
    }
}
