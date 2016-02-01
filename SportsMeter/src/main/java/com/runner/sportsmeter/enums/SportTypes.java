package com.runner.sportsmeter.enums;

/**
 * Created by angelr on 01-Sep-15.
 */
public enum SportTypes {
    BIKER(0, "biker"),
    RUNNER(1, "runner"),
    DRIVER(2, "driver");

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
            case "BIKER":
                return BIKER.val;
            case "RUNNER":
                return RUNNER.val;
            case "DRIVER":
                return DRIVER.val;
        }
        throw new RuntimeException("Sport Type value not supported " + val);
    }

    public SportTypes getSportTypeValue(int val){
        switch (val) {
            case 0:
                return BIKER;
            case 1:
                return RUNNER;
            case 2:
                return DRIVER;
        }
        throw new RuntimeException("Sport Type value not supported ");
    }

    public int getMaxSpeed(SportTypes type){
        switch (type.toString()) {
            case "BIKER":
                return MAX_SPEED_BIKER;
            case "RUNNER":
                return MAX_SPEED_RUNNER;
            case "DRIVER":
                return MAX_SPEED_DRIVER;
        }
        throw new RuntimeException("Sport Type value not supported ");
    }
}
