package com.newrunner.googlemap;

import android.location.Location;
import android.util.Log;
import com.google.android.gms.maps.model.LatLng;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by angelr on 29-Jul-15.
 */
public class Calculations {
    private static double maxSpeed = 0;

    public static void setMaxSpeed(double speed) {
        maxSpeed = speed;
    }

    public static double getMaxSpeed() {
        return maxSpeed;
    }

    public static double calculateDistance(LatLng lastUpdatedCoord, LatLng currentCoordinates) {
        float[] result = new float[4];
        Location.distanceBetween(lastUpdatedCoord.latitude,
                lastUpdatedCoord.longitude,
                currentCoordinates.latitude,
                currentCoordinates.longitude, result);
        return (Math.round(result[0] * 100))/100;
    }

    public static double calculateSpeed(Long time, Double distance) {
        double result = 0;
        if (time > 0) {
            result = (distance / 1000) / (Double.valueOf(time) / (60 * 60 * 1000) % 24);
            result = roundToTwoDigitsAfterDecimalPoint(result);
        }
        Log.d("time", String.valueOf(time));
        Log.d("distance", String.valueOf(distance));
        Log.d("result", String.valueOf(result));
        return result;
    }

    public static long calculateTime(String lastUpdateTime, String startTime) throws ParseException {
        Date lastDate = new SimpleDateFormat("HH:mm:ss").parse(lastUpdateTime);
        Date startDate = new SimpleDateFormat("HH:mm:ss").parse(startTime);

        return lastDate.getTime() - startDate.getTime();
    }

    public static String convertTimeToString(long diff) {
        long diffSeconds = diff / 1000 % 60;
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000) % 24;

        return diffHours + "h:" + diffMinutes + "m:" + diffSeconds + "s";
    }

    public static double calculateMaxSpeed(double currentSpeed) {
        if(currentSpeed > getMaxSpeed()){
            maxSpeed = roundToTwoDigitsAfterDecimalPoint(currentSpeed);
        }
        Log.d("cur", String.valueOf(currentSpeed));
        Log.d("max", String.valueOf(maxSpeed));
        return maxSpeed;
    }

    public static double roundToTwoDigitsAfterDecimalPoint(double in){
        double result = (Math.round(in * 100))/100.00;
        return result;
    }
}
