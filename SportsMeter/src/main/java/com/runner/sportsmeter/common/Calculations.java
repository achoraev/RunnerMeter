package com.runner.sportsmeter.common;

import android.location.Location;
import android.util.Log;
import com.google.android.gms.maps.model.LatLng;
import com.runner.sportsmeter.enums.SportTypes;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by angelr on 29-Jul-15.
 */
public class Calculations {


    public double calculateDistance(LatLng lastUpdatedCoord, LatLng currentCoordinates) {
        float[] result = new float[4];
        Location.distanceBetween(lastUpdatedCoord.latitude,
                lastUpdatedCoord.longitude,
                currentCoordinates.latitude,
                currentCoordinates.longitude, result);
        return (Math.round(result[0] * Constants.ONE_HUNDRED)) / Constants.ONE_HUNDRED;
    }

    public static double calculateSpeed(Long time, Double distance) {
        double result = 0;
        if (time > 0 && distance > 0.0 && time > 1500) {
            result = (distance / Constants.ONE_THOUSAND) / (Double.valueOf(time) / (Constants.SIXTY * Constants.SIXTY * Constants.ONE_THOUSAND) % Constants.TWENTY_FOUR);
            result = roundToTwoDigitsAfterDecimalPoint(result);
        }
        Log.i(Constants.TAG, String.valueOf(time) + "time");
        Log.i(Constants.TAG, String.valueOf(distance) + "distance");
        Log.i(Constants.TAG, String.valueOf(result) + "speed");
        if (result > 1.0) {
            return result;
        } else {
            result = 0;
            return result;
        }
    }

    public static long calculateTime(String lastUpdateTime, String startTime) {
        Date lastDate = null;
        Date startDate = null;
        try {
            lastDate = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).parse(lastUpdateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            startDate = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).parse(startTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return lastDate.getTime() - startDate.getTime();
    }

    public static String convertTimeToString(long diff) {
        long diffSeconds = diff / Constants.ONE_THOUSAND % Constants.SIXTY;
        long diffMinutes = diff / (Constants.SIXTY * Constants.ONE_THOUSAND) % Constants.SIXTY;
        long diffHours = diff / (Constants.SIXTY * Constants.SIXTY * Constants.ONE_THOUSAND) % Constants.TWENTY_FOUR;

        return diffHours + "h:" + diffMinutes + "m:" + diffSeconds + "s";
    }

    public static double calculateMaxSpeed(double currentSpeed, double currentMaxSpeed, SportTypes type) {
        double maxSpeed = roundToTwoDigitsAfterDecimalPoint(currentMaxSpeed);
        if (currentSpeed > 1.0 && currentSpeed > maxSpeed) {
            if (SportTypes.RUNNING.equals(type) && currentSpeed <= type.getMaxSpeed(type)) {
                maxSpeed = roundToTwoDigitsAfterDecimalPoint(currentSpeed);
            } else if (SportTypes.CYCLING.equals(type) && currentSpeed <= type.getMaxSpeed(type)) {
                maxSpeed = roundToTwoDigitsAfterDecimalPoint(currentSpeed);
            } else if (SportTypes.CLIMBING.equals(type) && currentSpeed <= type.getMaxSpeed(type)) {
                maxSpeed = roundToTwoDigitsAfterDecimalPoint(currentSpeed);
            } else if (SportTypes.WALKING.equals(type) && currentSpeed <= type.getMaxSpeed(type)) {
                maxSpeed = roundToTwoDigitsAfterDecimalPoint(currentSpeed);
            } else {
                maxSpeed = roundToTwoDigitsAfterDecimalPoint(currentSpeed);
            }
        }

        Log.i(Constants.TAG, String.valueOf(currentSpeed) + "current speed");
        Log.i(Constants.TAG, String.valueOf(maxSpeed) + "max speed");
        return maxSpeed;
    }

    public double calculateTimePerKilometer(double distance, Long duration) {
        double result = ((duration / Constants.ONE_THOUSAND / Constants.SIXTY) / (distance / Constants.ONE_THOUSAND));
        double finalResult = result - (result % 1);
        if (result % 1 != 0) {
            finalResult += 0.60 * (result % 1);
        }

        finalResult = Math.round(finalResult * Constants.ONE_HUNDRED);
        return finalResult / Constants.ONE_HUNDRED;
    }

    public static double roundToTwoDigitsAfterDecimalPoint(double in) {
        return roundDigitsAfterDecimalPoint(in, 2);
    }

    public static double roundDigitsAfterDecimalPoint(double in, int digits) {
        Double round = Math.pow(10, digits);
        return (Math.round(in * round)) / round;
    }

    public Boolean isTimePerKilometerValid(double timePerKilometer, String sportType) {
        Boolean isTimePerKilometerValid = true;
        switch (sportType) {
            case "WALKING":
                isTimePerKilometerValid = timePerKilometer >= Constants.BEST_TIME_WALKING;
                break;
            case "CYCLING":
                isTimePerKilometerValid = timePerKilometer >= Constants.BEST_TIME_BIKER;
                break;
            case "RUNNING":
                isTimePerKilometerValid = timePerKilometer >= Constants.BEST_TIME_RUNNER;
                break;
            case "CLIMBING":
                isTimePerKilometerValid = true;
                break;
        }
        return isTimePerKilometerValid;
    }

    public static double convertDoubleToTime(double distance) {
        return distance - (distance % 1) + (0.60 * (distance % 1));
    }
}
