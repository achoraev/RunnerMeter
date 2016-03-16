package com.runner.sportsmeter.models;

import com.runner.sportsmeter.interfaces.UserMetricsInterface;

/**
 * Created by Angel Raev on 16-Mar-16
 */
public class Metrics implements UserMetricsInterface {
    public static final String SPEED_UNIT = "km/h";
    public static final String LENGHT_UNIT = "m";
    public static final String LENGHT_UNIT_KM = "km";
    public static final String WEIGHT_UNIT = "kg";


    @Override
    public String getSpeedUnit() {
        return SPEED_UNIT;
    }

    @Override
    public String getLengthUnit() {
        return LENGHT_UNIT;
    }

    @Override
    public String getDistanceUnit() {
        return LENGHT_UNIT_KM;
    }

    @Override
    public String getWeightUnit() {
        return WEIGHT_UNIT;
    }
}
