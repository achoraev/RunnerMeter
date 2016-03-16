package com.runner.sportsmeter.models;

import com.runner.sportsmeter.interfaces.UserMetrics;

/**
 * Created by Angel Raev on 16-Mar-16
 */
public class Imperial implements UserMetrics {
    public static final String SPEED_UNIT = "mph";
    public static final String LENGHT_UNIT = "ft";
    public static final String WEIGHT_UNIT = "pound";

    @Override
    public String getSpeedUnit() {
        return SPEED_UNIT;
    }

    @Override
    public String getLenghtUnit() {
        return LENGHT_UNIT;
    }

    @Override
    public String getWeightUnit() {
        return WEIGHT_UNIT;
    }
}
