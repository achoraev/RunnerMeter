package com.runner.sportsmeter.common;

import android.location.Location;
import android.util.Log;
import com.google.android.gms.maps.model.LatLng;
import com.runner.sportsmeter.enums.SportTypes;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by angelr on 29-Jul-15.
 */
public class Calculations {

    private static final int ONE_HUNDRED = 100;
    private static final int SIXTY = 60;
    private static final int TWENTY_FOUR = 24;
    private static final int ONE_THOUSAND = 1000;
    private static final int MAX_SPEED_RUNNER = 44;
    private static final int MAX_SPEED_BIKER = 133;
    private static final int MAX_SPEED_DRIVER = 350;
    private static final double BEST_TIME_RUNNER = 1.36;
    private static final double BEST_TIME_BIKER = 0.45;
    private static final double BEST_TIME_DRIVER = 0.17;

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
        return (Math.round(result[0] * ONE_HUNDRED)) / ONE_HUNDRED;
    }

    public static double calculateSpeed(Long time, Double distance) {
        double result = 0;
        if (time > 0) {
            result = (distance / ONE_THOUSAND) / (Double.valueOf(time) / (SIXTY * SIXTY * ONE_THOUSAND) % TWENTY_FOUR);
            result = roundToTwoDigitsAfterDecimalPoint(result);
        }
        Log.d("time", String.valueOf(time));
        Log.d("distance", String.valueOf(distance));
        Log.d("result", String.valueOf(result));
        return result;
    }

    public static long calculateTime(String lastUpdateTime, String startTime) {
        Date lastDate = null;
        try {
            lastDate = new SimpleDateFormat("HH:mm:ss").parse(lastUpdateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date startDate = null;
        try {
            startDate = new SimpleDateFormat("HH:mm:ss").parse(startTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return lastDate.getTime() - startDate.getTime();
    }

    public static String convertTimeToString(long diff) {
        long diffSeconds = diff / ONE_THOUSAND % SIXTY;
        long diffMinutes = diff / (SIXTY * ONE_THOUSAND) % SIXTY;
        long diffHours = diff / (SIXTY * SIXTY * ONE_THOUSAND) % TWENTY_FOUR;

        return diffHours + "h:" + diffMinutes + "m:" + diffSeconds + "s";
    }

    public static double calculateMaxSpeed(double currentSpeed, SportTypes runner) {
        if (currentSpeed > getMaxSpeed()) {
            if (runner == SportTypes.RUNNER && currentSpeed <= MAX_SPEED_RUNNER) {
                maxSpeed = roundToTwoDigitsAfterDecimalPoint(currentSpeed);
            } else if (runner == SportTypes.BIKER && currentSpeed <= MAX_SPEED_BIKER) {
                maxSpeed = roundToTwoDigitsAfterDecimalPoint(currentSpeed);
            } else if (runner == SportTypes.DRIVER && currentSpeed <= MAX_SPEED_DRIVER) {
                maxSpeed = roundToTwoDigitsAfterDecimalPoint(currentSpeed);
            }
        }
        Log.d("cur", String.valueOf(currentSpeed));
        Log.d("max", String.valueOf(maxSpeed));
        return maxSpeed;
    }

    public double calculateTimePerKilometer(double distance, double duration) {
        double result = ((duration / ONE_THOUSAND / SIXTY) / (distance / ONE_THOUSAND));
        double finalResult = result - (result % 1);
        if(result % 1 != 0) {
            finalResult += 0.60 * (result % 1);
        }

        finalResult = Math.round(finalResult * ONE_HUNDRED);
        return finalResult / ONE_HUNDRED;
    }

    public static double roundToTwoDigitsAfterDecimalPoint(double in) {
        double result = (Math.round(in * ONE_HUNDRED)) / 100.00;
        return result;
    }

    public Boolean isTimePerKilometerValid(double timePerKilometer, String sportType) {
        switch (sportType){
            case "BIKER" :
                if(timePerKilometer <= BEST_TIME_BIKER){
                    return false;
                }
                break;
            case "RUNNER" :
                if(timePerKilometer <= BEST_TIME_RUNNER){
                    return false;
                }
                break;
            case "DRIVER" :
                if(timePerKilometer <= BEST_TIME_DRIVER){
                    return false;
                }
                break;
        }
        return true;
    }
}
