package com.runner.sportsmeter.enums;

/**
 * Created by angelr on 01-Sep-15.
 */
public enum SportTypes {
    CHOOSE_SPORT(0, "choose"),
    RUNNING(1, "running"),
    DRIVING(2, "driving"),
    CYCLING(3, "cycling"),
    BIKING(4, "biking"),
    CLIMBING(5, "climbing"),
    HIKING(6, "hiking"),
    WALKING(7, "walking");

    private static final int MAX_SPEED_RUNNER = 44;
    private static final int MAX_SPEED_BIKER = 133;
    private static final int MAX_SPEED_DRIVER = 350;
    private int val;
    private String strVal;

    SportTypes(int val, String strVal) {
        this.val = val;
        this.strVal = strVal;
    }

    public int getIntValue(String val){
        switch (val) {
            case "CHOOSE_SPORT":
                return CHOOSE_SPORT.val;
            case "RUNNING":
                return RUNNING.val;
            case "DRIVER":
                return DRIVING.val;
            case "CYCLING":
                return CYCLING.val;
            case "BIKING":
                return BIKING.val;
            case "CLIMBING":
                return CLIMBING.val;
            case "HIKING":
                return HIKING.val;
            case "WALKING":
                return WALKING.val;
        }
        throw new RuntimeException("Sport Type value not supported " + val);
    }

    public SportTypes getSportTypeValue(int val){
        switch (val) {
            case 0:
                return CHOOSE_SPORT;
            case 1:
                return RUNNING;
            case 2:
                return DRIVING;
            case 3:
                return CYCLING;
            case 4:
                return BIKING;
            case 5:
                return CLIMBING;
            case 6:
                return HIKING;
            case 7:
                return WALKING;
        }
        throw new RuntimeException("Sport Type value not supported ");
    }

    public int getMaxSpeed(SportTypes type){
        switch (type.toString()) {
            case "BIKING":
                return MAX_SPEED_BIKER;
            case "CYCLING":
                return MAX_SPEED_BIKER;
            case "RUNNING":
                return MAX_SPEED_RUNNER;
            case "DRIVING":
                return MAX_SPEED_DRIVER;
            default: return 0;
        }
    }
}
