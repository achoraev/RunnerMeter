package com.runner.sportsmeter.models;

import com.runner.sportsmeter.interfaces.UserMetricsInterface;

/**
 * Created by Angel Raev on 16-Mar-16
 */
public class Imperial implements UserMetricsInterface {
    public static final String SPEED_UNIT = "mph";
    public static final String LENGHT_UNIT = "ft";
    public static final String LENGHT_UNIT_MILE = "mile";
    public static final String WEIGHT_UNIT = "pound";

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
        return LENGHT_UNIT_MILE;
    }

    @Override
    public String getWeightUnit() {
        return WEIGHT_UNIT;
    }
}
